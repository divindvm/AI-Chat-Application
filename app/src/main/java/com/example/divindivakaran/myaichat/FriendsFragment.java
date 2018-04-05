package com.example.divindivakaran.myaichat;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.PrivateKey;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;
    private DatabaseReference mUsersDatabase;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView=inflater.inflate(R.layout.fragment_friends,container,false);
        mFriendsList=(RecyclerView)mMainView.findViewById(R.id.friends_list);
        mAuth=FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();


        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;

//
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsRecyclerViewAdapter=new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layou,
                FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewholder, Friends model, int i) {

                friendsViewholder.setDate(model.getDate());

                final String list_user_id=getRef(i).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName=dataSnapshot.child("name").getValue().toString();
                        String userThumb=dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")){

                            String userOnline=dataSnapshot.child("online").getValue().toString();
                            friendsViewholder.setUserOnline(userOnline);

                        }



                        friendsViewholder.setName(userName);
                        friendsViewholder.setUserImage(userThumb,getContext());

                        friendsViewholder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

//
//                                CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
//
//                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
//                                //  builder.setTitle("");
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                            if (i==0){
//
//
//                                                Intent profileIntent= new Intent(getContext(), ProfileActivity.class);
//                                                profileIntent.putExtra("user_id",list_user_id);
//                                                startActivity(profileIntent);
//                                            }
//                                            if (i==1){
//
//                                                Intent chatIntent= new Intent(getContext(),ChatActivity.class);
//                                                chatIntent.putExtra("user_id",list_user_id);
//                                                chatIntent.putExtra("user_name",userName);
//                                                startActivity(chatIntent);
//
//                                            }
//
//                                    }
//                                });
//
//                                builder.show();

                                Intent chatIntent= new Intent(getContext(),ChatActivity.class);
                                               chatIntent.putExtra("user_id",list_user_id);
                                               chatIntent.putExtra("user_name",userName);
                                               startActivity(chatIntent);


                            }
                        });

                        friendsViewholder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {

                                Intent profileIntent= new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("user_id",list_user_id);
                                startActivity(profileIntent);

                                return false;
                            }
                        });




                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setDate(String date){

            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);

        }
        public void setName(String name){

            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }
        public void setUserOnline(String online_status){

            ImageView userOnlineView=(ImageView)mView.findViewById(R.id.user_single_online);

            if (online_status.equals("true"))
            {

                userOnlineView.setVisibility(View.VISIBLE);
            }else{

                userOnlineView.setVisibility(View.INVISIBLE);
            }

        }


    }
}
