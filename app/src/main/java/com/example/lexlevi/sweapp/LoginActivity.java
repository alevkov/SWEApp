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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Controllers.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Singletons.UserSession;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.net.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

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
        // Set up the login form.
        _emailView = (AutoCompleteTextView) findViewById(R.id.email);
        _avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        _loginForm = (ScrollView) findViewById(R.id.login_form);
        _avi.hide();
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

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        _emailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
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
            // TODO: attempt authentication against a network service.
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URLs.BASE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            User user = new User();
            user.setEmail(mEmail);
            user.setPassword(mPassword);
            ChatServerAPI chatServerAPI = retrofit.create(ChatServerAPI.class);
            Call<User> call = chatServerAPI.loginUser(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    showProgress(false);
                    Snackbar s;
                    switch (response.code()) {
                        case 200:
                            UserSession.getInstance().setCurrentUser(response.body());
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
}