package com.example.lexlevi.sweapp;
;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.lexlevi.sweapp.Models.Chat;
import com.example.lexlevi.sweapp.Models.Event;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Singletons.Client;
import com.example.lexlevi.sweapp.Singletons.Session;

import com.wang.avi.AVLoadingIndicatorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.*;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CreateEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @InjectView(R.id.create_event_avi) AVLoadingIndicatorView _createEventAvi;
    @InjectView(R.id.create_event_inner_fab) FloatingActionButton _createEventFab;
    @InjectView(R.id.event_name) EditText _eventName;
    @InjectView(R.id.duedate_picker) TextView _dueDatePicker;
    @InjectView(R.id.event_description) EditText _eventDescription;

    private String _groupId;
    private Date _eventDate;
    private Event _createdEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.inject(this);
        _createEventAvi.hide();
        Intent intent = getIntent();
        _groupId = intent.getStringExtra("groupId");
        _createdEvent = new Event();
        _dueDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CreateEventActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(true);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        _createEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEventTapped();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        _eventDate = new Date(year, monthOfYear, dayOfMonth);
        _createdEvent.setDueDate(_eventDate);
    }

    private void showProgress(final boolean show) {
        if (show)
            _createEventAvi.show();
        else
            _createEventAvi.hide();
    }

    protected void createEventTapped() {
        showProgress(true);
        Snackbar s;
        if (_eventName.getText().length() == 0) {
            s = Snackbar.make(_createEventFab,
                    "Event must have a name!",
                    Snackbar.LENGTH_LONG);
            s.getView().setBackgroundColor(getResources()
                    .getColor(R.color.excitedColor));
            s.show();
            showProgress(false);
            return;
        }
        if (_createdEvent.getDueDate() == null) {
            s = Snackbar.make(_createEventFab,
                    "Event must have a Due Date!",
                    Snackbar.LENGTH_LONG);
            s.getView().setBackgroundColor(getResources()
                    .getColor(R.color.excitedColor));
            s.show();
            showProgress(false);
            return;
        }
        _createdEvent.setName(_eventName.getText().toString());
        _createdEvent.setGroup(_groupId);
        _createdEvent.setDescription(_eventDescription.getText().toString());
        _createdEvent.setCompleted(false);
        Call<Event> call = Client
                .shared()
                .api().createEventForGroup(_createdEvent, _groupId);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                showProgress(false);
                Snackbar.make(_createEventFab, "Event Created", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Snackbar s;
                s = Snackbar.make(_createEventFab,
                        "Error creating event",
                        Snackbar.LENGTH_LONG);
                s.getView().setBackgroundColor(getResources()
                        .getColor(R.color.excitedColor));
                s.show();
            }
        });
    }
}
