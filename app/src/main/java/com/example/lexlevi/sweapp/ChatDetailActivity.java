package com.example.lexlevi.sweapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.lexlevi.sweapp.Singletons.SocketConnector;

public class ChatDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_detail_toolbar);
        setSupportActionBar(toolbar);

        // Don't show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(ChatDetailFragment.CHAT_ITEM,
                    getIntent().getSerializableExtra(ChatDetailFragment.CHAT_ITEM));
            arguments.putSerializable(ChatDetailFragment.GROUP_ITEM,
                    getIntent().getSerializableExtra(ChatDetailFragment.GROUP_ITEM));
            ChatDetailFragment fragment = new ChatDetailFragment();
            fragment.setArguments(arguments);
            fragment.setRetainInstance(true);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chat_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //
            navigateUpTo(new Intent(this, ChatListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
