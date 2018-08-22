package com.IOTWebSocketServer.websocket.UserCommunication;


import com.IOTWebSocketServer.database.DevicesDatabaseManager;
import com.IOTWebSocketServer.database.UserDatabaseManager;
import com.IOTWebSocketServer.model.User;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/user/{serverID}", decoders = UserDecoder.class, encoders = UserEncoder.class)
public class UserEndPoint {

    private Session session;
    private String sessionDeviceID;


    @OnOpen
    public void onOpen(Session session, @PathParam("serverID") String serverID) throws IOException, EncodeException {
        System.out.println("New Device Connected " + serverID);
        DevicesDatabaseManager databaseManager = new DevicesDatabaseManager();

        if (databaseManager.authenticateServer(serverID)) {
            System.out.println("Server authenticated " + serverID);
            this.session = session;
            sessionDeviceID = serverID;
        }
        System.out.println("device " + serverID);
    }

    @OnMessage
    public void onMessage(Session session, User user) throws IOException, EncodeException {
        UserDatabaseManager userDatabaseManager = new UserDatabaseManager();
        System.out.println(user.getUserName());
        System.out.println(user.getPassword());
        System.out.println(user.getUserEmail());
        System.out.println(user.getAction());
        System.out.println(user.getToken());
        System.out.println(user.getUserID());
        switch (user.getAction()) {
            case "registerUser":
                boolean isEmailUsed = userDatabaseManager.checkIfEmailUsed(user.getUserEmail());
                boolean isUserNameUsed = userDatabaseManager.checkIfUserNameIsTaken(user.getUserName());
                if (isEmailUsed) {
                    user.setAction("EmailAddressAlreadyUsed");
                } else if (isUserNameUsed) {
                    user.setAction("UserNameAlreadyUsed");
                } else {
                    String token = userDatabaseManager.registerUser(user);
                    if (token.equals("databaseError")) {
                        user.setAction(token);
                    } else {
                        user.setAction("RegistrationSuccessful");
                        user.setToken(token);
                    }
                }
                session.getBasicRemote().sendObject(user);
                session.close();
                break;
            //todo in future include items like updating password and recovering emails
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }


}
