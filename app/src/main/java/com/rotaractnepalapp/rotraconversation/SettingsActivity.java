package com.rotaractnepalapp.rotraconversation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    //for layout
    private CircleImageView mImage;
    private TextView mName, mRIDNo, mStatus;
    private ImageView mImageBtn, mStatusBtn;
    private Button mInformationBtn;

    private static final int GALLERY_PICK = 1;

    //storage firebase
    private StorageReference mImageStorage;

    private ProgressDialog mImageLoadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mImage = (CircleImageView) findViewById(R.id.setting_image);
        mName = (TextView) findViewById(R.id.setting_display_name);
        mRIDNo = (TextView) findViewById(R.id.setting_ridno);
        mStatus = (TextView) findViewById(R.id.setting_status);

        mStatusBtn = (ImageView) findViewById(R.id.setting_status_btn);
        mImageBtn = (ImageView) findViewById(R.id.setting_image_btn);
        mInformationBtn = (Button) findViewById(R.id.setting_information_btn);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String ridno = dataSnapshot.child("ridno").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                //to change data in layout
                mName.setText(name);
                mRIDNo.setText(ridno);
                mStatus.setText(status);

                if (!image.equals("default")){
                    //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mImage);
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(mImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mImage);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mStatus.getText().toString();

                Intent statusIntent = new Intent(SettingsActivity.this,StatusActivity.class);
                statusIntent.putExtra("status", status_value);
                startActivity(statusIntent);
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image/*");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallaryIntent, "SELECT IMAGE"),GALLERY_PICK);

            }
        });

        mInformationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent informationIntent = new Intent(SettingsActivity.this,UserInformationActivity.class);
                startActivity(informationIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500,500)
                    .start(this);

        }

        //to get crop image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageLoadProgress = new ProgressDialog(SettingsActivity.this);
                mImageLoadProgress.setTitle("Uploading Image...");
                mImageLoadProgress.setMessage("Please wait while we upload and process an imaage.");
                mImageLoadProgress.setCanceledOnTouchOutside(false);
                mImageLoadProgress.show();

                Uri resultUri = result.getUri();
                File thumb_filePath = new File(resultUri.getPath());
                String current_user_id = mCurrentUser.getUid();
                //image to bitmap
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(SettingsActivity.this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            //get and store image url in database
                            final String download_url = task.getResult().getDownloadUrl().toString();

                            //retrieve thumb image
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()){

                                        Map update_hasMap = new HashMap<>();
                                        update_hasMap.put("image", download_url);
                                        update_hasMap.put("thumb_image", thumb_downloadUrl);

                                        mUserDatabase.updateChildren(update_hasMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    mImageLoadProgress.dismiss();
                                                    Toast.makeText(SettingsActivity.this,"Success Uploading",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(SettingsActivity.this,"Error in Uploading thumbnail.",Toast.LENGTH_LONG).show();
                                        mImageLoadProgress.dismiss();
                                    }

                                }
                            });

                        }else {
                            Toast.makeText(SettingsActivity.this,"Error in Uploading",Toast.LENGTH_LONG).show();
                            mImageLoadProgress.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    //to generate random name for random profile image
    public static String random() {

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
