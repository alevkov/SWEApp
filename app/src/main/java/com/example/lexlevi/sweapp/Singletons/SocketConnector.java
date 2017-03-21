package com.example.lexlevi.sweapp.Singletons;

import com.example.lexlevi.sweapp.Common.URLs;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;

public class SocketConnector {
    private static SocketConnector instance = null;
    private Socket _socket;
    {
        try {
            _socket = IO.socket(URLs.BASE_API);
        } catch (URISyntaxException e) {}
    }

    protected SocketConnector() {
        // Exists only to defeat instantiation.
    }

    public static SocketConnector getInstance() {
        if (instance == null) {
            instance = new SocketConnector();
        }
        return instance;
    }

    public Socket getSocket() {
        if (!_socket.connected()) {
            connect();
            Gson g = new Gson();
            _socket.emit("userJoined",
                    g.toJson(UserSession.getInstance().getCurrentUser()));
        }

        return _socket;
    }

    public void connect() {
        _socket.connect();
    }
}