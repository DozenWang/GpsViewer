package com.ledongli.util;

import com.ledongli.Location;

/**
 * Created by xiangying on 11/10/15.
 * desc:
 */
public class GpsValidate<T> {
    private LocationPicker<T> mPicker;
    Location temp1;
    Location temp2;

    public GpsValidate(LocationPicker<T> picker) {
        assert (null != picker);
        mPicker = picker;

        temp1 = new Location();
        temp2 = new Location();
    }

    /**
     * 验证validatePoint是否有效。本方法只是走一个粗略的判断gps是否有效，不能用来做跑步合理性的判断
     *
     * @param lastPoint     上一个gps点，可以为空
     * @param validatePoint 要验证的gps点，不能为空
     * @return 如果验证通过是个不漂移，较合理跑步产生的，返回true，否则返回false
     */
    public boolean validate(T lastPoint, T validatePoint) {

        assert (null != validatePoint);

        // 如果没有精度或者经度超过50米，过滤掉
        if (mPicker.getAccuracy(validatePoint) >= 50) {
            return false;
        }
        // 经纬度都是零，说明是没定到位，直接去掉
        if (mPicker.getLatitude(validatePoint) < 0.01 && mPicker.getLongitude(validatePoint) < 0.01) {
            return false;
        }
        if (null != lastPoint) {
            temp1.setLongitude(mPicker.getLongitude(validatePoint));
            temp1.setLatitude(mPicker.getLatitude(validatePoint));

            temp2.setLongitude(mPicker.getLongitude(lastPoint));
            temp2.setLatitude(mPicker.getLatitude(lastPoint));

            double distance = temp2.distanceTo(temp1);

            // 如果单点speed为零
            if (mPicker.getSpeed(validatePoint) < 0.01) {

                return false;
            }

            long time = Math.abs(mPicker.getTime(validatePoint) - mPicker.getTime(lastPoint));
            if (time <= 1000) { // 如果两个点的时间间隔太短，直接抛弃
                return false;
            }

            double speed = distance * 1000 / time;
            if (speed > 40) {
                return false;
            }
        }

        return true;
    }

    public interface LocationPicker<T> {
        double getLongitude(T obj);

        double getLatitude(T obj);

        // 单位:米/秒
        float getSpeed(T obj);

        // 从1970年开始的[毫秒]数
        long getTime(T obj);

        float getAccuracy(T obj);
    }
}