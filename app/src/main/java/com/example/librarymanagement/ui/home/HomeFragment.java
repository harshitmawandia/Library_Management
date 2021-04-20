package com.example.librarymanagement.ui.home;

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

import com.example.librarymanagement.BookInfoActivity;
import com.example.librarymanagement.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    View root;
    ArrayList<String> books,ids;
    ArrayAdapter arrayAdapter ;
    EditText searchBook;
    ListView listView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

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
                Intent intent = new Intent(getContext(), BookInfoActivity.class);
                intent.putExtra("objectId",ids.get(position));
                Log.i("id", ids.get(position));
                startActivity(intent);
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
                                for (ParseObject object : objects) {
                                    books.clear();
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

        return root;
    }
}