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

public class AddressActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mAddress;
    private Button mSaveAddressBtn;

    //firebase
    private DatabaseReference mAddressDatabase;
    private FirebaseUser mCurrentUser;

    //progress dialog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        //database
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mAddressDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = (Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String address_value = getIntent().getStringExtra("address");

        mAddress = (TextView) findViewById(R.id.address_input);
        mSaveAddressBtn = (Button) findViewById(R.id.address_save_btn);

        mAddress.getEditableText().toString();
        mAddress.setText(address_value);

        mSaveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //progress
                mProgress = new ProgressDialog(AddressActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we update your status.");
                mProgress.show();

                String address = mAddress.getEditableText().toString();
                mAddressDatabase.child("address").setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
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
