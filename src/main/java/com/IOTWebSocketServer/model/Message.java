package com.IOTWebSocketServer.model;

import java.util.ArrayList;

public class Message {
    private String from;
    private String to;
    private String action;
    //used to identify the session's handler
    private String handlerID;

    private String userName;
    private String userEmail;
    private String password;
    private String userToken;

    private String deviceDescription;
    private String deviceID;
    private String deviceType;

    private String lampStatus;
    private ArrayList<Message> deviceList;

    public void purgeUserData(){
        this.userName="";
        this.userToken="";
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getHandlerID() {
        return handlerID;
    }

    public void setHandlerID(String handlerID) {
        this.handlerID = handlerID;
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

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }


    public String getLampStatus() {
        return lampStatus;
    }

    public void setLampStatus(String lampStatus) {
        this.lampStatus = lampStatus;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Message> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(ArrayList<Message> deviceList) {
        this.deviceList = deviceList;
    }
}
