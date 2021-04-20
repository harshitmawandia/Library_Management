package com.example.librarymanagement.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.librarymanagement.BookInfoActivity;
import com.example.librarymanagement.R;
import com.example.librarymanagement.RequestActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment1 extends Fragment {

    private DashboardViewModel dashboardViewModel;
    ListView listView;
    ArrayList<String> bookId,title;
    ArrayAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard1, container, false);

        listView = (ListView) root.findViewById(R.id.requestListView);
        bookId = new ArrayList<String>();
        title = new ArrayList<String>();
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,title);
        listView.setAdapter(adapter);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Book");

        query.whereContains("availability","requested");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for(ParseObject object:objects){
                        title.add(object.getString("title"));
                        bookId.add(object.getObjectId());
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), RequestActivity.class);
                intent.putExtra("objectId",bookId.get(position));
                Log.i("id", bookId.get(position));
                startActivity(intent);
            }
        });

        return root;
    }
}