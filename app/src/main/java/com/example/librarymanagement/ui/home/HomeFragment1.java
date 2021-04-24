package com.example.librarymanagement.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.librarymanagement.BookEntryActivity;
import com.example.librarymanagement.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment1 extends Fragment {

    private HomeViewModel homeViewModel;
    View root;
    ArrayList<String> books,ids;
    ArrayAdapter arrayAdapter ;
    EditText searchBook;
    ListView listView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home1, container, false);

        searchBook = root.findViewById(R.id.searchBook);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Book");
        listView = root.findViewById(R.id.listView2);
        books= new ArrayList<String>();
        ids= new ArrayList<String>();
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,books);
        listView.setAdapter(arrayAdapter);

        query.orderByDescending("updatedAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for(ParseObject object:objects){
                        books.add(object.getString("title"));
                        ids.add(object.getObjectId());
                        arrayAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), BookEntryActivity.class);
                intent.putExtra("objectId",ids.get(position));
                Log.i("id", ids.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
                alertDialogue.setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle("Book Lost!!?")
                        .setMessage("Are you sure you want to delete this book?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Book");
                                query1.getInBackground(ids.get(position), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        object.deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Toast.makeText(getActivity(), "Book Deleted", Toast.LENGTH_SHORT).show();
                                                books.remove(position);
                                                ids.remove(position);
                                                arrayAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });
                            }
                        }).setNegativeButton("No", null).show();

                return true;
            }
        });

        Button button = (Button) root.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name= searchBook.getText().toString();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Book");

                query.whereContains("title",name);

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e==null){
                            if(objects.size()>0) {
                                books.clear();
                                ids.clear();
                                for (ParseObject object : objects) {

                                    books.add(object.getString("title"));
                                    ids.add(object.getObjectId());
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            }else{
                                Toast.makeText(getActivity(), "No Matches Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        Button addBook = (Button) root.findViewById(R.id.addBookButton);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BookEntryActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}