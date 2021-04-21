package com.example.librarymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RenewBook extends AppCompatActivity {

    ImageView bookCover;
    TextView titleTextView,authorTextView,publisherTextView,ISBNTextView, availabilityTextView;
    EditText daysRequired;
    Button button;
    String objectId;
    RatingBar ratingBar;
    ParseObject parseObject;

    public static class DownloadImage extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url;
            HttpURLConnection urlConnection = null;
            Bitmap bitmap;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();

                bitmap = BitmapFactory.decodeStream(in);

                return (bitmap);

            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }
    }

    public void borrow(View view){
        int days=Integer.parseInt(daysRequired.getText().toString());
        if(days>14){
            Toast.makeText(this, "Enter number less than 14", Toast.LENGTH_SHORT).show();
        }else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Book");
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null && object != null) {
                        object.put("availability","renew requested");
                        object.put("daysRequired",days);
                        object.put("borrower", ParseUser.getCurrentUser().getUsername());
                        Toast.makeText(RenewBook.this, "Request sent", Toast.LENGTH_SHORT).show();

                        object.saveInBackground();

                    }
                }
            });
        }
    }

    public void save(View view){
        EditText editText = findViewById(R.id.reviewEditText);
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Record");
        query1.whereMatches("book",objectId).whereMatches("borrower",ParseUser.getCurrentUser().getUsername());
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(objects.size()>0){
                    objects.get(0).put("review",editText.getText().toString());
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(RenewBook.this, "Rating Saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_book);


        bookCover = findViewById(R.id.bookCover);
        titleTextView = findViewById(R.id.titleTextView);
        authorTextView = findViewById(R.id.authorTextView);
        publisherTextView = findViewById(R.id.publisher);
        ISBNTextView = findViewById(R.id.ISBN);
        availabilityTextView = findViewById(R.id.availability);
        daysRequired = findViewById(R.id.daysRequired);
        button = findViewById(R.id.button2);

        Intent intent = getIntent();
        objectId=intent.getStringExtra("objectId");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Book");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null && object!=null){
                    parseObject=object;
                    String title = object.getString("title");
                    String author = "Author: "+ object.getString("author");
                    String publisher ="Published by: " +object.getString("publisher");
                    String ISBN ="ISBN: " +object.getString("ISBN");
                    String url = object.getString("url");
                    String availability = "Availability: "+object.getString("availability");

                    BookInfoActivity.DownloadImage downloadImage = new BookInfoActivity.DownloadImage();
                    Bitmap bitmap = null;
                    try {
                        bitmap = downloadImage.execute(url).get();
                        bookCover.setImageBitmap(bitmap);
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }

                    titleTextView.setText(title);
                    authorTextView.setText(author);
                    publisherTextView.setText(publisher);
                    ISBNTextView.setText(ISBN);
                    availabilityTextView.setText(availability);
                    Log.i("availability",availability);
                    button.setVisibility(View.VISIBLE);
                    daysRequired.setVisibility(View.VISIBLE);

                }
            }
        });

        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Record");
                query1.whereMatches("book",objectId).whereMatches("borrower",ParseUser.getCurrentUser().getUsername());
                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(objects.size()>0){
                            objects.get(0).put("rating",ratingBar.getRating());
                            objects.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(RenewBook.this, "Rating Saved", Toast.LENGTH_SHORT).show();
                                    if(objects.get(0).has("rating")) {
                                        double rating = (parseObject.getInt("n")-1) * parseObject.getDouble("rating") + ratingBar.getRating();
                                        parseObject.put("rating", rating);
                                    }else{
                                        double rating = (parseObject.getInt("n")) * parseObject.getDouble("rating") + ratingBar.getRating();
                                        parseObject.increment("n");
                                        parseObject.put("rating", rating);
                                    }

                                    parseObject.saveInBackground();
                                }
                            });

                        }
                    }
                });
            }
        });




    }
}