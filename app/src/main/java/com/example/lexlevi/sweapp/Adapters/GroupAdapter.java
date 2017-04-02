package com.example.lexlevi.sweapp.Adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lexlevi.sweapp.ChatDetailFragment;
import com.example.lexlevi.sweapp.ChatListActivity;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.R;

public class GroupAdapter extends BaseAdapter {
    private final Context _context;
    private final Group[] _groups;

    public GroupAdapter(Context context, Group[] groups) {
        _context = context;
        _groups = groups;
    }

    @Override
    public int getCount() {
        return _groups.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(_context);
            convertView = layoutInflater.inflate(R.layout.linearlayout_group, null);
        }

        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_group_name);
        final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);
        nameTextView.setText(_groups[position].toString());
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, ChatListActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable(ChatDetailFragment.GROUP_ITEM, _groups[position]);
                intent.putExtras(extras);
                _context.startActivity(intent);
            }
        });
        return convertView;
    }
}
