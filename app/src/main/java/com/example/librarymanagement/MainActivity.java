package com.example.librarymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText username,password;

    public  void signIn(View view){
        if (username.getText().toString().matches("") || password.getText().toString().matches("")){
            Toast.makeText(this, "Empty Field/s", Toast.LENGTH_SHORT).show();
        }else {
            ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user!=null){
                        Log.i("Log In", "OK");
                        Toast.makeText(MainActivity.this, "logged In", Toast.LENGTH_SHORT).show();

                        if(user.getString("role").matches("user")){
                            Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                            startActivity(intent);
                        }else if(user.getString("role").matches("librarian")){
                            Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                            startActivity(intent);
                        }else if(user.getString("role").matches("admin")){
                            Intent intent = new Intent(getApplicationContext(),AdminActivity.class);
                            startActivity(intent);
                        }

                    }else{
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("unsuccessful", e.getMessage());
                    }
                }
            });
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

        if(ParseUser.getCurrentUser()!=null){
            if(ParseUser.getCurrentUser().getString("role").matches("user")){
                Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                startActivity(intent);
            }else if(ParseUser.getCurrentUser().getString("role").matches("librarian")){
                Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                startActivity(intent);
            }else if(ParseUser.getCurrentUser().getString("role").matches("admin")){
                Intent intent = new Intent(getApplicationContext(),AdminActivity.class);
                startActivity(intent);
            }
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}