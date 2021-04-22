package com.example.librarymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    EditText username,password,email,OTP;

    public int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;


    }

    Button signUP ;
    int a;

    public void sendOTP(View view){

        if (username.getText().toString().matches("") || password.getText().toString().matches("") || email.getText().toString().matches("")){
            Toast.makeText(this, "Empty Field/s", Toast.LENGTH_SHORT).show();
        }else {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            try {
                a=getRandomNumberUsingNextInt(100000,999999);
                GMailSender sender = new GMailSender("librarymanagementdatabase@gmail.com", "lib12345");
                sender.sendMail("Verification Mail",
                        "Your Verification Code is:\n"+a,
                        "librarymanagementdatabase@gmail.com",
                        email.getText().toString());

                Toast.makeText(this, "Email Sent", Toast.LENGTH_SHORT).show();
                username.setActivated(false);
                password.setActivated(false);
                email.setActivated(false);
                signUP.setActivated(true);

            } catch (Exception e1) {
                Log.e("SendMail", e1.getMessage(), e1);

                Toast.makeText(this, "Could not send OTP, try Again Later", Toast.LENGTH_SHORT).show();
            }


        }

    }

    public void signUp(View view){
        if(Integer.parseInt(OTP.getText().toString())==a){

            ParseUser user = new ParseUser();
            ParseObject user1 = new ParseObject("User2");

            user.setUsername(username.getText().toString());
            user.setPassword(password.getText().toString());
            user.setEmail(email.getText().toString());


            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        user1.put("username",username.getText().toString());
                        user1.saveInBackground();
                        Log.i("Success", "sign up successful");
                        Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();


                        ParseUser.logInInBackground(username.getText().toString(),password.getText().toString());
                        Intent intent1 = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(intent1);


                    } else {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("UnSuccess", e.getMessage());
                    }
                }
            });
        }else{
            Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
        }
    }

    public void signInActivity(View view){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        OTP = findViewById(R.id.OTP);
        signUP = findViewById(R.id.button1);

        signUP.setActivated(false);



        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}