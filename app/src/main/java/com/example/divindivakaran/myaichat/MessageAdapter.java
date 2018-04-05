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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private String from_user;
    private String image;

    public MessageAdapter(List<Messages> mMessageList){

        this.mMessageList=mMessageList;
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layou,parent,false);
        mAuth = FirebaseAuth.getInstance();


        return new MessageViewHolder(v);

    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profileImage;
        public RelativeLayout rr;
        public MessageViewHolder(View itemView) {
            super(itemView);


                messageText=(TextView)itemView.findViewById(R.id.message_text_layout);
                profileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_layout);
                rr=(RelativeLayout)itemView.findViewById(R.id.message_single_layout);

        }
    }



    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

//        if (mAuth.getCurrentUser()!=null){
//
//
//
//        }

        String current_user_id=mAuth.getCurrentUser().getUid();

        Messages c=mMessageList.get(position);

        from_user=c.getFrom();
        Log.d("aa","From google"+from_user);
        if (from_user.equals(current_user_id)){

            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);
            holder.profileImage.setVisibility(View.INVISIBLE);
            holder.messageText.setGravity(Gravity.RIGHT);
            holder.messageText.setBackgroundResource(R.drawable.current_user_message_background);


        }else if (from_user.equals("ahE6YBgKKASILpqpk76CXiE0wxT2")){

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.rr.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.rr.setLayoutParams(params);
            holder.profileImage.setImageResource(R.drawable.cabot);
//            holder.messageText.setBackground(R.drawable.user_message_text_bg.);
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);

            holder.messageText.setTextColor(Color.BLACK);
            holder.profileImage.setVisibility(View.VISIBLE);


//           holder.messageText.setGravity(Gravity.RIGHT);

        }

        else{



            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.rr.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.rr.setLayoutParams(params);
//            holder.profileImage.setImageResource(R.drawable.default_avatar);

            Picasso.with(holder.profileImage.getContext()).load(image).placeholder(R.drawable.default_avatar).into(holder.profileImage);
            holder.profileImage.setVisibility(View.VISIBLE);
            holder.messageText.setBackgroundResource(R.drawable.user_message_text_bg);
            holder.messageText.setTextColor(Color.BLACK);



        }
        holder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}



