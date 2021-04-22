package com.example.librarymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static android.provider.ContactsContract.Intents.Insert.EMAIL;

public class MainActivity extends AppCompatActivity {


    EditText username,password;
    public static GoogleSignInClient mGoogleSignInClient;
    public static  GoogleSignInAccount account;


    public  void signIn(View view){
        if (username.getText().toString().matches("") || password.getText().toString().matches("")){
            Toast.makeText(this, "Empty Field/s", Toast.LENGTH_SHORT).show();
        }else {
            ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(ParseUser.getCurrentUser()!=null){
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("User2");
                        query.whereMatches("username", ParseUser.getCurrentUser().getUsername());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(objects.get(0).getString("role").matches("user")){
                                    Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                                    startActivity(intent);
                                }else if(objects.get(0).getString("role").matches("librarian")){
                                    Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                                    startActivity(intent);
                                }else if(objects.get(0).getString("role").matches("admin")){
                                    Intent intent = new Intent(getApplicationContext(),AdminActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });

                    }else{
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("unsuccessful", e.getMessage());
                    }
                }
            });
        }
    }

    public void signInGoogle(View view){

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.i("reached",signInIntent.getAction());
        startActivityForResult(signInIntent, 0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.i("Request Code", Integer.toString(requestCode));
            if(requestCode==0) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                handleSignInResult(task);
            }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {


        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            ParseUser user = new ParseUser();
            user.setUsername(account.getEmail());
            user.setEmail(account.getEmail());
            user.setPassword(account.getEmail());
            ParseObject user1 = new ParseObject("User2");
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        user1.put("username",account.getEmail());
                        user1.saveInBackground();
                        Log.i("Success", "sign in successful");
                        Toast.makeText(MainActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                        ParseUser.logInInBackground(account.getEmail(),account.getEmail());
                        Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                        startActivity(intent);

                    } else {
                        try {
                            ParseUser.logInInBackground(account.getEmail(),account.getEmail());
                            if(ParseUser.getCurrentUser()!=null){
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("User2");
                                query.whereMatches("username", ParseUser.getCurrentUser().getUsername());
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if(objects.get(0).getString("role").matches("user")){

                                            Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                                            startActivity(intent);
                                        }else if(objects.get(0).getString("role").matches("librarian")){
                                            Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                                            startActivity(intent);
                                        }else if(objects.get(0).getString("role").matches("admin")){
                                            Intent intent = new Intent(getApplicationContext(),AdminActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });


                            }

                        }catch (Exception exception){
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("UnSuccess", e.getMessage());
                        }

                    }
                }
            });


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("sign in", "signInResult:failed code=" + e.getStatusCode());

        }
    }



    public void signUpActivity(View view){
        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);




        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);


        if(account!=null) {
            ParseUser.logInInBackground(account.getEmail(),account.getEmail());
        }


        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle(v);
            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.librarymanagement", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if(ParseUser.getCurrentUser()!=null){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("User2");
            query.whereMatches("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(objects.get(0).getString("role").matches("user")){
                        Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                        startActivity(intent);
                    }else if(objects.get(0).getString("role").matches("librarian")){
                        Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                        startActivity(intent);
                    }else if(objects.get(0).getString("role").matches("admin")){
                        Intent intent = new Intent(getApplicationContext(),AdminActivity.class);
                        startActivity(intent);
                    }
                }
            });


        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}