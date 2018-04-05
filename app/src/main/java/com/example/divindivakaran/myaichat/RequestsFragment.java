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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.security.PrivateKey;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    private RecyclerView mRequestList;
    private DatabaseReference mRequestDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainViewRequest;
    private DatabaseReference mUsersDatabase;
    private String user;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        mMainViewRequest=inflater.inflate(R.layout.fragment_requests,container,false);
//        mRequestList=(RecyclerView)mMainView.findViewById(R.id.req);

            mRequestList=(RecyclerView)mMainViewRequest.findViewById(R.id.request_list);

            mAuth=FirebaseAuth.getInstance();

            if (mAuth.getCurrentUser()!=null){
                mCurrent_user_id=mAuth.getCurrentUser().getUid();
            }

                mRequestDatabase= FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
                mRequestDatabase.keepSynced(true);

                mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
                mUsersDatabase.keepSynced(true);

                user=mRequestDatabase.getKey();
                    Log.w("myApp", user);

        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainViewRequest;
    }
//
    @Override
    public void onStart() {
        super.onStart();

        Log.w("myApp","oncreate method started");

        FirebaseRecyclerAdapter<Messages,RequestViewHolder> requestsRecyclerViewAdapter= new FirebaseRecyclerAdapter<Messages,RequestViewHolder>(

                Messages.class,
                R.layout.users_single_layou,
                RequestViewHolder.class,
                mRequestDatabase

        ) {

            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Messages model, int position) {

//              viewHolder.setDate(model.getDate());

                final String list_user_id=getRef(position).getKey();


                Log.w("myApp", "First part");
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        final String userName=dataSnapshot.child("name").getValue().toString();
                        String userThumb=dataSnapshot.child("thumb_image").getValue().toString();

                        Log.w("myApp", "Second part");

//                        if (dataSnapshot.hasChild("online")){
//
//                            String userOnline=dataSnapshot.child("online").getValue().toString();
////                            viewHolder.setUserOnline(userOnline);
//
//                        }


                        viewHolder.setName(userName);
                        viewHolder.setUserImage(userThumb,getContext());

                        Log.w("myApp", "Third part");

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


//                                CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
//
//                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
//                                //  builder.setTitle("");
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                        if (i==0){
//
//
//                                            Intent profileIntent= new Intent(getContext(), ProfileActivity.class);
//                                            profileIntent.putExtra("user_id",list_user_id);
//                                            startActivity(profileIntent);
//
//                                            Log.w("myApp", "Fourth part");
//                                        }
//                                        if (i==1){
//
//                                            Log.w("myApp", "Fifth part");
//                                            Intent chatIntent= new Intent(getContext(),ChatActivity.class);
//                                            chatIntent.putExtra("user_id",list_user_id);
//                                            chatIntent.putExtra("user_name",userName);
//                                            startActivity(chatIntent);
//
//                                        }
//
//                                    }
//                                });
//
//                                builder.show();
//                                Log.w("myApp", "Seventh part");

                                           Intent profileIntent= new Intent(getContext(), ProfileActivity.class);
                                           profileIntent.putExtra("user_id",list_user_id);
                                           startActivity(profileIntent);


                            }
                        });



                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {


//                        Toast.makeText(getActivity(), "You have no new Friend requests", Toast.LENGTH_SHORT).show();

                    }

                });

            }
        };

        mRequestList.setAdapter(requestsRecyclerViewAdapter);

    }





    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
//        public void setDate(String date){
//
//            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_status);
//            userNameView.setText(date);
//
//        }
        public void setName(String name){

            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
            Log.w("myApp", "9th part");
        }
        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
            Log.w("myApp", "8th part");

        }



    }
}





