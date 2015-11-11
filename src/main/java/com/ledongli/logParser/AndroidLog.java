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
            location.setLon(Double.parseDouble(locationStr[0]));
            location.setLat(Double.parseDouble(locationStr[1]));
            location.setAccuracy(Double.parseDouble(locationStr[2]));
            location.setSpeed(Double.parseDouble(locationStr[3]));
            location.setTime((long) Double.parseDouble(locationStr[4]) * 1000);

            locations.add(location);
        }

        return locations;
    }
}
