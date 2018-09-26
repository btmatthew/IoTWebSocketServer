package com.IOTWebSocketServer.database;


import com.IOTWebSocketServer.model.Message;
import com.IOTWebSocketServer.token.RandomString;

import java.sql.*;
import java.util.ArrayList;
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
            stmt.close();
            conn.close();
            userAuthentication = count == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userAuthentication;
    }

    public void removeDeviceFromSystem(String deviceID){
        Connection conn = getConnection();
        String sql ="DELETE FROM Devices WHERE idDevices = ?";
        PreparedStatement stmt = null;
        try {

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, deviceID);
            stmt.executeUpdate();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public String updateDeviceName(Message message){
        String actionOutcome="";
        Connection conn = getConnection();
        String query = "update Devices set deviceDescription = ? where idDevices = ?";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, message.getDeviceDescription());
            preparedStatement.setString(2,message.getFrom());

            int i = preparedStatement.executeUpdate();

            if (i > 0) {
                actionOutcome = "deviceNameUpdated";
            } else {
                actionOutcome = "databaseError";
            }
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return  actionOutcome;
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

    public ArrayList<Message> getDeviceList(int userID) {
        ArrayList<Message> deviceList = new ArrayList<>();
        Connection conn = getConnection();
        String sql ="SELECT idDevices,deviceType,deviceDescription FROM Devices WHERE userID = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Message device = new Message();
                device.setDeviceID(rs.getString(1));
                device.setDeviceType(rs.getString(2));
                device.setDeviceDescription(rs.getString(3));
                deviceList.add(device);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deviceList;
    }


}
