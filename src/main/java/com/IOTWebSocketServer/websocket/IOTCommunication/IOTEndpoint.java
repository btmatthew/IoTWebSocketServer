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
    public void onOpen(Session session, @PathParam("deviceID") String deviceID) {
        System.out.println("New Device Connected " + deviceID + "\n");
        DevicesDatabaseManager databaseManager = new DevicesDatabaseManager();
        if (databaseManager.authenticateDevice(deviceID)
                || deviceID.contains("newDevice")
                || databaseManager.authenticateServer(deviceID)) {
            System.out.printf("Session created and authenticated %s\n", session.getId());
            //remove any zombie sessions.
            if (iotEndpoints.stream()
                    .anyMatch(x -> deviceID.equals(x.sessionDeviceID))) {
                IOTEndpoint tobeRemoved = getSession(deviceID);
                if(tobeRemoved!=null) {
                    iotEndpoints.remove(tobeRemoved);
                }
            }
            this.session = session;
            iotEndpoints.add(this);
            devices.put(session.getId(), deviceID);
            sessionDeviceID = deviceID;
        }
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
//        System.out.println("from " + message.getFrom());
//        System.out.println("to " + message.getTo());
//        System.out.println("action " + message.getAction());
//        System.out.println("handlerID " + message.getHandlerID());
//        System.out.println("device description " + message.getDeviceDescription());
//        System.out.println("user name " + message.getUserName());
//        System.out.println("token " + message.getUserToken());
//        System.out.println("user name " + message.getUserEmail());
//        System.out.println("password " + message.getPassword());

        DevicesDatabaseManager databaseManager = new DevicesDatabaseManager();
        Message replyMessage = new Message();

        switch (message.getAction()) {
            case "requestDevicesList":
                if (databaseManager.authenticateUser(message)) {
                    System.out.printf("Device list requested by %s\n", message.getUserName());
                    int userID = databaseManager.getUserID(message);
                    ArrayList<Message> deviceList = databaseManager.getDeviceList(userID);
                    message.setAction("deviceList");
                    message.setDeviceList(deviceList);
                    session.getBasicRemote().sendObject(message);
                } else {
                    System.out.printf("Device list requested failed by %s due to incorrect credentials\n", message.getUserName());
                    replyMessage.setAction("IncorrectCredentials");
                    session.getBasicRemote().sendObject(replyMessage);
                }
                break;
            case "registerNewDevice":
                //The system will have to authenticate the user first
                //if the details are correct the device will be registered on the system
                //otherwise the system will reply back to the system to inform that incorrect user details were provided.
                if (databaseManager.authenticateUser(message)) {
                    int userID = databaseManager.getUserID(message);
                    String deviceID = databaseManager.registerNewDevice(message.getDeviceType(), message.getDeviceDescription(), userID);
                    if (!deviceID.equals("databaseError")) {
                        replyMessage.setAction("deviceRegistrationCompleted");
                        replyMessage.setDeviceID(String.valueOf(deviceID));
                        session.getBasicRemote().sendObject(replyMessage);
                        System.out.printf("Device registration sucessful with ID %s\n", String.valueOf(deviceID));
                    } else {
                        System.out.print("Device registration failed due to database error\n");
                        replyMessage.setAction("databaseError");
                        session.getBasicRemote().sendObject(replyMessage);
                    }
                } else {
                    System.out.print("Device authentication failed due to incorrect credentials\n");
                    replyMessage.setAction("registrationUnsuccessful");
                    session.getBasicRemote().sendObject(replyMessage);
                }
                break;

            case "deviceremoved":
                if (databaseManager.authenticateUser(message)) {
                    System.out.printf("Device %s removed from system.", message.getFrom());
                    databaseManager.removeDeviceFromSystem(message.getFrom());
                    replySession(message);
                }
                break;
            case "devicedescriptionupdated":
                if (databaseManager.authenticateUser(message)) {
                    System.out.printf("Device %s name updated to %s \n", message.getFrom(), message.getDeviceDescription());
                    message.setAction(databaseManager.updateDeviceName(message));
                    replySession(message);
                }
                break;
            //default will be called for following requests
//            case "updatedevicedescription":
//            case "removedevice":
//            case "lampstatus":
//            case "lampon":
//            case "lampoff":
//            case "remoteaction":
//            case "deviceStatus":
            default:
                if (databaseManager.authenticateUser(message)) {
                    replySession(message);
                } else {
                    System.out.printf("user %s action %s on device %s failed due to incorrect credentials.\n",
                            message.getUserName(),
                            message.getAction(),
                            message.getTo());
                    replyMessage.setAction("IncorrectCredentials");
                    session.getBasicRemote().sendObject(replyMessage);
                }
        }
    }


    @OnClose
    public void onClose(Session session) {
        System.out.printf("session %s closed for device %s\n", session.getId(), sessionDeviceID);
        System.out.printf("number of sessions left %d \n", iotEndpoints.size());
        iotEndpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.printf("session %s closed for device %s because of an error\n", session.getId(), sessionDeviceID);
        System.out.printf("number of sessions left %d \n", iotEndpoints.size());
        iotEndpoints.remove(this);
    }

    private void replySession(Message message) {
        IOTEndpoint io = getSession(message.getTo());
        try {
            if (io != null) {
                System.out.printf("user %s requested %s from device %s\n",
                        message.getUserName(),
                        message.getAction(),
                        message.getTo());
                message.purgeUserData();
                io.session.getBasicRemote().sendObject(message);
            } else {
                System.out.printf("user %s requested %s but device %s is disconnected.\n",
                        message.getUserName(),
                        message.getAction(),
                        message.getTo());
                message.purgeUserData();
                message.setAction("deviceNotConnectedToSystem");
                session.getBasicRemote().sendObject(message);
            }
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    private static IOTEndpoint getSession(String deviceId) {
        System.out.println("searching for session");
        return iotEndpoints
                .stream()
                .filter(x -> deviceId.equals(x.sessionDeviceID))
                .findAny()
                .orElse(null);
    }
}
