package com.example.librarymanagement.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.librarymanagement.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment2 extends Fragment {

    private HomeViewModel homeViewModel;
    View root;
    ArrayList<String> users,ids;
    ArrayAdapter arrayAdapter ;

    ListView listView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home2, container, false);
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User2");
        listView = root.findViewById(R.id.listView);
        users= new ArrayList<String>();
        ids= new ArrayList<String>();
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,users);
        listView.setAdapter(arrayAdapter);

        query.whereNotEqualTo("role","admin");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for(ParseObject object:objects){
                        users.add("User: "+object.getString("username")+"\nRole: "+object.getString("role"));
                        ids.add(object.getObjectId());
                        arrayAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                alertDialogue.setIcon(R.drawable.profile)
                        .setTitle("Change Roles")
                        .setMessage(users.get(position))
                        .setPositiveButton("User", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("User2");
                                query1.getInBackground(ids.get(position), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        object.put("role","user");
                                        object.saveInBackground();
                                        users.set(position,object.getString("username")+"\nuser");
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Librarian", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("User2");
                                query1.getInBackground(ids.get(position), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {

                                        object.put("role","librarian");
                                        object.saveInBackground();

                                        users.set(position,object.getString("username")+"\nlibrarian");
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).show();
            }
        });



        return root;
    }
}