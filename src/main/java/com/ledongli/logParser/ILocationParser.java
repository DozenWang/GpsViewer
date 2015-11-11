package com.ledongli.logParser;

import com.ledongli.Location;
import java.util.List;

/**
 * Created by xingjiu on 11/11/15.
 */
public interface ILocationParser {

    List<Location> parseLocations(String logFile);
}
