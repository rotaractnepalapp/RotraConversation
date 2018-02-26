package com.rotaractnepalapp.rotraconversation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class UserInformationActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        mToolbar = (Toolbar) findViewById(R.id.information_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
