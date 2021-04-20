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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.librarymanagement.BookInfoActivity;
import com.example.librarymanagement.R;
import com.example.librarymanagement.RenewBook;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ListView listView = root.findViewById(R.id.dueDates);
        ArrayList<String> borrowedBooks = new ArrayList<String>();
        ArrayList<String> borrowedBookIds= new ArrayList<String>();

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, borrowedBooks);

        listView.setAdapter(adapter);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Book");

        query.whereContains("borrower",ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for(ParseObject object:objects){
                        borrowedBooks.add(object.getString("title") +"\n"+ new SimpleDateFormat("dd MMM yyyy").format(object.getDate("dueDate")));
                        borrowedBookIds.add(object.getObjectId());
                        adapter.notifyDataSetChanged();
                    }

                }else {
                    borrowedBooks.add("No Borrowed Books");
                    adapter.notifyDataSetChanged();
                }
            }
        });
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialogue.setIcon(R.drawable.profile)
                        .setTitle("Renew your Book")
                        .setMessage("Do you wish to renew your Book?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Book");
                                query1.getInBackground(borrowedBookIds.get(position), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        Intent intent = new Intent(getContext(), RenewBook.class);
                                        intent.putExtra("objectId",borrowedBookIds.get(position));
                                        Log.i("id", borrowedBookIds.get(position));
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).setNegativeButton("No", null).show();
            }
        });


        return root;
    }
}