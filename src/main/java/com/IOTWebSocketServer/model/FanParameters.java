package com.IOTWebSocketServer.model;

public class FanParameters {

    private boolean fanStatus;
    private int fanSpeed;
    private int fanMode;
    private boolean rotation;
    private boolean ion;

    public boolean isFanStatus() {
        return fanStatus;
    }

    public void setFanStatus(boolean fanStatus) {
        this.fanStatus = fanStatus;
    }

    public int getFanSpeed() {
        return fanSpeed;
    }

    public void setFanSpeed(int fanSpeed) {
        this.fanSpeed = fanSpeed;
    }

    public int getFanMode() {
        return fanMode;
    }

    public void setFanMode(int fanMode) {
        this.fanMode = fanMode;
    }

    public boolean isRotation() {
        return rotation;
    }

    public void setRotation(boolean rotation) {
        this.rotation = rotation;
    }

    public boolean isIon() {
        return ion;
    }

    public void setIon(boolean ion) {
        this.ion = ion;
    }
}
