package com.example.lexlevi.sweapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.example.lexlevi.sweapp.Models.Event;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.Message;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Singletons.Client;
import com.example.lexlevi.sweapp.Singletons.Sockets;
import com.example.lexlevi.sweapp.Singletons.Session;
import com.github.clans.fab.FloatingActionMenu;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListActivity extends AppCompatActivity {

    public Group _group;
    public List<User> _groupUsers;
    public List<Event> _groupReminders;
    public ArrayList<String> _directChatUsers;

    public RecyclerView _chatListView;
    public FloatingActionMenu _floatingMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get selected group
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_chat_list);
        if (_group == null)
            _group = (Group) getIntent().getSerializableExtra(ChatDetailFragment.GROUP_ITEM);
        // view setup
        Sockets.shared().getSocket(_group.getId()).on(Constants.sEventUserJoin, onUserJoin);
        setTitle(_group.getName());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        View recyclerView = findViewById(R.id.chat_list);
        _chatListView = (RecyclerView) recyclerView;
        recyclerView.setBottom(3);
        _directChatUsers = new ArrayList<>();
        setupRecyclerView((RecyclerView) recyclerView);
        loadRemindersForGroup();
        Sockets.shared().socket().on(Constants.sEventUpdateUsers, onUpdateUsers);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setupRecyclerView(_chatListView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sockets.shared().socket().off(Constants.sEventUserJoin, onUserJoin);
        Sockets.shared().socket().off(Constants.sEventUpdateUsers, onUpdateUsers);
        Sockets.shared().disconnect();
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {
        loadChatsForGroup(recyclerView);
    }

    private void loadChatsForGroup(@NonNull final RecyclerView recyclerView) {
        Call<List<Chat>> callChats = Client.shared().api().getChatListForGroup(_group.getId());
        callChats.enqueue(new Callback<List<Chat>>() { // call to load group chats
            @Override
            public void onResponse(Call<List<Chat>> call, final Response<List<Chat>> chatResponse) {
                switch (chatResponse.code()) {
                    case 200:
                        loadUsersForGroup(recyclerView, chatResponse.body());
                        joinChatsLoaded(chatResponse.body());
                        break;
                    case 404:
                        Snackbar s;
                        s = Snackbar.make(recyclerView,
                                "Oops! Something went wrong",
                                Snackbar.LENGTH_LONG);
                        s.getView().setBackgroundColor(getResources()
                                .getColor(R.color.excitedColor));
                        s.show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) { // fail to load chats
                Snackbar s;
                s = Snackbar.make(recyclerView,
                        "Oops! Something went wrong",
                        Snackbar.LENGTH_LONG);
                s.getView().setBackgroundColor(getResources()
                        .getColor(R.color.excitedColor));
                s.show();
            }
        });
    }

    private void loadUsersForGroup(@NonNull final RecyclerView recyclerView, final List<Chat> chatResponse) {
        Call<List<User>> callUsers = Client.shared().api().getGroupUsersList(_group.getId());
        callUsers.enqueue(new Callback<List<User>>() { // call to load users chained after loading chats
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> userResponse) {
                switch (userResponse.code()) {
                    case 200:
                        _groupUsers = userResponse.body();
                        recyclerView.setAdapter(new ChatRecyclerViewAdapter(chatResponse,
                                userResponse.body()));
                        FloatingActionButton chatFab = (FloatingActionButton) findViewById(R.id.chat_list_fab_chat);
                        chatFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ChatListActivity.this,
                                        CreateChatActivity.class);
                                ArrayList<User> users = (ArrayList<User>) _groupUsers;
                                intent.putExtra("groupUsers", users);
                                intent.putExtra("groupId", _group.getId());
                                startActivity(intent);
                            }
                        });
                        FloatingActionButton eventFab = (FloatingActionButton) findViewById(R.id.chat_list_fab_event);
                        eventFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ChatListActivity.this,
                                        CreateEventActivity.class);
//                                ArrayList<User> users = (ArrayList<User>) _groupUsers;
//                                intent.putExtra("groupUsers", users);
                                intent.putExtra("groupId", _group.getId());
                                startActivity(intent);
                            }
                        });
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) { // fail to load users
                Snackbar s;
                s = Snackbar.make(recyclerView,
                        "Oops! Something went wrong",
                        Snackbar.LENGTH_LONG);
                s.getView().setBackgroundColor(getResources()
                        .getColor(R.color.excitedColor));
                s.show();
            }
        });

    }

    public void loadRemindersForGroup() {
        Call<List<Event>> callEvents = Client.shared().api().getEventsListForGroup(_group.getId());
        callEvents.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                switch (response.code()) {
                    case 200:
                        _groupReminders = response.body();
                        Log.d("REMINDERS", response.body() + "");
                        int i = 0;
                        displayEvents(i);
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Snackbar s;
                s = Snackbar.make(_chatListView,
                        "Oops! Something went wrong",
                        Snackbar.LENGTH_LONG);
                s.getView().setBackgroundColor(getResources()
                        .getColor(R.color.excitedColor));
                s.show();
            }
        });
    }

    // Reminder UI
    public void displayEvents(final int index) {
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy")
                .format(_groupReminders.get(index).getDueDate());
        Alerter.create(ChatListActivity.this)
                .setTitle(_groupReminders.get(index).getName())
                .setText("Due " + formattedDate)
                .setBackgroundColor(R.color.excitedColor)
                .setOnHideListener(new OnHideAlertListener() {
                    @Override
                    public void onHide() {
                        int i = index;
                        if (i < _groupReminders.size()) {
                            displayEvents(++i);
                        }
                    }
                })
                .show();
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

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson parser = new Gson();
                    Message m = parser.fromJson((String) args[0], Message.class);
                }
            });
        }
    };

    private void joinChatsLoaded(List<Chat> chats) {
        for (Chat c : chats) {
            Sockets.shared().getSocket(_group.getId()).emit(Constants.sEventJoinChat, c.getId());
        }
        Sockets.shared().getSocket(_group.getId()).on(Constants.sEventNewMessage, onNewMessage);
    }

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
                    if (c.getParticipants().size() == 1 &&
                            !c.getParticipants().get(0).equals(Session
                            .shared()
                            .user()
                            .getId())) {
                        continue;
                    } else if (c.getParticipants().size() > 1 &&
                            !c.getParticipants().contains(Session
                            .shared()
                            .user()
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
        public boolean onMenuItemClick(final MenuItem item) {
            String myId = Session.shared().user().getId();
            if (_groupUsers.get(item.getOrder()).getId().equals(myId) ||
                    _directChatUsers.contains(_groupUsers.get(item.getOrder()).getId())) {
                return false;
            }
            Chat directChat = new Chat();
            List<String> p = Arrays.asList(myId, _groupUsers.get(item.getOrder()).getId());
            directChat.setParticipants(p);
            directChat.setIsGroupMessage(false);
            directChat.setGroup(_group.getId());
            Call<Chat> createDirectChat = Client.shared().api()
                    .createChatForGroup(
                            directChat,
                            _group.getId());
            createDirectChat.enqueue(new Callback<Chat>() {
                @Override
                public void onResponse(Call<Chat> call, Response<Chat> response) {
                    // open the newly created directed chat
                    Context context = getApplicationContext();
                    Intent intent = new Intent(context, ChatDetailActivity.class);
                    response.body().setName("@" + _groupUsers.get(item.getOrder()).getName());
                    intent.putExtra(ChatDetailFragment.CHAT_ITEM, response.body());
                    intent.putExtra(ChatDetailFragment.GROUP_ITEM, _group);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Chat> call, Throwable t) {

                }
            });
            return false;
        }

        @Override
        public void onBindViewHolder(final ChatListVH holder, int position, int relativePosition, int absolutePosition) {
            switch (position) {
                case 0:
                    holder._chat = _groupChats.get(relativePosition);
                    holder._idView.setText("#");
                    holder._contentView.setText(_groupChats.get(relativePosition).getName());
                    holder._view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, ChatDetailActivity.class);
                            intent.putExtra(ChatDetailFragment.CHAT_ITEM, holder._chat);
                            intent.putExtra(ChatDetailFragment.GROUP_ITEM, _group);
                            startActivity(intent);
                        }
                    });
                    break;
                case 1:
                    holder._idView.setText("@");
                    String myId = Session.shared().user().getId();
                    List<String> chatUsers = _directChats.get(relativePosition).getParticipants();
                    if (chatUsers.size() == 1 && chatUsers.get(0).equals(myId) ||
                            chatUsers.size() > 1 && chatUsers.contains(myId)) {
                        holder._chat = _directChats.get(relativePosition);
                        if (chatUsers.size() == 1) {
                            holder._chat.setName("@me");
                            holder._contentView.setText(Session.shared().user().getUserName());
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
                            Context context = v.getContext();
                            Intent intent = new Intent(context, ChatDetailActivity.class);
                            intent.putExtra(ChatDetailFragment.CHAT_ITEM, holder._chat);
                            intent.putExtra(ChatDetailFragment.GROUP_ITEM, _group);
                            startActivity(intent);
                        }
                    });
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
