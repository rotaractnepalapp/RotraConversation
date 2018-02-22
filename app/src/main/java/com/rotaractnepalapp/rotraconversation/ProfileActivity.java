package com.rotaractnepalapp.rotraconversation;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileRIDNo, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendRequestBtn;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mProfileImage = (ImageView) findViewById(R.id.profile_displayImage);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileRIDNo = (TextView) findViewById(R.id.profile_ridno);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendRequestBtn = (Button) findViewById(R.id.sendRequest_btn);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Profile");
        mProgressDialog.setMessage("Please wait while we load user profile.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String ridno = dataSnapshot.child("ridno").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileRIDNo.setText(ridno);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
