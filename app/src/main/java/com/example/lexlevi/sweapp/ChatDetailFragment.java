package com.example.lexlevi.sweapp;

import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lexlevi.sweapp.Common.Constants;
import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Interfaces.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.Chat;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.Message;
import com.example.lexlevi.sweapp.Singletons.Sockets;
import com.example.lexlevi.sweapp.Singletons.Session;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatDetailFragment extends Fragment {

    public static final String CHAT_ITEM = "chat";
    public static final String GROUP_ITEM = "group";

    private Chat _chat;
    private Group _group;
    private Socket _socket;
    private View _rView;
    private TextView _noMessagesView;
    private MessagesListAdapter<Message> _chatAdapter;

    public ChatDetailFragment() {

    }

    // Fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(CHAT_ITEM)) {
            _chat = (Chat) getActivity().getIntent().getSerializableExtra(CHAT_ITEM);
            _group = (Group) getActivity().getIntent().getSerializableExtra(GROUP_ITEM);

            _socket = Sockets.shared().getSocket();
            _socket.on(Constants.sEventNewMessage, onNewMessage);
            if (_socket.connected())
                _socket.emit(Constants.sEventJoinChat, _chat.getId());

            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity()
                    .findViewById(R.id.chat_detail_toolbar_layout);
            String expandTitle = " # " + _chat.getName();
            if (collapsingToolbarLayout != null)
                collapsingToolbarLayout.setTitle(expandTitle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);
        _rView = rootView;
        setUpMessageView();
        setUpInputView();
        return rootView;
    }

    // View Setup
    public void setUpMessageView() {
        final AVLoadingIndicatorView messagesAvi = (AVLoadingIndicatorView) _rView.findViewById(R.id.avi_messages);
        messagesAvi.show();
        final TextView noMessages = (TextView) _rView.findViewById(R.id.text_no_messages);
        _noMessagesView = noMessages;
        noMessages.setVisibility(View.GONE);
        RelativeLayout messagesListContainer = (RelativeLayout) _rView.findViewById(R.id.message_list_container);
        final MessagesList messagesList = (MessagesList) messagesListContainer.findViewById(R.id.messagesList);
        final MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(Session
                .shared()
                .user()
                .getId(), null);
        _chatAdapter = adapter;
        messagesList.setAdapter(adapter);
        // Get latest Messages from Server
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatServerAPI chatServerAPI = retrofit.create(ChatServerAPI.class);
        Call<List<Message>> call = chatServerAPI.getMessagesForChat(_chat.getId());
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                switch (response.code()) {
                    case 200:
                        Collections.reverse(response.body());
                        messagesAvi.hide();
                        adapter.addToEnd(response.body(), true);
                        break;
                    case 404:
                        noMessages.setVisibility(View.VISIBLE);
                        messagesAvi.hide();
                        break;
                }

            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                messagesAvi.hide();
                Log.d("ERROR: ", t.toString());
            }
        });
    }

    public void setUpInputView() {
        final MessageInput inputView = (MessageInput) _rView.findViewById(R.id.input);
        inputView.setInputListener(new MessageInput.InputListener() {

            @Override
            public boolean onSubmit(CharSequence input) {
                Message m = new Message();
                Gson g = new Gson();
                m.setAuthor(Session
                .shared()
                .user());
                m.setBody(input.toString());
                m.setChat(_chat.getId());
                m.setCreatedAt(new Date());
                m.setUpdatedAt(new Date());
                _socket.emit(Constants.sEventNewMessage,
                        _chat.getId(),
                        g.toJson(m));
                return true;
            }
        });
    }

    // Socket Events
    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _noMessagesView.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    Message m = gson.fromJson((String) args[0], Message.class);
                    _chatAdapter.addToStart(m, true);
                }
            });
        }
    };
}
