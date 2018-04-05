package com.example.divindivakaran.myaichat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mGroupsList;
    private View mMainViewRequest;
    private Button mAddNewGroupbtn;
    private DatabaseReference mGroupsDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private DatabaseReference mUsersDatabase;
    private String user;



    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mMainViewRequest=inflater.inflate(R.layout.fragment_chats,container,false);
        mGroupsList=(RecyclerView)mMainViewRequest.findViewById(R.id.groups_list);


        mAddNewGroupbtn=(Button)mMainViewRequest.findViewById(R.id.add_new_gruop_btn);

        mAuth=FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser()!=null){
            mCurrent_user_id=mAuth.getCurrentUser().getUid();
        }

        mGroupsDatabase= FirebaseDatabase.getInstance().getReference().child("Groups");
        mGroupsDatabase.keepSynced(true);

        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        user=mGroupsDatabase.getKey();
        Log.w("myApp", user);

        mGroupsList.setHasFixedSize(true);
        mGroupsList.setLayoutManager(new LinearLayoutManager(getContext()));


        // Inflate the layout for this fragment
        return mMainViewRequest;



    }

    @Override
    public void onStart() {
        super.onStart();


//        Toast.makeText(getActivity(),"Error Signing In. Incorrect email address or password",Toast.LENGTH_LONG).show();






        mAddNewGroupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mainIntent =new Intent(getContext(), GroupCreateActivity.class);

                startActivity(mainIntent);

            }
        });






    FirebaseRecyclerAdapter<Messages,ChatsFragment.GroupsViewHolder> groupsRecyclerViewAdapter= new FirebaseRecyclerAdapter<Messages,GroupsViewHolder>(

            Messages.class,
            R.layout.users_single_layou,
            ChatsFragment.GroupsViewHolder.class,
            mGroupsDatabase

    ) {

        @Override
        protected void populateViewHolder(final ChatsFragment.GroupsViewHolder viewHolder, Messages model, int position) {

//              viewHolder.setDate(model.getDate());

            final String list_user_id=getRef(position).getKey();


            Log.w("myApp", "First part");
            mGroupsDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Log.w("myApp","id is :"+list_user_id);


                    String userThumb=dataSnapshot.child("thumb_image").getValue().toString();
                    final String name=dataSnapshot.child("name").getValue().toString();
                    final String token=dataSnapshot.child("token").getValue().toString();

                    String status=dataSnapshot.child("status").getValue().toString();

                    Log.w("myApp", "Second part");

//                        if (dataSnapshot.hasChild("online")){
//
//                            String userOnline=dataSnapshot.child("online").getValue().toString();
////                            viewHolder.setUserOnline(userOnline);
//
//                        }


//                    viewHolder.setName(userName);

                    viewHolder.setName(name);
                    viewHolder.setUserImage(userThumb,getContext());
                    viewHolder.setUserStatus(status,getContext());


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
//
//                            Intent profileIntent= new Intent(getContext(), ProfileActivity.class);
//                            profileIntent.putExtra("user_id",list_user_id);
//                            startActivity(profileIntent);

                            Intent chatIntent= new Intent(getContext(),GroupChat.class);
                            chatIntent.putExtra("user_id",list_user_id);
                            chatIntent.putExtra("user_name",name);
                            chatIntent.putExtra("token",token);
                            startActivity(chatIntent);


                        }
                    });



                }
                @Override
                public void onCancelled(DatabaseError databaseError) {


//                    Toast.makeText(getActivity(), "You have no new Friend requests", Toast.LENGTH_SHORT).show();

                }

            });

        }
    };

        mGroupsList.setAdapter(groupsRecyclerViewAdapter);


    }









    public static class GroupsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public GroupsViewHolder(View itemView) {
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
        public void setUserStatus(String status, Context ctx){


            TextView userStatusView=(TextView)mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);

        }



    }


}
