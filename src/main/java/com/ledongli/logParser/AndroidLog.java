package com.ledongli.logParser;

import com.ledongli.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingjiu on 11/11/15.
 */
public class AndroidLog implements ILocationParser {

    @Override
    public List<Location> parseLocations(String logFile) {
        List<String> list = LogPickers.getLogWithoutTag(logFile, "xxxxxx,");
        List<Location> locations = new ArrayList<Location>();

        for (String str : list) {
            String locationStr[] = str.trim().split(",");
            Location location = new Location();
            location.setLongitude(Double.parseDouble(locationStr[0]));
            location.setLatitude(Double.parseDouble(locationStr[1]));
            location.setAccuracy(Float.parseFloat(locationStr[2]));
            location.setSpeed(Float.parseFloat(locationStr[3]));
            location.setTime((long) (Double.parseDouble(locationStr[4]) * 1000));

            locations.add(location);
        }

        return locations;
    }
}
