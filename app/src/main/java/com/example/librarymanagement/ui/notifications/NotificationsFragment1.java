package com.example.librarymanagement.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.librarymanagement.MainActivity;
import com.example.librarymanagement.R;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class NotificationsFragment1 extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications1, container, false);

        TextView userInfo = root.findViewById(R.id.userInfoTextView);

        String s = "Username : "+ ParseUser.getCurrentUser().getString("username")+"\n\nEmail-id : "+ ParseUser.getCurrentUser().getString("email")+"\n\nRole : "+  ParseUser.getCurrentUser().getString("role");

        userInfo.setText(s);

        Button button = (Button) root.findViewById(R.id.logOut);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });


        return root;
    }
}