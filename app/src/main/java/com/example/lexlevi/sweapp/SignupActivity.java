package com.example.lexlevi.sweapp;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Controllers.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.User;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.CheckedTextView;

import retrofit2.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_username) EditText _usernameText;
    @InjectView(R.id.input_firstname) EditText _firstnameText;
    @InjectView(R.id.input_lastname) EditText _lastnameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.select_major) TextView _selectMajor;
    @InjectView(R.id.select_courses) TextView _selectCourses;
    @InjectView(R.id.select_year) TextView _selectYear;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        setTitle(" ");
        ButterKnife.inject(this);

        _selectMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SignupActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Computer Science");
                arrayAdapter.add("Computer Engineering");

                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setAdapter(arrayAdapter, null)
                        .setNegativeButton(getResources().getString(android.R.string.cancel), null)
                        .create();

                dialog.getListView().setItemsCanFocus(false);
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        System.out.println("clicked" + position);
                        CheckedTextView textView = (CheckedTextView) view;
                        if(textView.isChecked()) {

                        } else {

                        }
                    }
                });

                dialog.show();
            }
        });

        _selectCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SignupActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Foundations of CS");
                arrayAdapter.add("Data Structures");
                arrayAdapter.add("Discrete Math");
                arrayAdapter.add("Analysis of Algorithms");
                arrayAdapter.add("Operating Systems");
                arrayAdapter.add("Principles of Software Engineering");
                arrayAdapter.add("Intro to Databases");
                arrayAdapter.add("Intro to C");
                arrayAdapter.add("Intro to Internet Computing");

                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setAdapter(arrayAdapter, null)
                        .setNegativeButton(getResources().getString(android.R.string.cancel), null)
                        .create();

                dialog.getListView().setItemsCanFocus(false);
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        System.out.println("clicked" + position);
                        CheckedTextView textView = (CheckedTextView) view;
                        if(textView.isChecked()) {

                        } else {

                        }
                    }
                });

                dialog.show();
            }
        });

        _selectYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SignupActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Freshman");
                arrayAdapter.add("Sophomore");
                arrayAdapter.add("Junior");
                arrayAdapter.add("Senior");

                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setAdapter(arrayAdapter, null)
                        .setNegativeButton(getResources().getString(android.R.string.cancel), null)
                        .create();

                dialog.getListView().setItemsCanFocus(false);
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        System.out.println("clicked" + position);
                        CheckedTextView textView = (CheckedTextView) view;
                        if(textView.isChecked()) {

                        } else {

                        }
                    }
                });

                dialog.show();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        User user = new User();
        String name = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        user.setEmail(email);
        user.setUserName(name);
        user.setPassword(password);
        ChatServerAPI chatServerAPI = retrofit.create(ChatServerAPI.class);
        Call<User> call = chatServerAPI.createUser(user);
        progressDialog.show();
        call.enqueue(new Callback<User>() {
             @Override
             public void onResponse(Call<User> call, Response<User> response) {
                 Log.d("New user: ", response.body().toString());
                 progressDialog.hide();
             }

             @Override
             public void onFailure(Call<User> call, Throwable t) {
                 progressDialog.hide();
             }
        });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _usernameText.setError("at least 3 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
