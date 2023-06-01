package com.g16.roborallyserver.sessionUtils;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {
    static List<Connection> connectionList = new ArrayList<>();

    public static void addConnection(Connection conn){
        connectionList.add(conn);
    }

}
