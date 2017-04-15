package com.example.lexlevi.sweapp;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Common.Constants;
import com.example.lexlevi.sweapp.Interfaces.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Singletons.Client;
import com.wang.avi.AVLoadingIndicatorView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {

    @InjectView(R.id.input_username) EditText _usernameText;
    @InjectView(R.id.input_firstname) EditText _firstnameText;
    @InjectView(R.id.input_lastname) EditText _lastnameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.select_major) TextView _selectMajor;
    @InjectView(R.id.select_courses) TextView _selectCourses;
    @InjectView(R.id.select_year) TextView _selectYear;
    @InjectView(R.id.select_semester) TextView _selectSemester;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;
    @InjectView(R.id.avi) AVLoadingIndicatorView _avi;
    @InjectView(R.id.sign_up_form) ScrollView _signUpForm;

    private ArrayList<Course> _courseList = null;
    private HashMap<Integer, Course> _selectedCourses = null;
    private String _selectedMajor = "";
    private String _selectedYear = "";
    private String _selectedSemester = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        setTitle(" ");
        ButterKnife.inject(this);

        // Get all courses
        _avi.show();
        _selectCourses.setEnabled(false);
        _selectedCourses = new HashMap<>();
        _courseList = new ArrayList<>();
        Call<List<Course>> call = Client.shared().api().getAllCourses();
        call.enqueue(new Callback<List<Course>>() {
            Snackbar s;
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                for(Course c : response.body()) {
                    _courseList.add(c);
                }
                _avi.hide();
                _selectCourses.setEnabled(true);
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Log.d("Error:", R.string.error_courses_load + t.toString());
                s = Snackbar.make(_signUpForm,
                        R.string.error_courses_load,
                        Snackbar.LENGTH_LONG);
                s.getView().setBackgroundColor(getResources().getColor(R.color.excitedColor));
                s.show();
                _avi.hide();
            }
        });
        // Set up major select
        _selectMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = -1;
                final CharSequence[] cs = new CharSequence[Constants.majors.length];
                for(int i = 0; i < cs.length; i++) {
                    if(Constants.majors[i] == _selectedMajor) checked = i;
                    cs[i] = Constants.majors[i];
                }
                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setSingleChoiceItems(cs, checked, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _selectedMajor = (String) cs[which];
                            }
                        })
                        .create();
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                dialog.show();
            }
        });
        // Set up course select
        _selectCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] cs = new CharSequence[_courseList.size()];
                for(int i = 0; i < _courseList.size(); i++) {
                    cs[i] = _courseList.get(i).getName();
                }
                boolean[] checkedItems = new boolean[cs.length];
                for(Map.Entry<Integer, Course> entry: _selectedCourses.entrySet()) {
                    checkedItems[entry.getKey()] = true;
                }
                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setMultiChoiceItems(cs, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if(isChecked) {
                                    if (_selectedCourses.get(which) == null) {
                                        _selectedCourses.put(which, _courseList.get(which));
                                    }
                                } else {
                                    _selectedCourses.remove(which);
                                }
                            }
                        })
                        .create();
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                dialog.show();
            }
        });

        _selectYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = -1;
                final CharSequence[] cs = new CharSequence[Constants.years.length];
                for(int i = 0; i < cs.length; i++) {
                    if(Constants.years[i] == _selectedYear) checked = i;
                    cs[i] = Constants.years[i];
                }
                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setSingleChoiceItems(cs, checked, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _selectedYear = (String) cs[which];
                            }
                        })
                        .create();
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                dialog.show();
            }
        });

        _selectSemester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = -1;
                final CharSequence[] cs = new CharSequence[Constants.semesters.length];
                for(int i = 0; i < cs.length; i++) {
                    if(Constants.semesters[i] == _selectedSemester) checked = i;
                    cs[i] = Constants.semesters[i];
                }
                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setSingleChoiceItems(cs, checked, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _selectedSemester = (String) cs[which];
                            }
                        })
                        .create();
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
        String first = _firstnameText.getText().toString();
        String last = _lastnameText.getText().toString();
        user.setEmail(email);
        user.setUserName(name);
        user.setPassword(password);
        user.setFirstName(first);
        user.setLastName(last);
        ArrayList<Course> selectedCourseList = new ArrayList<>(_selectedCourses.values());
        user.setCourses(selectedCourseList);
        user.setMajor(_selectedMajor);
        user.setYear(_selectedYear);
        user.setSemester(_selectedSemester);
        ChatServerAPI chatServerAPI = retrofit.create(ChatServerAPI.class);
        Call<User> call = chatServerAPI.createUser(user);
        progressDialog.show();
        call.enqueue(new Callback<User>() {
             @Override
             public void onResponse(Call<User> call, Response<User> response) {
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
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
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

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("between 4 and 20 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
