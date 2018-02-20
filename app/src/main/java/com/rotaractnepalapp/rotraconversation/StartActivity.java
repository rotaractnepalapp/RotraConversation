package com.rotaractnepalapp.rotraconversation;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mregBtn, mloginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mregBtn = (Button) findViewById(R.id.startRegButton);
        mloginBtn = (Button) findViewById(R.id.startLogInButton);
        mregBtn.setPaintFlags(mregBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mloginBtn.setPaintFlags(mloginBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mregBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

}
