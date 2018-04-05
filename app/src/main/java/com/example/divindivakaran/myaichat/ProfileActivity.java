package com.example.divindivakaran.myaichat;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    private ImageView mImageView;
    private  TextView mProfileName,mProfileStatus;
    private Button mSendRequestbutton,mDeclineButton;
    private DatabaseReference mUsersDatabase;

    private  DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationsDatabase;

    private ProgressDialog mProgressDialog;
    private String mCurrent_state;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mRootRef;
    private String currentUserID;
//    private DatabaseReference mUserRef;
//    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id=getIntent().getStringExtra("user_id");
        mUsersDatabase= FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        mFriendRequestDatabase=FirebaseDatabase.getInstance().getReference("Friend_req");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        currentUserID=mCurrentUser.getUid();

        mRootRef=FirebaseDatabase.getInstance().getReference();


//        mUserRef.child("online").setValue("true");



        mFriendsDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationsDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");


        mImageView=(ImageView)findViewById(R.id.profile_image);
        mProfileName=(TextView)findViewById(R.id.profile_display_name);
        mProfileStatus=(TextView)findViewById(R.id.profile_status);
//        mProfielFriendsCount=(TextView)findViewById(R.id.profile_total_friends);
        mSendRequestbutton=(Button)findViewById(R.id.profile_send_request_btn);
        mDeclineButton=(Button)findViewById(R.id.profile_decline_friend_btn);


        mCurrent_state="not_friends";

        mDeclineButton.setVisibility(View.INVISIBLE);
        mDeclineButton.setEnabled(false);



        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data.");
        mProgressDialog.setMessage("Please wait while we load user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();



        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                String uid=currentUserID;
                String cuid=mUsersDatabase.child("Users").child(user_id).getKey();

                if (uid.equals(cuid))
                {

//                    Toast.makeText(ProfileActivity.this,uid +"and"+cuid ,Toast.LENGTH_LONG).show();
//                    Log.w("myApp", uid+"    and   "+cuid);
                    mSendRequestbutton.setVisibility(View.INVISIBLE);

                }




                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mImageView);

                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)){

                            String req_type= dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")){


                                mCurrent_state="req_received";
                                mSendRequestbutton.setText("Accept Friend Request");
                                mDeclineButton.setVisibility(View.VISIBLE);
                                mDeclineButton.setEnabled(true);

                            }else if (req_type.equals("sent")){
                                mCurrent_state="req_sent";
                                mSendRequestbutton.setText("Cancel Friend request");
                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);

                            }
                            mProgressDialog.dismiss();

                        }else{

                            mFriendsDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    if (dataSnapshot.hasChild(user_id)){


                                        mCurrent_state="friends";
                                        mSendRequestbutton.setText("Unfriend This Person");
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);

                                    }

                                    mProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();
                                }
                            });
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSendRequestbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                mSendRequestbutton.setEnabled(false);


                if (mCurrent_state.equals("not_friends")){



                    DatabaseReference newNotificationRef=mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId=newNotificationRef.getKey();


                    HashMap<String, String> notificationData=new HashMap<>();
                    notificationData.put("from",mCurrentUser.getUid());
                    notificationData.put("type","request");

                    Map requestMap= new HashMap();
                    requestMap.put("Friend_req/"+mCurrentUser.getUid()+"/"+user_id+"/request_type","sent");
                    requestMap.put("Friend_req/"+user_id+"/"+mCurrentUser.getUid()+"/request_type","received");
                    requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError!=null){

                                Toast.makeText(ProfileActivity.this,"Ther was some error sending friend request",Toast.LENGTH_SHORT).show();
                            }

                            mSendRequestbutton.setText("Cancel Friend Request");
                            mSendRequestbutton.setEnabled(true);
                            mCurrent_state="req_sent";

                            mDeclineButton.setVisibility(View.INVISIBLE);
                            mDeclineButton.setEnabled(false);

                        }
                    });



                }

                //cancel request state

                if (mCurrent_state.equals("req_sent")){

                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    mSendRequestbutton.setEnabled(true);
                                    mCurrent_state="not_friends";
                                    mSendRequestbutton.setText("Send Friend Request");

                                    mDeclineButton.setVisibility(View.INVISIBLE);
                                    mDeclineButton.setEnabled(false);


                                }
                            });

                        }
                    });
                }


                //request received state


                if (mCurrent_state.equals("req_received")){


                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap=new HashMap();
                    friendsMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id+"/date",currentDate);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid()+"/date",currentDate);

//
//                    friendsMap.put("Friend_req/"+mCurrentUser.getUid()+"/"+user_id,null);
//                    friendsMap.put("Friends_req/"+user_id+"/"+mCurrentUser.getUid(),null);



                    mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });



                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError==null){

                                mSendRequestbutton.setEnabled(true);
                                mCurrent_state="friends";
                                mSendRequestbutton.setText("Unfriend This Person");

                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);

                            }else{

                                String error=databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }


                //--------------------unfriend-------------



                if (mCurrent_state.equals("friends")){

                    Map unfriendMap=new HashMap();
                    unfriendMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id,null);
                    unfriendMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid(),null);



                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError==null){




                                mCurrent_state="not_friends";
                                mSendRequestbutton.setText("Send Friend Request");

                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            }else{

                                String error=databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }
                            mSendRequestbutton.setEnabled(true);

                        }
                    });





                }



                //--------------------decline friend requset-----------


            }
        });


        mDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                    Map declineFriendMap=new HashMap();
                    declineFriendMap.put("Friend_req/"+mCurrentUser.getUid()+"/"+user_id,null);
                    declineFriendMap.put("Friend_req/"+user_id+"/"+mCurrentUser.getUid(),null);



                    mRootRef.updateChildren(declineFriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError==null){




                                mCurrent_state="not_friends";
                                mSendRequestbutton.setText("Send Friend Request");

                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            }else{

                                String error=databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }
                            mSendRequestbutton.setEnabled(true);

                        }
                    });


                }


        });


    }
}
