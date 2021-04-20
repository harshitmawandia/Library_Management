package com.example.librarymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RequestActivity extends AppCompatActivity {

    ParseObject object1;

    public void accept(View view){
        object1.put("availability","borrowed");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy 'at' hh:mm:ss 'UTC'");
        calendar.add(Calendar.DATE, +object1.getInt("daysRequired"));
        Log.i("Date",format.format(calendar.getTime()));

        try {
            Date date =format.parse(format.format(calendar.getTime()));
            object1.put("dueDate",date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        ParseObject object = new ParseObject("Record");

        object.put("book",object1.getObjectId());
        object.put("borrower",object1.get("borrower"));

        object1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(RequestActivity.this, " Request Accepted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void reject(View view){
        object1.put("availability","available");
        object1.remove("borrower");
        object1.remove("daysRequired");
        object1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(RequestActivity.this, "Request Denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        TextView textView = findViewById(R.id.textView4);
        Intent intent = getIntent();
        String objectId=intent.getStringExtra("objectId");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Book");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object1 = object;
                textView.setText("Title: "+object.getString("title")+"\n\nRequested by: "+object.getString("borrower")+"\n\nRequested for: "+
                        object.getInt("daysRequired")+" days");
            }
        });
    }
}