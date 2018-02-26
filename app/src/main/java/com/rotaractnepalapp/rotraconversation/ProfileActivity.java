package com.rotaractnepalapp.rotraconversation;

import android.app.ProgressDialog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileRIDNo, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendRequestBtn, mDeclineBtn;

    private DatabaseReference mUsersDatabase, mFriendRequestDatabase, mFriendDatabase, mNotificationDatabase;
    private FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        mProfileImage = (CircleImageView) findViewById(R.id.profile_displayImage);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileRIDNo = (TextView) findViewById(R.id.profile_ridno);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendRequestBtn = (Button) findViewById(R.id.sendRequest_btn);
        mDeclineBtn = (Button) findViewById(R.id.declineRequest_btn);

        mCurrent_state = "not_friends";

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

                //----------------Friends list / Request Feature
                mFriendRequestDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")){

                                mCurrent_state = "req_received";
                                mProfileSendRequestBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);

                            }else if (req_type.equals("sent")){
                                mCurrent_state = "req_sent";
                                mProfileSendRequestBtn.setText("Cancel Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }
                            mProgressDialog.dismiss();
                        }else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)){
                                        mCurrent_state = "friends";
                                        mProfileSendRequestBtn.setText("Unfriend this Person");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                    }
                                    mProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendRequestBtn.setEnabled(false);


                //----------------Not Friends State----------------
                if (mCurrent_state.equals("not_friends")){
                    mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrent_user.getUid());
                                        notificationData.put("type", "request");


                                        mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mCurrent_state = "req_sent";
                                                mProfileSendRequestBtn.setText("Cancel Friend Request");

                                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                                mDeclineBtn.setEnabled(false);
                                            }
                                        });

                                        //Toast.makeText(ProfileActivity.this,"Request sent Successfully.",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else {
                                Toast.makeText(ProfileActivity.this,"Failed Sending Request.",Toast.LENGTH_LONG).show();
                            }
                            mProfileSendRequestBtn.setEnabled(true);
                        }
                    });
                }

                //----------------Cancel Request State----------------
                if (mCurrent_state.equals("req_sent")){
                    mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendRequestBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendRequestBtn.setText("Send Friend Request");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                //---------------Request Recieved State-----------------
                if (mCurrent_state.equals("req_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProfileSendRequestBtn.setEnabled(true);
                                                    mCurrent_state = "friends";
                                                    mProfileSendRequestBtn.setText("Unfriend this Person");

                                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                                    mDeclineBtn.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                //--------Unfriend state----------------------
                if (mCurrent_state.equals("friends")){
                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendRequestBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendRequestBtn.setText("Send Friend Request");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }
            }
        });

    }
}
