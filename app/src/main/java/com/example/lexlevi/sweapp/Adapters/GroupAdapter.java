package com.example.lexlevi.sweapp.Adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lexlevi.sweapp.ChatDetailFragment;
import com.example.lexlevi.sweapp.ChatListActivity;
import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Controllers.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.R;
import com.example.lexlevi.sweapp.Singletons.UserSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupAdapter extends BaseAdapter {
    private final Context mContext;
    private final Group[] groups;

    // 1
    public GroupAdapter(Context context, Group[] groups) {
        this.mContext = context;
        this.groups = groups;
    }

    // 2
    @Override
    public int getCount() {
        return groups.length;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_group, null);
        }

        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_group_name);
        final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);
        nameTextView.setText(groups[position].toString());
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatListActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable(ChatDetailFragment.GROUP_ITEM, groups[position]);
                intent.putExtras(extras);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
}
