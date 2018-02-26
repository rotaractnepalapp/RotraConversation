package com.rotaractnepalapp.rotraconversation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayName, mRIDno, mEmail, mPassword;
    private Button createBtn;
    private Toolbar rtoolbar;
    private ProgressDialog mRegProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (EditText) findViewById(R.id.reg_display_name);
        mRIDno = (EditText) findViewById(R.id.reg_ridno);
        mEmail = (EditText) findViewById(R.id.reg_email);
        mPassword = (EditText) findViewById(R.id.reg_password);
        rtoolbar = (Toolbar) findViewById(R.id.register_toolbar);
        createBtn = (Button) findViewById(R.id.reg_create_btn);
        createBtn.setPaintFlags(createBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        //Toolbar set
        setSupportActionBar(rtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Registration
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = mDisplayName.getText().toString();
                String ridno = mRIDno.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                mRegProgress.setTitle("Registering User");
                mRegProgress.setMessage("Please wait while we create your account");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();
                register_user(display_name, ridno, email, password);
            }
        });
    }

    private void register_user(final String display_name, final String ridno, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //to get current user id from firebase
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    //to create user reg data into firebase --  FirebaseDatabase+DatabaseReference(Root)+child(users)+child(user id)
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("ridno", ridno);
                    userMap.put("status", "Hi there I'm using Rotra Conversation App.");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mRegProgress.dismiss();

                                String current_user_id = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this,"Sucessful Register",Toast.LENGTH_LONG).show();
                                        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }else {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this,"You got some error",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
