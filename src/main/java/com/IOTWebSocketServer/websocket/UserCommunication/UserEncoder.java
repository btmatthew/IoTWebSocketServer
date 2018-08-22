package com.IOTWebSocketServer.websocket.UserCommunication;

import com.IOTWebSocketServer.model.Message;
import com.IOTWebSocketServer.model.User;
import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class UserEncoder implements Encoder.Text<User> {

    private static Gson gson = new Gson();

    @Override
    public String encode(User user) throws EncodeException {
        String json = gson.toJson(user);
        return json;
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
