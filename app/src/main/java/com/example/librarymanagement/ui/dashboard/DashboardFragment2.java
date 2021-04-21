package com.example.librarymanagement.ui.dashboard;

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
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.librarymanagement.R;
import com.example.librarymanagement.RequestActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment2 extends Fragment {

    ListView listView;
    ArrayList<String> user,reviews,ids;
    ArrayAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard2, container, false);

        listView = (ListView) root.findViewById(R.id.reviewList);
        user = new ArrayList<String>();
        reviews = new ArrayList<String>();
        ids = new ArrayList<String>();
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,reviews);
        listView.setAdapter(adapter);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Record");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for(ParseObject object:objects){
                        if(object.has("review")){
                            user.add(object.getString("borrower"));
                            reviews.add(object.getString("review"));
                            ids.add(object.getObjectId());
                            adapter.notifyDataSetChanged();
                        }

                    }

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
                alertDialogue.setIcon(android.R.drawable.stat_sys_warning)
                        .setTitle("Offensive Review??")
                        .setMessage("Do you want to warn user and delete review?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("User2");
                                query1.whereMatches("username",user.get(position));
                                query1.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        ParseObject user1 =  objects.get(0);
                                        user1.put("warning",true);
                                        user1.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e==null) {
                                                    Log.i("Warning", String.valueOf(user1.getBoolean("warning")));
                                                    Log.i("User", user1.getString("username"));
                                                    user.remove(position);
                                                    reviews.remove(position);

                                                    adapter.notifyDataSetChanged();
                                                    query.getInBackground(ids.get(position), new GetCallback<ParseObject>() {
                                                        @Override
                                                        public void done(ParseObject object, ParseException e) {
                                                            object.remove("review");
                                                            object.saveInBackground();
                                                            ids.remove(position);
                                                        }
                                                    });
                                                }else {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    }
                                });
                            }
                        }).setNegativeButton("No",null).show();

            }
        });

        return root;
    }
}