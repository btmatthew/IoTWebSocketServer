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

        getConnection();
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

    public boolean autenticateUser(String userName, String password) {
        boolean userAutentication = false;
        try {
            Connection conn = getConnection();
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String query = "SELECT count(USERS.userEmail) FROM IOT.USERS WHERE USERS.userEmail = \"" + userName + "\" AND USERS.userPassword = \"" + password + "\";";
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                int count = rs.getInt(1);
                stmt.close();
                conn.close();
                userAutentication = count == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userAutentication;
    }

//    public void registerNewDevice() {
//        Connection conn = getConnection();
//        String query1 = "INSERT INTO DEVICES (deviceID, deviceType,deviceDescription,userID) " +
//                "VALUES (?,?,?,?)";
//        PreparedStatement preparedStatement1 = null;
//        try {
//            preparedStatement1 = conn.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
//            preparedStatement1.setInt(1, rowID);
//            preparedStatement1.setString(2, userName);
//            preparedStatement1.executeUpdate();
//            preparedStatement1.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}