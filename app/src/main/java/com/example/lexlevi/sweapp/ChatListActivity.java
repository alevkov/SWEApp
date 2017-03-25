package com.example.lexlevi.sweapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.example.lexlevi.sweapp.Common.Constants;
import com.example.lexlevi.sweapp.Models.Chat;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Singletons.ChatServerClient;
import com.example.lexlevi.sweapp.Singletons.SocketConnector;
import com.example.lexlevi.sweapp.Singletons.UserSession;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Chats. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ChatDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ChatListActivity extends AppCompatActivity {

    private boolean _twoPane; // won't really be used
    private Socket _socket;

    public Group _group;
    public List<User> _groupUsers;
    public ArrayList<String> _directChatUsers;

    public RecyclerView _chatListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up socket events
        _socket = SocketConnector.getInstance().getSocket();
        _socket.on(Constants.sEvenUserJoin, onUserJoin);
        _socket.on(Constants.sEventUpdateUsers, onUpdateUsers);
        // get selected group
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_chat_list);
        if (_group == null)
            _group = (Group) getIntent().getSerializableExtra(ChatDetailFragment.GROUP_ITEM);
        // view setup
        setTitle(_group.getName());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //.. for creating new chat
            }
        });

        View recyclerView = findViewById(R.id.chat_list);
        _chatListView = (RecyclerView) recyclerView;
        recyclerView.setBottom(3);
        _directChatUsers = new ArrayList<>();
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.chat_detail_container) != null) {
            _twoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {
        Call<List<Chat>> callChats = ChatServerClient
                .getInstance()
                .api()
                .getChatListForGroup(_group.getId());
        callChats.enqueue(new Callback<List<Chat>>() { // call to load group chats
            @Override
            public void onResponse(Call<List<Chat>> call, final Response<List<Chat>> chatResponse) {
                switch (chatResponse.code()) {
                    case 200:
                        Call<List<User>> callUsers = ChatServerClient
                                .getInstance()
                                .api()
                                .getGroupUsersList(_group.getId());
                        callUsers.enqueue(new Callback<List<User>>() { // call to load users chained after loading chats
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> userResponse) {
                                switch (userResponse.code()) {
                                    case 200:
                                        _groupUsers = userResponse.body();
                                        recyclerView.setAdapter(new ChatRecyclerViewAdapter(chatResponse.body(),
                                                userResponse.body()));
                                        break;
                                }
                            }
                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) { // fail to load users
                                Log.d("ERROR: ", t.toString());
                            }
                        });
                        break;
                    case 404:
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) { // fail to load chats

            }
        });
    }

    // Socket events
    private Emitter.Listener onUserJoin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("USER JOIN: ", args[0] + "just joined!");
                }
            });
        }
    };

    private Emitter.Listener onUpdateUsers = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("UPDATE USERS: ", args[0] + "");
                }
            });
        }
    };

    // Adapter
    public class ChatRecyclerViewAdapter
            extends SectionedRecyclerViewAdapter<ChatRecyclerViewAdapter.ChatListVH>
            implements PopupMenu.OnMenuItemClickListener {

        private final List<Chat> _chats;
        private final List<Chat> _groupChats;
        private final List<Chat> _directChats;
        private final List<User> _users;

        public ChatRecyclerViewAdapter(List<Chat> chats, List<User> users) {
            _chats = chats;
            _groupChats = new ArrayList<>();
            _directChats = new ArrayList<>();
            _users = users;
            for (Chat c : _chats) {
                if (c.getIsGroupMessage()) {
                    _groupChats.add(c);
                } else {
                    if (c.getParticipants().size() == 1
                            && !c.getParticipants().get(0).equals(UserSession
                            .getInstance()
                            .getCurrentUser()
                            .getId())) {
                        continue;
                    }
                    _directChats.add(c);
                }
            }
        }

        @Override
        public ChatListVH onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout;
            switch (viewType) {
                case VIEW_TYPE_HEADER:
                    layout = R.layout.chat_list_header;
                    break;
                case VIEW_TYPE_ITEM:
                    layout = R.layout.chat_list_content;
                    break;
                default:
                    layout = R.layout.chat_list_content;
                    break;
            }
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(layout, parent, false);
            return new ChatListVH(view);
        }

        @Override
        public void onBindHeaderViewHolder(ChatListVH holder, int section) {
            switch (section) {
                case 0:
                    holder._title.setText("channels");
                    holder._addPrivateChannel.setVisibility(View.GONE);
                    break;
                case 1:
                    holder._title.setText("direct messaging");
                    holder._addPrivateChannel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final PopupMenu popupMenu = new PopupMenu(ChatListActivity.this, v);
                            popupMenu.setOnMenuItemClickListener(ChatRecyclerViewAdapter.this);
                            popupMenu.inflate(R.menu.menu_group_users);
                            int userId = 0;
                            for (User u : _groupUsers) {
                                popupMenu.getMenu().add(0, userId, userId++, u.getName());
                            }
                            popupMenu.show();
                        }
                    });
                    break;
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            String myId = UserSession
                    .getInstance()
                    .getCurrentUser()
                    .getId();
            if (_groupUsers.get(item.getOrder()).getId().equals(myId) ||
                    _directChatUsers.contains(_groupUsers.get(item.getOrder()).getId())) {
                return false;
            }
            Chat directChat = new Chat();
            List<String> p = Arrays.asList(myId, _groupUsers.get(item.getOrder()).getId());
            directChat.setParticipants(p);
            directChat.setIsGroupMessage(false);
            directChat.setGroup(_group.getId());
            Call<Chat> createDirectChat = ChatServerClient
                    .getInstance()
                    .api()
                    .createChatForGroup(
                            directChat,
                            _group.getId());
            createDirectChat.enqueue(new Callback<Chat>() {
                @Override
                public void onResponse(Call<Chat> call, Response<Chat> response) {
                    setupRecyclerView(_chatListView);
                }

                @Override
                public void onFailure(Call<Chat> call, Throwable t) {

                }
            });
            return false;
        }

        @Override
        public void onBindViewHolder(final ChatListVH holder,
                                     int position,
                                     int relativePosition,
                                     int absolutePosition) {
            switch (position) {
                case 0:
                    holder._chat = _groupChats.get(relativePosition);
                    holder._idView.setText("#");
                    holder._contentView.setText(_groupChats.get(relativePosition).getName());
                    holder._view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (_twoPane) {
                                // Not implemented yet
                                // Bundle arguments = new Bundle();
                                // arguments.putSerializable(ChatDetailFragment.CHAT_ITEM, holder._chat);
                                // ChatDetailFragment fragment = new ChatDetailFragment();
                                // fragment.setArguments(arguments);
                                // getSupportFragmentManager().beginTransaction()
                                //     .replace(R.id.chat_detail_container, fragment)
                                //     .commit();
                            } else {
                                Context context = v.getContext();
                                Intent intent = new Intent(context, ChatDetailActivity.class);
                                intent.putExtra(ChatDetailFragment.CHAT_ITEM, holder._chat);
                                intent.putExtra(ChatDetailFragment.GROUP_ITEM, _group);
                                startActivity(intent);
                            }
                        }
                    });
                    break;
                case 1:
                    holder._idView.setText("@");
                    String myId = UserSession.getInstance().getCurrentUser().getId();
                    List<String> chatUsers = _directChats.get(relativePosition).getParticipants();
                    if (chatUsers.size() == 1 && chatUsers.get(0).equals(myId) ||
                            chatUsers.size() > 1 && chatUsers.contains(myId)) {
                        holder._chat = _directChats.get(relativePosition);
                        if (chatUsers.size() == 1) {
                            holder._chat.setName("@me");
                            holder._contentView.setText(UserSession
                                    .getInstance()
                                    .getCurrentUser()
                                    .getUserName());
                        } else {
                            String theirId = chatUsers.get(0).equals(myId) ? chatUsers.get(1) : chatUsers.get(0);
                            for (User u : _users) {
                                if (theirId.equals(u.getId())) {
                                    _directChatUsers.add(u.getId());
                                    holder._chat.setName("@" + u.getName());
                                    holder._contentView.setText(u.getName());
                                }
                            }
                        }
                    }
                    holder._view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (_twoPane) {
                                // Not implemented yet
                                // Bundle arguments = new Bundle();
                                // arguments.putSerializable(ChatDetailFragment.CHAT_ITEM, holder._chat);
                                // ChatDetailFragment fragment = new ChatDetailFragment();
                                // fragment.setArguments(arguments);
                                // getSupportFragmentManager().beginTransaction()
                                //     .replace(R.id.chat_detail_container, fragment)
                                //     .commit();
                            } else {
                                Context context = v.getContext();
                                Intent intent = new Intent(context, ChatDetailActivity.class);
                                intent.putExtra(ChatDetailFragment.CHAT_ITEM, holder._chat);
                                intent.putExtra(ChatDetailFragment.GROUP_ITEM, _group);
                                startActivity(intent);
                            }
                        }
                    });
                    // Self-chat
                    // has only 1 participant, the current user

                    break;
                default:
                    break;
            }
        }

        @Override
        public int getSectionCount() {
            return 2; // number of sections.
        }

        @Override
        public int getItemCount(int section) {
            switch (section) {
                case 0:
                    return _groupChats.size();
                case 1:
                    return _directChats.size();
                default:
                    return 0;
            }
        }

        public class ChatListVH extends RecyclerView.ViewHolder {
            public final View _view;

            public final TextView _idView;
            public final TextView _contentView;
            public final TextView _title;
            public final Button _addPrivateChannel;

            public Chat _chat;

            public ChatListVH(View view) {
                super(view);
                _view = view;
                _idView = (TextView) view.findViewById(R.id.chat_item_label);
                _contentView = (TextView) view.findViewById(R.id.chat_item_content);
                _title = (TextView) view.findViewById(R.id.chat_header_title);
                _addPrivateChannel = (Button) view.findViewById(R.id.add_private_channel);
            }

            @Override
            public String toString() {
                return super.toString() + "'" + _contentView.getText() + "'";
            }
        }
    }


}