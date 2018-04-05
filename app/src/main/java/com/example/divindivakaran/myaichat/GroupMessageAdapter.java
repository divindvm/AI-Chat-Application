package com.example.divindivakaran.myaichat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by divin.divakaran on 9/8/2017.
 */

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private String display_name;
    private  String image;
    private String groupid;
    private DatabaseReference mUsersDatabase;
//    private DatabaseReference mGroupsDatabase;
    public GroupMessageAdapter(List<Messages> mMessageList){

        this.mMessageList=mMessageList;
    }
    public GroupMessageAdapter(List<Messages> mMessageList,String groupid){

        this.mMessageList=mMessageList;
        this.groupid=groupid;
    }
    public GroupMessageAdapter(List<Messages> mMessageList,String groupid,String display_name, String image){

        this.mMessageList=mMessageList;
        this.groupid=groupid;
        this.display_name=display_name;
        this.image=image;
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_message_single_layout,parent,false);
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference("Users");
        mUsersDatabase.keepSynced(true);

//        mGroupsDatabase= FirebaseDatabase.getInstance().getReference("Users");
//        mGroupsDatabase.keepSynced(true);

        return new MessageViewHolder(v);

    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profileImage;
        public RelativeLayout rr;
        public TextView messageName;
        public MessageViewHolder(View itemView) {
            super(itemView);


            messageText=(TextView)itemView.findViewById(R.id.message_text_layout);
            profileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_layout);
            rr=(RelativeLayout)itemView.findViewById(R.id.group_message_single_layout);
            messageName=(TextView)itemView.findViewById(R.id.message_name);

        }
    }


    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {



        String current_user_id=mAuth.getCurrentUser().getUid();

        Messages c=mMessageList.get(position);

        String from_user=c.getFrom();
        String from_user_name=c.getMessage();
//        long time=c.getTime();
//        String from_user_image=c.getUserImage();

//        Log.d("abc","user name : "+time);
//        Log.d("abc","user Image Uri : "+image);

        mUsersDatabase.child(from_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                display_name=dataSnapshot.child("name").getValue().toString();
                image=dataSnapshot.child("image").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)holder.rr.getLayoutParams();
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        holder.rr.setLayoutParams(params2);


        if (from_user.equals(current_user_id)){

//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.rr.getLayoutParams();
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            holder.rr.setLayoutParams(params);

//            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);
            holder.profileImage.setVisibility(View.INVISIBLE);
            holder.messageName.setVisibility(View.INVISIBLE);
            holder.messageText.setGravity(Gravity.RIGHT);
            holder.messageText.setBackgroundResource(R.drawable.current_user_message_background);
            holder.messageText.setText(c.getMessage());

        }else if (from_user.equals(groupid)){

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.rr.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.rr.setLayoutParams(params);
//
            holder.profileImage.setImageResource(R.drawable.cabot);
//            holder.messageText.setBackgroundColor(Color.rgb(208,214,224));
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageName.setText("Cabot");
            holder.messageText.setTextColor(Color.BLACK);
            holder.profileImage.setVisibility(View.VISIBLE);


            holder.messageText.setText(c.getMessage());
//           holder.messageText.setGravity(Gravity.RIGHT);

        }

        else{

//            mUsersDatabase.child(from_user).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    display_name=dataSnapshot.child("name").getValue().toString();
//                    image=dataSnapshot.child("image").getValue().toString();
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });


//            CircleImageView userImageView=(CircleImageView)holder.findViewById(R.id.user_single_image);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.rr.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.rr.setLayoutParams(params);
            Picasso.with(holder.profileImage.getContext()).load(image).placeholder(R.drawable.default_avatar).into(holder.profileImage);
            holder.messageName.setText(display_name);
//
//            holder.profileImage.setImageResource(R.drawable.default_avatar);
            holder.profileImage.setVisibility(View.VISIBLE);
//            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setBackgroundResource(R.drawable.user_message_text_bg);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setText(c.getMessage());

        }
//        holder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}



