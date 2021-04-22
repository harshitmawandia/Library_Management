package com.example.librarymanagement.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.librarymanagement.MainActivity;
import com.example.librarymanagement.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Method;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        TextView userInfo = root.findViewById(R.id.userInfoTextView);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User2");
        try {
            query.whereMatches("username", ParseUser.getCurrentUser().getUsername());
            Log.i("user", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    Log.i("user", objects.get(0).getString("role"));
                    String role = objects.get(0).getString("role");
                    String s = "Username : " + ParseUser.getCurrentUser().getString("username") + "\n\nEmail-id : " + ParseUser.getCurrentUser().getString("email") + "\n\nRole : " + role;
                    userInfo.setText(s);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }






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
                            try {
                                MainActivity.mGoogleSignInClient.signOut();
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }

                            startActivity(intent);
                        }
                    }
                });
            }
        });


        return root;
    }
}