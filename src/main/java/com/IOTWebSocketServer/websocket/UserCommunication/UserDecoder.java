package com.IOTWebSocketServer.websocket.UserCommunication;

import com.IOTWebSocketServer.model.Message;
import com.IOTWebSocketServer.model.User;
import com.google.gson.Gson;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class UserDecoder implements Decoder.Text<User> {

    private static Gson gson = new Gson();

    @Override
    public User decode(String s) throws DecodeException {
        User user = gson.fromJson(s, User.class);
        return user;
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
