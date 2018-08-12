package com.IOTWebSocketServer.websocket;

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

import com.IOTWebSocketServer.model.Message;

@ServerEndpoint(value = "/iot/{deviceID}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class IOTEndpoint {
    private Session session;
    private String sessionDeviceID;
    private static final Set<IOTEndpoint> iotEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> devices = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("deviceID") String deviceID) throws IOException, EncodeException {

        this.session = session;
        iotEndpoints.add(this);
        devices.put(session.getId(), deviceID);
        sessionDeviceID=deviceID;
        System.out.println("device " + deviceID);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        System.out.println(message.getFrom());
        System.out.println(message.getTo());
        System.out.println(message.getAction());
        System.out.println(message.getHandlerID());
        System.out.println(message.getDeviceDescription());
        System.out.println(message.getUserName());
        System.out.println(message.getPassword());

        switch(message.getAction()){
            case "registerNewDevice":
                Message replyMessage = new Message();
                replyMessage.setAction("deviceRegistrationCompleted");
                replyMessage.setDeviceID("espLamp1");
                session.getBasicRemote().sendObject(replyMessage);
                //DatabaseManager databaseManager = new DatabaseManager();

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
