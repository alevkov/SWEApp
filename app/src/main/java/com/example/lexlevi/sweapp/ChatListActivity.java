package com.example.lexlevi.sweapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.example.lexlevi.sweapp.Common.Constants;
import com.example.lexlevi.sweapp.Models.Chat;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Singletons.ChatServerClient;
import com.example.lexlevi.sweapp.Singletons.SocketConnector;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

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

    private boolean _twoPane; // won't really be used2
    private Socket _socket;

    public  Group _group;


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
        recyclerView.setBottom(3);

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
        callChats.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, final Response<List<Chat>> chatResponse) {
                switch (chatResponse.code()) {
                    case 200: // call to load users chained after loading chats
                        Call<List<User>> callUsers = ChatServerClient
                                .getInstance()
                                .api()
                                .getGroupUsersList(_group.getId());
                        callUsers.enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> userResponse) {
                                switch (userResponse.code()) {
                                    case 200:
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
            extends SectionedRecyclerViewAdapter<ChatRecyclerViewAdapter.ChatListVH> {

        private final List<Chat> _chats;
        private final List<User> _users;

        public ChatRecyclerViewAdapter(List<Chat> items, List<User> users) {
            _chats = items;
            _users = users;
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
                    break;
                case 1:
                    holder._title.setText("private messaging");
                    break;
            }
        }

        @Override
        public void onBindViewHolder(final ChatListVH holder,
                                     int position,
                                     int relativePosition,
                                     int absolutePosition) {
            switch (position) {
                case 0:
                    holder._chat = _chats.get(relativePosition);
                    holder._idView.setText("#");
                    holder._contentView.setText(_chats.get(relativePosition).getName());
                    holder._view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (_twoPane) {
                                // Not implemented
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
                    holder._chat = null;
                    holder._idView.setText("@");
                    holder._contentView.setText(_users.get(relativePosition).getUserName());
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
                    return _chats.size();
                case 1:
                    return _users.size();
                default:
                    return 0;
            }
        }

        public class ChatListVH extends RecyclerView.ViewHolder {
            public final View _view;
            public final TextView _idView;
            public final TextView _contentView;
            public final TextView _title;
            public Chat _chat;

            public ChatListVH(View view) {
                super(view);
                _view = view;
                _idView = (TextView) view.findViewById(R.id.id);
                _contentView = (TextView) view.findViewById(R.id.content);
                _title = (TextView) view.findViewById(R.id.chat_header_title);
            }

            @Override
            public String toString() {
                return super.toString() + "'" + _contentView.getText() + "'";
            }
        }
    }


}
