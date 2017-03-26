package com.example.lexlevi.sweapp;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Common.Constants;
import com.example.lexlevi.sweapp.Interfaces.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.Chat;
import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Singletons.ChatServerClient;
import com.example.lexlevi.sweapp.Singletons.UserSession;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wang.avi.AVLoadingIndicatorView;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateChatActivity extends AppCompatActivity {

    @InjectView(R.id.invite_user_spinner) TextView _inviteUsers;
    @InjectView(R.id.create_chat_avi) AVLoadingIndicatorView _createChatAvi;
    @InjectView(R.id.chat_description) EditText _chatDescription;
    @InjectView(R.id.create_chat_fab) FloatingActionButton _createChatFab;
    @InjectView(R.id.chat_name) EditText _chatName;

    private String _groupId;

    private List<User> _groupUsers;
    boolean[] _selectedInvites; // new boolean[_groupUsers.size()];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);
        ButterKnife.inject(this);
        setTitle("");
        showProgress(false);
        Intent intent = getIntent();
        _groupUsers = (ArrayList<User>) intent.getSerializableExtra("groupUsers");
        _groupId = intent.getStringExtra("groupId");
        _selectedInvites = new boolean[_groupUsers.size()];
        _inviteUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] cs = new CharSequence[_groupUsers.size()];
                for(int i = 0; i < _groupUsers.size(); i++)
                    cs[i] = _groupUsers.get(i).getName();
                final AlertDialog dialog = new AlertDialog.Builder(CreateChatActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setMultiChoiceItems(cs, _selectedInvites, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                _selectedInvites[which] = isChecked;
                            }
                        })
                        .create();
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                dialog.show();
            }
        });
        _createChatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChatTapped();
            }
        });
    }

    private void showProgress(final boolean show) {
        if (show)
            _createChatAvi.show();
        else
            _createChatAvi.hide();
    }

    protected void createChatTapped() {
        List<String> invites; // list of user id's
        invites = new ArrayList<>();
        showProgress(true);
        if (_chatName.getText().length() == 0) {
            Snackbar s;
            s = Snackbar.make(_createChatFab,
                    "Chat must have a name!",
                    Snackbar.LENGTH_LONG);
            s.getView().setBackgroundColor(getResources()
                    .getColor(R.color.excitedColor));
            s.show();
            showProgress(false);
            return;
        }
        invites.add(UserSession.getInstance().getCurrentUser().getId());
        for (int i = 0; i < _groupUsers.size(); i++) {
            if (_selectedInvites[i]) {
                if (!_groupUsers.get(i).getId().equals(UserSession.getInstance().getCurrentUser().getId()))
                    invites.add(_groupUsers.get(i).getId());
            }
        }
        Chat chat = new Chat();
        chat.setName(_chatName.getText().toString());
        chat.setParticipants(invites);
        chat.setIsGroupMessage(true);
        chat.setGroup(_groupId);
        Call<Chat> call = ChatServerClient
                .getInstance()
                .api()
                .createChatForGroup(chat, _groupId);
        call.enqueue(new Callback<Chat>() {
            @Override
            public void onResponse(Call<Chat> call, Response<Chat> response) {
                showProgress(false);
                Snackbar.make(_createChatFab, "Chat Created", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Chat> call, Throwable t) {
                Snackbar s;
                s = Snackbar.make(_createChatFab,
                        "Error creating chat",
                        Snackbar.LENGTH_LONG);
                s.getView().setBackgroundColor(getResources()
                        .getColor(R.color.excitedColor));
                s.show();
            }
        });


    }
}
