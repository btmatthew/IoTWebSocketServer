package com.IOTWebSocketServer.database;

import com.IOTWebSocketServer.model.User;
import com.IOTWebSocketServer.token.RandomString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class UserDatabaseManager extends DatabaseManager {


    public String registerUser(User user) {
        String token = "";

        Connection conn = getConnection();
        String sql = " INSERT INTO User (userEmail, userPassword, userName, userToken)"
                + " VALUES (?, ?, ?, ?)";
        RandomString gen = new RandomString(8, ThreadLocalRandom.current());

        PreparedStatement preparedStmt;
        try {
            preparedStmt = conn.prepareStatement(sql);
            String tempToken = gen.nextString();
            preparedStmt.setString(1, user.getUserEmail());
            preparedStmt.setString(2, user.getPassword());
            preparedStmt.setString(3, user.getUserName());
            preparedStmt.setString(4, tempToken);

            int i = preparedStmt.executeUpdate();

            if (i > 0) {
                token = tempToken;
            } else {
                token = "databaseError";
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    public boolean checkIfUserNameIsTaken(String userName) {
        boolean isUser = false;
        Connection conn = getConnection();
        String sql = "SELECT count(userEmail) FROM IOT.User WHERE userName = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            isUser = count > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUser;
    }


    public boolean checkIfEmailUsed(String userEmail) {
        boolean isUser = false;
        Connection conn = getConnection();
        String sql = "SELECT count(userEmail) FROM IOT.User WHERE userEmail = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            isUser = count > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUser;
    }


}
