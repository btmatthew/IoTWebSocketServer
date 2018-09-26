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
        this.USER = "";
        this.PASS = "";
    }

    protected Connection getConnection() {
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


}