package com.IOTWebSocketServer.database;

import com.IOTWebSocketServer.model.User;
import com.IOTWebSocketServer.token.RandomString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class UserDatabaseManager extends DatabaseManager {


    public User registerUser(User user) {
        if (checkIfEmailUsed(user.getUserEmail())) {
            user.setAction("EmailAddressAlreadyUsed");
        } else if (checkIfUserNameIsTaken(user.getUserName())) {
            user.setAction("UserNameAlreadyUsed");
        } else {
            Connection conn = getConnection();
            String sql = " INSERT INTO User (userEmail, userPassword, userName, userToken)"
                    + " VALUES (?, ?, ?, ?)";
            RandomString gen = new RandomString(8, ThreadLocalRandom.current());

            PreparedStatement preparedStmt;
            try {
                preparedStmt = conn.prepareStatement(sql);
                String tempToken;
                do {
                    tempToken = gen.nextString();
                } while (checkIfTokenIsTaken(tempToken));

                preparedStmt.setString(1, user.getUserEmail());
                preparedStmt.setString(2, user.getPassword());
                preparedStmt.setString(3, user.getUserName());
                preparedStmt.setString(4, tempToken);

                int i = preparedStmt.executeUpdate();

                if (i > 0) {
                    user.setAction("RegistrationSuccessful");
                    user.setUserToken(tempToken);
                } else {
                    user.setAction("DatabaseError");
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        user.purgeConfidentialData();
        return user;
    }

    public User userLogin(User user) {
        String sql = "SELECT userToken, userName FROM IOT.User WHERE userEmail = ? AND userpassword = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUserEmail());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();

            if(rs.isBeforeFirst()){
                System.out.println("is before first");
                rs.next();
                user.setUserToken(rs.getString(1));
                user.setUserName(rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (user.getUserToken()==null || user.getUserToken().equals("")) {
                user.setAction("IncorrectLoginCredentials");
            } else {
                System.out.println("login");
                user.setAction("LoginSuccessful");
            }
            user.purgeConfidentialData();
        }
        return user;
    }

    private boolean checkIfTokenIsTaken(String userToken) {
        String sql = "SELECT count(userEmail) FROM IOT.User WHERE userToken = ?";
        return statementCheck(sql, userToken);
    }

    private boolean checkIfUserNameIsTaken(String userName) {
        String sql = "SELECT count(userEmail) FROM IOT.User WHERE userName = ?";

        //        try {
//            stmt = conn.prepareStatement(sql);
//            stmt.setString(1, userName);
//            ResultSet rs = stmt.executeQuery();
//            rs.next();
//            int count = rs.getInt(1);
//            isUser = count > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return statementCheck(sql, userName);
    }

    private boolean checkIfEmailUsed(String userEmail) {
        String sql = "SELECT count(userEmail) FROM IOT.User WHERE userEmail = ?";

        return statementCheck(sql, userEmail);
//        PreparedStatement stmt = null;
//        try {
//            stmt = conn.prepareStatement(sql);
//            stmt.setString(1, userEmail);
//            ResultSet rs = stmt.executeQuery();
//            rs.next();
//            int count = rs.getInt(1);
//            isUser = count > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return isUser;
    }

    private boolean statementCheck(String sql, String value) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            return count > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
