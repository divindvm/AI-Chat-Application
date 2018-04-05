package com.example.divindivakaran.myaichat;

import android.content.Context;
import android.content.Intent;
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

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar=(Toolbar)findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsersDataBase= FirebaseDatabase.getInstance().getReference().child("Users");


        mUsersList=(RecyclerView)findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.users_single_layou,
                UsersViewHolder.class,
                mUsersDataBase


        ) {
            @Override
            public void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {

                UsersViewHolder.setName(model.getName());
                UsersViewHolder.setUserStatus(model.getStatus());
                UsersViewHolder.setUserImage(model.getThumb_image(),getApplicationContext());
                final String user_id=getRef(position).getKey();

                UsersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent= new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);


                    }
                });

            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);



    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        static View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public static void setName(String name){

            TextView mUserNameView =mView.findViewById(R.id.user_single_name);
            mUserNameView.setText(name);

        }
        public static void setUserStatus(String status){

            TextView mUserStatusView =mView.findViewById(R.id.user_single_status);
            mUserStatusView.setText(status);

        }
        public static void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);


        }




    }
}
