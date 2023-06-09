package com.g16.roborallyserver.sessionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ConnectionManager
 * Static class to help manage connections.
 * Can be expanded in the future.
 */

public class ConnectionManager {
    static final List<Connection> connectionList = new ArrayList<>();

    public static void addConnection(Connection conn){
        connectionList.add(conn);
    }

}
