package com.example.divindivakaran.myaichat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;
import ai.api.RequestExtras;
import ai.api.android.AIDataService;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;
import java.util.Map;
import android.view.View;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements AIListener{


    public AIService aiService;
    public AIDataService aiDataService;


    public String image;
    private String mChatUser;
    private Toolbar mchatToolbar;
    private DatabaseReference mRootRef;

    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private ImageButton mChatAddbtn;
    private ImageButton mChatSendbtn;
    private EditText mMessageView;

    private RecyclerView mMessegesList;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mchatToolbar=(Toolbar)findViewById(R.id.chat_app_bar);



        setSupportActionBar(mchatToolbar);

        ActionBar actionBar=getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mCurrentUserId=mAuth.getCurrentUser().getUid();



        mChatUser=getIntent().getStringExtra("user_id");

        String userName=getIntent().getStringExtra("user_name");
//        getSupportActionBar().setTitle(userName);

        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=inflater.inflate(R.layout.chat_cutom_bar,null);

        actionBar.setCustomView(action_bar_view);


        mTitleView=(TextView)findViewById(R.id.custom_bar_title);
        mLastSeenView=(TextView)findViewById(R.id.custom_bar_seen);
        mProfileImage=(CircleImageView)findViewById(R.id.cutom_bar_image);


        mChatAddbtn=(ImageButton)findViewById(R.id.chat_add_btn);
        mChatSendbtn=(ImageButton)findViewById(R.id.chat_send_btn);
        mMessageView=(EditText)findViewById(R.id.chat_message_view);

        mAdapter=new MessageAdapter(messagesList);

        mMessegesList=(RecyclerView)findViewById(R.id.messages_list);

        mLinearLayout=new LinearLayoutManager(this);
        mMessegesList.setHasFixedSize(true);
        mMessegesList.setLayoutManager(mLinearLayout);

        mMessegesList.setAdapter(mAdapter);
        loadMessages();

        mTitleView.setText(userName);


        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String onLine=dataSnapshot.child("online").getValue().toString();
                image=dataSnapshot.child("image").getValue().toString();


                CircleImageView userImageView=(CircleImageView)findViewById(R.id.cutom_bar_image);
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.default_avatar).into(userImageView);


                if (onLine.equals("true")){

                    mLastSeenView.setText("Online");
                }else{

                    GetTimeAgo getTimeAgo=new GetTimeAgo();
                    long lastTime=Long.parseLong(onLine);
                    String lastSeenTime= getTimeAgo.getTimeAgo(lastTime,getApplicationContext());

                    mLastSeenView.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser)){

                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);


                    Map chatUserMap=new HashMap();
                    chatUserMap.put("Chat/"+mCurrentUserId+"/"+mChatUser,chatAddMap);
                    chatUserMap.put("Chat/"+mChatUser+"/"+mCurrentUserId,chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError!=null){


                                Log.d("Chat_Log",databaseError.getMessage().toString());
                            }

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mChatSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                sendMessage();
            }
        });

        mChatAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Intent mainIntent =new Intent(ChatActivity.this, AIChat.class);
////                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(mainIntent);
//                finish();






            }
        });

        mChatAddbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

//                Toast.makeText(ChatActivity.this,"hi r you on long click",Toast.LENGTH_SHORT).show();

                aiService.startListening();
                return true;
            }
        });




        final AIConfiguration config = new AIConfiguration("9fbbc0ff33b246aa83449b8e0f0c9ae3",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);


        mChatAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String queryString = String.valueOf(mMessageView.getText());

                new ChatActivity.DoTextRequestTask().execute(queryString);

                mMessageView.setText("");


            }
        });



    }


    private void loadMessages() {

        mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ;
                Messages message=dataSnapshot.getValue(Messages.class);
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();



                mMessegesList.post(new Runnable() {
                    @Override
                    public void run() {
                        // Call smooth scroll
                        mMessegesList.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                });


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void sendMessage() {

        String message=mMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)){

            String current_user_ref="messages/"+mCurrentUserId+"/"+mChatUser;
            String chat_user_ref="messages/"+mChatUser+"/"+mCurrentUserId;


            DatabaseReference user_message_push=mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id=user_message_push.getKey();


            Map messageMap= new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUserId);

            Map messageUserMap= new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            mMessageView.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError!=null){
                        Log.d("Chat_Log",databaseError.getMessage().toString());
                    }


                }
            });

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(mMessageView.getWindowToken(), 0);


        }
    }

    private void sendMessagetoAi(String result) {

        String message=result;

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mMessageView.getWindowToken(), 0);

        if (!TextUtils.isEmpty(message)){

            String current_user_ref="messages/"+mCurrentUserId+"/"+mChatUser;
            String chat_user_ref="messages/"+mChatUser+"/"+mCurrentUserId;


            DatabaseReference user_message_push=mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id=user_message_push.getKey();


            Map messageMap= new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUserId);

            Map messageUserMap= new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            mMessageView.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError!=null){
                        Log.d("Chat_Log",databaseError.getMessage().toString());
                    }


                }
            });




        }
    }




    private void getMessageFromAi(String speech) {


        String message=speech;
        String googleId="ahE6YBgKKASILpqpk76CXiE0wxT2";

        if (!TextUtils.isEmpty(message)){

            String current_user_ref="messages/"+mCurrentUserId+"/"+mChatUser;
            String chat_user_ref="messages/"+mChatUser+"/"+mCurrentUserId;


            DatabaseReference user_message_push=mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id=user_message_push.getKey();


            Map messageMap= new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",googleId);

            Map messageUserMap= new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            mMessageView.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError!=null){
                        Log.d("Chat_Log",databaseError.getMessage().toString());
                    }


                }
            });


        }
    }


    private void initService(final LanguageConfig selectedLanguage) {
        final AIConfiguration.SupportedLanguages lang = AIConfiguration.SupportedLanguages.fromLanguageTag(selectedLanguage.getLanguageCode());
        final AIConfiguration config = new AIConfiguration(selectedLanguage.getAccessToken(),
                lang,
                AIConfiguration.RecognitionEngine.System);


        aiDataService = new AIDataService(this, config);
    }




    public void onResult(AIResponse response) {
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }
        final String speech = result.getFulfillment().getSpeech();
        Log.i("djfs", "Speech: " + speech);




        // Show results in TextView.
//        mResultView.setText("Query:" + result.getResolvedQuery() +
//                "\nAction: " + result.getAction() +
//                "\nParameters: " + parameterString+"\nResponse from server: "+speech);

//        mResultView.setText(speech);
//        mQuerryView.setText(result.getResolvedQuery());
//
//        Toast.makeText(ChatActivity.this,speech,Toast.LENGTH_SHORT).show();
//        Toast.makeText(ChatActivity.this,result.getResolvedQuery(),Toast.LENGTH_SHORT).show();

            sendMessagetoAi(result.getResolvedQuery());
            getMessageFromAi(speech);



    }

    class DoTextRequestTask extends AsyncTask<String, Void, AIResponse> {
        private Exception exception = null;
        protected AIResponse doInBackground(String... text) {
            AIResponse resp = null;
            try {
                resp = aiService.textRequest(text[0], new RequestExtras());
                // might depend on you implementation ; find out how to
                // retrieve the AIService instance and replace "aiDialog.getAIService()"
            } catch (Exception e) {
                this.exception = e;
            }
            return resp;
        }
        @Override
        protected void onPostExecute(final AIResponse response) {
            if (response != null) {
                onResult(response);
            } else {
//                mResultView.setText("Errorrr");
                Toast.makeText(ChatActivity.this,"I can't here you !",Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
