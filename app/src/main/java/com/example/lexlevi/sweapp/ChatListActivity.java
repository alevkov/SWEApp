package com.example.lexlevi.sweapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;


import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Controllers.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.Chat;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Singletons.SocketConnector;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    public  Group _group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SocketConnector.getInstance().getSocket();
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_chat_list);
        if (_group == null)
            _group = (Group) getIntent().getSerializableExtra(ChatDetailFragment.GROUP_ITEM);
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatServerAPI chatServerAPI = retrofit.create(ChatServerAPI.class);
        Call<List<Chat>> call = chatServerAPI.getChatListForGroup(_group.getId());
        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                recyclerView.setAdapter(new ChatRecyclerViewAdapter(response.body()));
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {

            }
        });
    }

    public class ChatRecyclerViewAdapter
            extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

        private final List<Chat> _chats;

        public ChatRecyclerViewAdapter(List<Chat> items) {
            _chats = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder._chat = _chats.get(position);
            holder._idView.setText("#");
            holder._contentView.setText(_chats.get(position).getName());
            holder._view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_twoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(ChatDetailFragment.CHAT_ITEM, holder._chat);
                        ChatDetailFragment fragment = new ChatDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.chat_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ChatDetailActivity.class);
                        intent.putExtra(ChatDetailFragment.CHAT_ITEM, holder._chat);
                        intent.putExtra(ChatDetailFragment.GROUP_ITEM, _group);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return _chats.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View _view;
            public final TextView _idView;
            public final TextView _contentView;
            public Chat _chat;

            public ViewHolder(View view) {
                super(view);
                _view = view;
                _idView = (TextView) view.findViewById(R.id.id);
                _contentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + _contentView.getText() + "'";
            }
        }
    }
}
