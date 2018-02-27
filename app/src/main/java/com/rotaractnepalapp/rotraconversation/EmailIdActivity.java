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

public class EmailIdActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mEmailID;
    private Button mSaveEmailIDBtn;

    //firebase
    private DatabaseReference mEmailIDDatabase;
    private FirebaseUser mCurrentUser;

    //progress dialog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_id);

        //database
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mEmailIDDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = (Toolbar) findViewById(R.id.emailid_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Email ID");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String contactno_value = getIntent().getStringExtra("email");

        mEmailID = (TextView) findViewById(R.id.emailid_input);
        mSaveEmailIDBtn = (Button) findViewById(R.id.emailid_save_btn);

        mEmailID.getEditableText().toString();
        mEmailID.setText(contactno_value);

        mSaveEmailIDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //progress
                mProgress = new ProgressDialog(EmailIdActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we update your email id.");
                mProgress.show();

                String email = mEmailID.getEditableText().toString();
                mEmailIDDatabase.child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                        }else {
                            Toast.makeText(getApplicationContext(),"There was some error to update your address.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
