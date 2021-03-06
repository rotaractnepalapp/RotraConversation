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

public class ContactNoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mContactNo;
    private Button mSaveContactNoBtn;

    //firebase
    private DatabaseReference mContactNoDatabase;
    private FirebaseUser mCurrentUser;

    //progress dialog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_no);

        //database
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mContactNoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = (Toolbar) findViewById(R.id.contactno_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Contact No");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String contactno_value = getIntent().getStringExtra("contactno");

        mContactNo = (TextView) findViewById(R.id.contactno_input);
        mSaveContactNoBtn = (Button) findViewById(R.id.contactno_save_btn);

        mContactNo.getEditableText().toString();
        mContactNo.setText(contactno_value);

        mSaveContactNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //progress
                mProgress = new ProgressDialog(ContactNoActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we update your Contact no.");
                mProgress.show();

                String contactno = mContactNo.getEditableText().toString();
                mContactNoDatabase.child("contactno").setValue(contactno).addOnCompleteListener(new OnCompleteListener<Void>() {
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
