package com.rotaractnepalapp.rotraconversation;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayName, mRIDno, mEmail, mPassword;
    private Button createBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (EditText) findViewById(R.id.reg_display_name);
        mRIDno = (EditText) findViewById(R.id.reg_ridno);
        mEmail = (EditText) findViewById(R.id.reg_email);
        mPassword = (EditText) findViewById(R.id.reg_password);
        createBtn = (Button) findViewById(R.id.reg_create_btn);
        createBtn.setPaintFlags(createBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = mDisplayName.getText().toString();
                String ridno = mRIDno.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                register_user(display_name, ridno, email, password);
            }
        });
    }

    private void register_user(String display_name, String ridno, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Sucessful Register",Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else {
                    Toast.makeText(RegisterActivity.this,"You got some error",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
