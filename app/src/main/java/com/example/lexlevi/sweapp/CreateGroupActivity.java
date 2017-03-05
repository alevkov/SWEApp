package com.example.lexlevi.sweapp;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Common.Constants;
import com.example.lexlevi.sweapp.Controllers.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.Group;
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
import java.util.List;

import retrofit2.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateGroupActivity extends AppCompatActivity {

    @InjectView(R.id.courses_spinner) MaterialSpinner _coursesSpinner;
    @InjectView(R.id.days_spinner) TextView _daysSpinner;
    @InjectView(R.id.private_switch) Switch _isPrivate;
    @InjectView(R.id.fab) FloatingActionButton _fab;
    @InjectView(R.id.group_name) EditText _groupName;
    @InjectView(R.id.group_description) EditText _groupDescription;
    @InjectView(R.id.create_group_avi) AVLoadingIndicatorView _avi;

    List<Course> _selectedCourse;
    List<String> _days;
    boolean[] _selectedDays = new boolean[Constants.days.length];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.inject(this);
        setTitle("");
        showProgress(false);
        _selectedCourse = new ArrayList<>();
        _selectedCourse.add(UserSession.getInstance().getCurrentUser().getCourses().get(0));
        _days = new ArrayList<>();

        _coursesSpinner.setItems(UserSession.getInstance().getCurrentUser().getCourses());
        _coursesSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                _selectedCourse = new ArrayList<Course>();
                _selectedCourse.add((Course) item);
            }
        });

        _daysSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] cs = new CharSequence[Constants.days.length];
                for(int i = 0; i < Constants.days.length; i++)
                    cs[i] = Constants.days[i];
                final AlertDialog dialog = new AlertDialog.Builder(CreateGroupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setMultiChoiceItems(cs, _selectedDays, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                _selectedDays[which] = isChecked;
                            }
                        })
                        .create();
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                dialog.show();
            }
        });

        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupTapped();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (show)
            _avi.show();
        else
            _avi.hide();
    }

    protected void createGroupTapped() {
        _days = new ArrayList<>();
        showProgress(true);
        if (_groupName.getText().length() == 0) {
            Snackbar s;
            s = Snackbar.make(_fab,
                    "Group must have a name!",
                    Snackbar.LENGTH_LONG);
            s.getView().setBackgroundColor(getResources()
                    .getColor(R.color.excitedColor));
            s.show();
            showProgress(false);
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Group group = new Group();
        group.setName(_groupName.getText().toString());
        group.setCourses(_selectedCourse);
        for (int i = 0; i < Constants.days.length; i++) {
            if (_selectedDays[i])
                _days.add(Constants.days[i]);
        }
        group.setDays(_days);
        group.setIsPrivate(_isPrivate.isChecked());
        group.setAcademicYear(UserSession.getInstance().getCurrentUser().getAcademicYear());
        group.setSemester(UserSession.getInstance().getCurrentUser().getSemester());
        group.setDesc(_groupDescription.getText().toString());
        ChatServerAPI chatServerAPI = retrofit.create(ChatServerAPI.class);
        Call<Group> call = chatServerAPI.createGroupForUser(UserSession.getInstance()
                .getCurrentUser()
                .getId(), group);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                showProgress(false);
                Snackbar.make(_fab, "Group Made", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                showProgress(false);
                Snackbar s;
                s = Snackbar.make(_fab,
                        "Error creating group",
                        Snackbar.LENGTH_LONG);
                s.getView().setBackgroundColor(getResources()
                        .getColor(R.color.excitedColor));
                s.show();
            }
        });
    }
}
