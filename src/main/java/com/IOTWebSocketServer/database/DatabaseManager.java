package com.IOTWebSocketServer.database;

import java.sql.*;

/**
 * Created by Mateusz on 05/10/2016.
 */
public class DatabaseManager {
    //  Database credentials
    private Connection mConnection;
    private final String DB_URL;
    private final String USER;
    private final String PASS;

    public DatabaseManager() {

        this.DB_URL = "jdbc:mysql://localhost:3306/IOT?useSSL=false";
        this.USER = "iotadmin";
        this.PASS = "iot";
    }

    private Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            if (mConnection == null || !mConnection.isValid(1)) {
                mConnection = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            mConnection = null;
        }
        return mConnection;
    }

    public boolean authenticateUser(String userEmail, String password) {
        boolean userAutentication = false;
        try {
            Connection conn = getConnection();
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String query = "SELECT count(userEmail) FROM User WHERE userEmail = \"" + userEmail + "\" AND userPassword = \"" + password + "\";";
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                int count = rs.getInt(1);
                System.out.println("email count is "+count);
                stmt.close();
                conn.close();
                userAutentication = count == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("user autenticated" + userAutentication);
        return userAutentication;
    }

    public int getUserID(String userEmail) {
        int userID=0;
        try {
            Connection conn = getConnection();
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String query = "SELECT userID FROM IOT.User WHERE userEmail = \"" + userEmail + "\";";
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                userID = rs.getInt(1);
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("user id " + userID);
        return userID;
    }

    public int registerNewDevice(String deviceType,String deviceDescription, int userID) {
        int deviceID=0;
        Connection conn = getConnection();
        String query = "INSERT INTO Devices (deviceType,deviceDescription,userID) " +
                "VALUES (?,?,?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, deviceType);
            preparedStatement.setString(2, deviceDescription);
            preparedStatement.setInt(3, userID);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                deviceID = rs.getInt(1);
            }
            rs.close();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("deivceID "+ deviceID);
        return deviceID;
    }
}