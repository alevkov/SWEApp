package com.example.lexlevi.sweapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lexlevi.sweapp.DashboardActivity;
import com.example.lexlevi.sweapp.Models.Classmate;
import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.R;
import com.example.lexlevi.sweapp.SignupActivity;
import com.example.lexlevi.sweapp.Singletons.Client;
import com.example.lexlevi.sweapp.Singletons.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassmatesAdapter extends RecyclerView.Adapter<ClassmatesAdapter.ViewHolder>
implements PopupMenu.OnMenuItemClickListener {

    private final int MENU_INVITE = 100;

    private RecyclerView _recyclerView;

    private Context _context;
    private Classmate[] _classmates;
    private int _expandedPosition = -1;
    private int _menuForPosition = -1;

    private List<Group> _groupList = null;
    private HashMap<Integer, Group> _selectedGroups = null;

    public ClassmatesAdapter(RecyclerView rootView, Context context, Classmate[] classmates, RecyclerView v) {
        super();
        _context = context;
        _classmates = classmates;
        _recyclerView = v;
        _selectedGroups = new HashMap<>();
        _groupList = Session.shared().groups();
        _recyclerView = rootView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.classmates_list_content, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Classmate classmate = _classmates[position];
        String matchingClasses = "";
        final boolean isExpanded = position == _expandedPosition;
        holder._textViewName.setText(classmate.getUser().getFirstName()
                + " " + classmate.getUser().getLastName() + " (" + classmate.getMatches() + ")");
        for (Course c : _classmates[position].getUser().getCourses()) {
            for (Course myC : Session.shared().user().getCourses()) {
                if (c.getCode().equals(myC.getCode())) {
                    matchingClasses += c.getName();
                    matchingClasses += "\r\n";
                }
            }
        }
        matchingClasses = matchingClasses.substring(0, matchingClasses.length() - 2);
        holder._textViewClasses.setText(matchingClasses);
        holder._textViewClasses.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder._textViewName.setActivated(isExpanded);
        holder._imageViewLogo.setActivated(isExpanded);
        holder._imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _expandedPosition = isExpanded ? -1 : position;
                //TransitionManager.beginDelayedTransition(_recyclerView);
                notifyDataSetChanged();
            }
        });
        holder._textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _expandedPosition = isExpanded ? -1 : position;
                notifyDataSetChanged();
            }
        });
        holder._inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(holder._textViewName, position);
            }
        });
    }

    public void showMenu(View v, int position) {
        PopupMenu popup = new PopupMenu(_context, v);
        _menuForPosition = position;
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_classmates);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getOrder()) {
            case MENU_INVITE:
                showSelectGroupsDialog(_recyclerView);
                break;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return _classmates.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView _textViewName;
        public ImageView _imageViewLogo;
        public TextView _textViewClasses;
        public ImageView _inviteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            _textViewName = (TextView) itemView.findViewById(R.id.classmate_name);
            _imageViewLogo = (ImageView) itemView.findViewById(R.id.classmate_logo);
            _textViewClasses = (TextView) itemView.findViewById(R.id.classmate_classes_in);
            _inviteButton = (ImageView) itemView.findViewById(R.id.classmate_invite);
        }
    }

    private void showSelectGroupsDialog(final View v) {
        CharSequence[] cs = new CharSequence[_groupList.size()];
        for(int i = 0; i < _groupList.size(); i++) {
            cs[i] = _groupList.get(i).getName();
        }
        boolean[] checkedItems = new boolean[cs.length];
        for(Map.Entry<Integer, Group> entry: _selectedGroups.entrySet()) {
            checkedItems[entry.getKey()] = true;
        }
        final AlertDialog dialog = new AlertDialog.Builder(_context)
                .setTitle("")
                .setPositiveButton("Send Invites", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendInivitesToClassmate(v);
                    }
                })
                .setMultiChoiceItems(cs, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked) {
                            if (_selectedGroups.get(which) == null) {
                                _selectedGroups.put(which, _groupList.get(which));
                            }
                        } else {
                            _selectedGroups.remove(which);
                        }
                    }
                })
                .create();
        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dialog.show();
    }

    private void sendInivitesToClassmate(final View v) {
        ArrayList<String> selectedGroupList = new ArrayList<>();
        for (Group g : _selectedGroups.values()) {
            selectedGroupList.add(g.getId());
        }
        Call<User> call = Client.shared().api().inviteUserToGroup(_classmates[_menuForPosition].getUser().getId(),
                                                Session.shared().user().getFirstName(), selectedGroupList);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Snackbar.make(v, "Invitations Sent", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(v, "Failed Sending Invitations", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
