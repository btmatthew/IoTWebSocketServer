package com.IOTWebSocketServer.database;


import com.IOTWebSocketServer.token.RandomString;

import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;

public class DevicesDatabaseManager extends DatabaseManager {


    public boolean authenticateUser(String userName, String userToken) {

        boolean userAuthentication = false;

        Connection conn = getConnection();
        String sql ="SELECT count(userName) FROM User WHERE userName = ? AND userToken = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            stmt.setString(2, userToken);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            System.out.println("email count is "+count);
            stmt.close();
            conn.close();
            userAuthentication = count == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userAuthentication;
    }

    public boolean authenticateServer(String serverID) {

        boolean serverAutentication = false;

        Connection conn = getConnection();
        String sql ="SELECT count(ServerID) FROM Servers WHERE ServerID = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, serverID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            stmt.close();
            conn.close();
            serverAutentication = count == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serverAutentication;
    }



    public boolean authenticateDevice(String deviceId) {
        boolean deviceAuthentication = false;
        Connection conn = getConnection();
        String sql ="SELECT count(idDevices) FROM Devices WHERE idDevices = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, deviceId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            System.out.println("email count is "+count);
            stmt.close();
            conn.close();
            deviceAuthentication = count == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return deviceAuthentication;
    }


    public int getUserID(String userName) {
        int userID=0;

        Connection conn = getConnection();
        String sql ="SELECT userID FROM IOT.User WHERE userName = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            userID = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userID;
    }

    public String registerNewDevice(String deviceType,String deviceDescription, int userID) {
        String deviceID="";
        Connection conn = getConnection();
        String query = "INSERT INTO Devices (idDevices,deviceType,deviceDescription,userID) " +
                "VALUES (?,?,?,?)";
        RandomString gen = new RandomString(20, ThreadLocalRandom.current());
        PreparedStatement preparedStatement = null;
        try {
            String tempDeviceID = gen.nextString();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,tempDeviceID);
            preparedStatement.setString(2, deviceType);
            preparedStatement.setString(3, deviceDescription);
            preparedStatement.setInt(4, userID);
            int i = preparedStatement.executeUpdate();

            if (i > 0) {
                deviceID = tempDeviceID;
            } else {
                deviceID = "databaseError";
            }
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deviceID;
    }
}
