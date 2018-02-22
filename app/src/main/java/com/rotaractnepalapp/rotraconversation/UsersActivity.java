package com.rotaractnepalapp.rotraconversation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mUserToolbar;
    private RecyclerView mUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mUserToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mUserToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsersList = (RecyclerView) findViewById(R.id.users_list);
    }
}
