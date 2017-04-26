package com.example.lexlevi.sweapp.Singletons;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Models.Group;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;

public class Sockets {
    private static Sockets instance = null;
    private Socket _socket;
    {
        try {
            _socket = IO.socket(URLs.BASE_API);
        } catch (URISyntaxException e) {}
    }

    protected Sockets() {
        // Exists only to defeat instantiation.
    }

    public static Sockets shared() {
        if (instance == null) {
            instance = new Sockets();
        }
        return instance;
    }

    public Socket getSocket(String groupId) {
        if (!_socket.connected()) {
            connect();
            Gson parser = new Gson();
            _socket.emit("userJoined",
                    parser.toJson(Session.shared().user()),
                    groupId);
        }

        return _socket;
    }

    public void connect() {
        _socket.connect();
    }

    public void disconnect() { _socket.off(); _socket.disconnect(); instance = null; _socket = null; }
}