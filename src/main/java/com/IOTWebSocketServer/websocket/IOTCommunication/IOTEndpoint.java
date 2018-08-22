package com.IOTWebSocketServer.websocket.IOTCommunication;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.IOTWebSocketServer.database.DevicesDatabaseManager;
import com.IOTWebSocketServer.model.Message;


@ServerEndpoint(value = "/iot/{deviceID}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class IOTEndpoint {
    private Session session;
    private String sessionDeviceID;
    private static final Set<IOTEndpoint> iotEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> devices = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("deviceID") String deviceID) throws IOException, EncodeException {
        System.out.println("New Device Connected " + deviceID);
        DevicesDatabaseManager databaseManager = new DevicesDatabaseManager();
        if (databaseManager.authenticateDevice(deviceID)
                || deviceID.contains("newDevice")
                || databaseManager.authenticateServer(deviceID)) {
            System.out.println("Device authenticated " + deviceID);
            this.session = session;
            iotEndpoints.add(this);
            devices.put(session.getId(), deviceID);
            sessionDeviceID = deviceID;
        }
        System.out.println("device " + deviceID);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        System.out.println("from " + message.getFrom());
        System.out.println("to " + message.getTo());
        System.out.println("action " + message.getAction());
        System.out.println("handlerID " + message.getHandlerID());
        System.out.println("device description " + message.getDeviceDescription());
        System.out.println("user name " + message.getUserName());
        System.out.println("token " + message.getUserToken());
        System.out.println("user name " + message.getUserEmail());
        System.out.println("password " + message.getPassword());
        DevicesDatabaseManager databaseManager = new DevicesDatabaseManager();
        Message replyMessage = new Message();
        switch (message.getAction()) {
            case "registerNewDevice":
                System.out.println("Device registration started " + message.getUserName());
                //The system will have to authenticate the user first
                //if the details are correct the device will be registered on the system
                //otherwise the system will reply back to the system to inform that incorrect user details were provided.
                if (databaseManager.authenticateUser(message.getUserName(), message.getUserToken())) {
                    int userID = databaseManager.getUserID(message.getUserName());
                    String deviceID = databaseManager.registerNewDevice(message.getDeviceType(), message.getDeviceDescription(), userID);
                    if (!deviceID.equals("databaseError")) {
                        replyMessage.setAction("deviceRegistrationCompleted");
                        replyMessage.setDeviceID(String.valueOf(deviceID));
                        session.getBasicRemote().sendObject(replyMessage);
                    } else {
                        replyMessage.setAction("databaseError");
                        session.getBasicRemote().sendObject(replyMessage);
                    }

                } else {
                    replyMessage.setAction("registrationUnsuccessful");
                    session.getBasicRemote().sendObject(replyMessage);
                }
                break;
            case "lampStatus":
            case "lampOn":
            case "lampOff":
                if (databaseManager.authenticateUser(message.getUserName(), message.getUserToken())) {
                    IOTEndpoint io = getSession(message.getTo());
                    if (io != null) {
                        //purging data from packet so it can be reused
                        message.setUserName("");
                        message.setUserToken("");
                        io.session.getBasicRemote().sendObject(message);
                    } else {

                        replyMessage = message;
                        replyMessage.setAction("deviceNotConnectedToSystem");
                        session.getBasicRemote().sendObject(replyMessage);
                    }
                }
                break;


        }

        //IOTEndpoint io = getSession(message.getTo());
        //System.out.println("value send to " + io.session.getId());
        //io.session.getBasicRemote().sendObject(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        iotEndpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static IOTEndpoint getSession(String deviceId) {
        System.out.println("searching for session");
        final IOTEndpoint[] iotEndpoint = {null};
        iotEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                if (Objects.equals(endpoint.sessionDeviceID, deviceId)) {
                    iotEndpoint[0] = endpoint;
                }
            }
        });
        return iotEndpoint[0];
    }

}
