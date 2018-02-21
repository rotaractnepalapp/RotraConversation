package com.rotaractnepalapp.rotraconversation;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mStatus;
    private Button mSaveBtn;

    //firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //progress dialog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //database
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = (Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status");

        mStatus = (TextView) findViewById(R.id.status_input);
        mSaveBtn = (Button) findViewById(R.id.status_save_btn);

        mStatus.getEditableText().toString();
        mStatus.setText(status_value);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //progress
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we update your status.");
                mProgress.show();

                String status = mStatus.getEditableText().toString();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                        }else {
                            Toast.makeText(getApplicationContext(),"There was some error to update your status",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
