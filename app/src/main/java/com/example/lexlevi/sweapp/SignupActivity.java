package com.example.lexlevi.sweapp;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Common.Constants;
import com.example.lexlevi.sweapp.Controllers.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.User;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.CheckedTextView;

import org.w3c.dom.Text;

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

    private ArrayList<Course> courseList = null;
    private HashMap<Integer, Course> selectedCourses = null;
    private String selectedMajor = "";
    private String selectedYear = "";
    private String selectedSemester = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        setTitle(" ");
        ButterKnife.inject(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Get all courses
        selectedCourses = new HashMap<Integer, Course>();
        courseList = new ArrayList<Course>();
        ChatServerAPI chatServerAPI = retrofit.create(ChatServerAPI.class);
        Call<List<Course>> call = chatServerAPI.getAllCourses();
        call.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                for(Course c : response.body()) {
                    courseList.add(c);
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {

            }
        });
        // Set up major select
        _selectMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = -1;
                final CharSequence[] cs = new CharSequence[Constants.majors.length];
                for(int i = 0; i < cs.length; i++) {
                    if(Constants.majors[i] == selectedMajor) checked = i;
                    cs[i] = Constants.majors[i];
                }
                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setSingleChoiceItems(cs, checked, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedMajor = (String) cs[which];
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
                CharSequence[] cs = new CharSequence[courseList.size()];
                for(int i = 0; i < courseList.size(); i++) {
                    cs[i] = courseList.get(i).getName();
                }
                boolean[] checkedItems = new boolean[cs.length];
                for(Map.Entry<Integer, Course> entry: selectedCourses.entrySet()) {
                    checkedItems[entry.getKey()] = true;
                }
                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setMultiChoiceItems(cs, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if(isChecked) {
                                    if (selectedCourses.get(which) == null) {
                                        selectedCourses.put(which, courseList.get(which));
                                    }
                                } else {
                                    selectedCourses.remove(which);
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
                    if(Constants.years[i] == selectedYear) checked = i;
                    cs[i] = Constants.years[i];
                }
                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setSingleChoiceItems(cs, checked, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedYear = (String) cs[which];
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
                    if(Constants.semesters[i] == selectedSemester) checked = i;
                    cs[i] = Constants.semesters[i];
                }
                final AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("")
                        .setPositiveButton("Done", null)
                        .setSingleChoiceItems(cs, checked, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedSemester = (String) cs[which];
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
        ArrayList<Course> selectedCourseList = new ArrayList<>(selectedCourses.values());
        user.setCourses(selectedCourseList);
        user.setMajor(selectedMajor);
        user.setYear(selectedYear);
        user.setSemester(selectedSemester);
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
