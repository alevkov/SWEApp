package com.example.lexlevi.sweapp;

import android.app.Activity;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lexlevi.sweapp.Models.Chat;
import com.example.lexlevi.sweapp.Models.Group;

/**
 * A fragment representing a single Chat detail screen.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity}
 * on handsets.
 */
public class ChatDetailFragment extends Fragment {

    public static final String CHAT_ITEM = "chat";
    public static final String GROUP_ITEM = "group";

    private Chat _chat;
    private Group _group;

    public ChatDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(CHAT_ITEM)) {
            _chat = (Chat) getActivity().getIntent().getSerializableExtra(CHAT_ITEM);
            _group = (Group) getActivity().getIntent().getSerializableExtra(GROUP_ITEM);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity()
                    .findViewById(R.id.chat_detail_toolbar_layout);
            AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.chat_detail_app_bar);
            String expandTitle = " # " + _chat.getName();
            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.chat_detail_toolbar);
            if (collapsingToolbarLayout != null)
                collapsingToolbarLayout.setTitle(expandTitle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_detail, container, false);
        if (_chat != null) {
            ((TextView) rootView.findViewById(R.id.chat_detail)).setText(_chat.getName());
        }

        return rootView;
    }
}
