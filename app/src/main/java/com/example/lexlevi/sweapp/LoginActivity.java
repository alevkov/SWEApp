package com.example.lexlevi.sweapp;

import android.content.Intent;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Interfaces.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Singletons.Client;
import com.example.lexlevi.sweapp.Singletons.Session;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 0;
    private UserLoginTask _loginAuthTask = null;

    // UI references
    private AutoCompleteTextView _emailView;
    private EditText _passwordView;
    private AVLoadingIndicatorView _avi;
    private Button _emailSignInButton;
    private TextView _registerButton;
    private ScrollView _loginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(" ");
        _avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        _avi.hide();
        Session.shared().setContext(getApplicationContext());
        // Set up the login form.
        _emailView = (AutoCompleteTextView) findViewById(R.id.email);
        _loginForm = (ScrollView) findViewById(R.id.login_form);
        populateAutoComplete();

        _passwordView = (EditText) findViewById(R.id.password);
        _passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        _emailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        _emailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        _registerButton = (TextView) findViewById(R.id.action_register);
        _registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistration();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_avi != null) _avi.hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (_avi != null) _avi.hide();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(_emailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private void attemptLogin() {
        if (_loginAuthTask != null) {
            return;
        }

        // Reset errors.
        _emailView.setError(null);
        _passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = _emailView.getText().toString();
        String password = _passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            _passwordView.setError(getString(R.string.error_invalid_password));
            focusView = _passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            _emailView.setError(getString(R.string.error_field_required));
            focusView = _emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            _emailView.setError(getString(R.string.error_invalid_email));
            focusView = _emailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            _loginAuthTask = new UserLoginTask(email, password);
            _loginAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailView.setError("enter a valid email address");
            return false;
        } else {
            _emailView.setError(null);
            return true;
        }

    }

    private boolean isPasswordValid(String password) {
        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordView.setError("between 4 and 20 alphanumeric characters");
            return false;
        } else {
            _passwordView.setError(null);
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void goToRegistration() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (show) {
            _avi.show();
            _registerButton.setVisibility(View.GONE);
            _emailSignInButton.setVisibility(View.GONE);

        } else {
            _avi.hide();
            _registerButton.setVisibility(View.VISIBLE);
            _emailSignInButton.setVisibility(View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            User user = new User();
            user.setEmail(mEmail);
            user.setPassword(mPassword);
            Call<User> call = Client.shared().api().loginUser(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    showProgress(false);
                    Snackbar s;
                    switch (response.code()) {
                        case 200:
                            Session.shared().setCurrentUser(response.body());
                            Intent intent = new Intent(LoginActivity.this,
                                                        DashboardActivity.class);
                            startActivity(intent);
                            break;
                        case 404:
                            s = Snackbar.make(_loginForm,
                                    R.string.error_user_nonexist,
                                    Snackbar.LENGTH_LONG);
                            s.getView().setBackgroundColor(getResources()
                                                            .getColor(R.color.excitedColor));
                            s.show();
                            break;
                        case 403:
                            s = Snackbar.make(_loginForm,
                                    R.string.error_incorrect_password,
                                    Snackbar.LENGTH_LONG);
                            s.getView().setBackgroundColor(getResources()
                                                            .getColor(R.color.excitedColor));
                            s.show();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showProgress(false);
                    Snackbar s;
                    s = Snackbar.make(_loginForm,
                            R.string.error_oops,
                            Snackbar.LENGTH_LONG);
                    s.getView().setBackgroundColor(getResources()
                            .getColor(R.color.excitedColor));
                    s.show();
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            _loginAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            _loginAuthTask = null;
            showProgress(false);
        }
    }

//    public void goStraightToDashboard() {
//        Call<User> call = Client.shared().api().getUserForId(Session.shared().getUserId());
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                switch (response.code()) {
//                    case 200:
//                        Session.shared().setCurrentUser(response.body());
//                        Intent intent = new Intent(LoginActivity.this,
//                                DashboardActivity.class);
//                        startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Snackbar s;
//                s = Snackbar.make(_loginForm,
//                        R.string.error_oops,
//                        Snackbar.LENGTH_LONG);
//                s.getView().setBackgroundColor(getResources()
//                        .getColor(R.color.excitedColor));
//                s.show();
//            }
//        });
//    }
}