package com.ledongli;

import com.ledongli.util.GeomTransform;

/**
 * Created by xingjiu on 11/11/15.
 */
public class Location {
    double longitude;
    double latitude;
    float accuracy;
    float speed;
    float course;
    long time;
    GeomTransform geomTransform = new GeomTransform();

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getCourse() {
        return course;
    }

    public void setCourse(float course) {
        this.course = course;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double distanceTo(Location otherLoc) {
        return geomTransform.getDistance(this.getLongitude(), this.getLatitude(), otherLoc.getLongitude(), otherLoc.getLatitude());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getLongitude()).append("\t");
        sb.append(this.getLatitude()).append("\t");
        sb.append(this.getAccuracy()).append("\t");
        sb.append(this.getSpeed()).append("\t");
        sb.append(this.getTime());
        return sb.toString();
    }
}