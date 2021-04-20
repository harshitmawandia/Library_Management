package com.example.librarymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class BookEntryActivity extends AppCompatActivity {

    EditText enterTitle,enterAuthor,enterISBN,enterURL,enterPublisher;
    Button button;
    boolean b = true;
    String id;

    public void addBook(View view){
        if(b) {
            ParseObject object = new ParseObject("Book");
            object.put("title", enterTitle.getText().toString());
            object.put("author", enterAuthor.getText().toString());
            object.put("publisher", enterPublisher.getText().toString());
            object.put("ISBN", enterISBN.getText().toString());
            object.put("url", enterURL.getText().toString());
            object.put("availability", "available");

            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(BookEntryActivity.this, "Book Added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }else{
            ParseQuery<ParseObject> query= ParseQuery.getQuery("Book");
            query.getInBackground(id, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e==null && object!=null){
                        object.put("title", enterTitle.getText().toString());
                        object.put("author", enterAuthor.getText().toString());
                        object.put("publisher", enterPublisher.getText().toString());
                        object.put("ISBN", enterISBN.getText().toString());
                        object.put("url", enterURL.getText().toString());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(BookEntryActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_entry);

        button = findViewById(R.id.addBook);

        enterAuthor = findViewById(R.id.enterAuthor);
        enterTitle = findViewById(R.id.enterTitle);
        enterISBN = findViewById(R.id.enterISBN);
        enterURL = findViewById(R.id.enterURL);
        enterPublisher = findViewById(R.id.enterPublisher);

        Intent intent = getIntent();

        if(intent.hasExtra("objectId")){
            id= intent.getStringExtra("objectId");
            ParseQuery<ParseObject> query= ParseQuery.getQuery("Book");
            query.getInBackground(id, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e==null && object!=null){
                        button.setText("Save Changes");
                        enterAuthor.setText(object.getString("author"));
                        enterTitle.setText(object.getString("title"));
                        enterISBN.setText(object.getString("ISBN"));
                        enterPublisher.setText(object.getString("publisher"));
                        enterURL.setText(object.getString("url"));
                        b=false;
                    }
                }
            });
        }else{
            button.setText("Add Book");
        }


    }
}