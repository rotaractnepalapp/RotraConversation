package com.rotaractnepalapp.rotraconversation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mUserToolbar;
    private RecyclerView mUsersList;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mUserToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mUserToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.user_list_layout,
                UsersViewHolder.class,
                mUserDatabase

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                UsersViewHolder.setName(model.getName());
                UsersViewHolder.setStatus(model.getStatus());
                UsersViewHolder.setRIDNo(model.getRidno());
            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    //User list viewholder
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        static View mView;
        private CircleImageView mUserView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public static void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public static void setStatus(String status){
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }
        public static void setRIDNo(String ridno){
            TextView userRIDNoView = (TextView) mView.findViewById(R.id.user_single_ridno);
            userRIDNoView.setText(ridno);
        }
    }
}
