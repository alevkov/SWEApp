package com.example.lexlevi.sweapp.Singletons;

import com.example.lexlevi.sweapp.Common.URLs;
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

    public Socket getSocket() {
        if (!_socket.connected()) {
            connect();
            Gson g = new Gson();
            _socket.emit("userJoined",
                    g.toJson(Session.shared().user()));
        }

        return _socket;
    }

    public void connect() {
        _socket.connect();
    }
}