package com.IOTWebSocketServer.model;

public class User {


    private String action;
    private int userID;
    private String userEmail;
    private String password;
    private String userName;
    private String userToken;

    public User(String userEmail, String password, String userName) {
        this.userEmail = userEmail;
        this.password = password;
        this.userName = userName;
    }

    public void purgeConfidentialData(){
        this.password="";
        this.userEmail="";
        this.userID=-1;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
