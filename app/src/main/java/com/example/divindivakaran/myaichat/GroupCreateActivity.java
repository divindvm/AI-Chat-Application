package com.example.divindivakaran.myaichat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class GroupCreateActivity extends AppCompatActivity {


    private Toolbar mToolBar;

    private FirebaseAuth mAuth;

    private ProgressDialog mLoginProgress;

    private DatabaseReference mUserDatabase;

    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mToken;
    private TextInputLayout mProjectName;
    private TextInputLayout mProjectdescription;
    private Button mCreatebtn;
    private DatabaseReference mDataBase;
    private DatabaseReference mGetNameData;
    private String projectName;
    private String projectStatus;
    public String uid;
    public static String  mProname;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        mToolBar= (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Create New Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLoginProgress=new ProgressDialog(this);


        mEmail=(TextInputLayout)findViewById(R.id.reg_email);

        mProjectName=(TextInputLayout)findViewById(R.id.project_name);
        mProjectdescription=(TextInputLayout)findViewById(R.id.project_dscription);

        mPassword=(TextInputLayout)findViewById(R.id.reg_password);
        mToken=(TextInputLayout)findViewById(R.id.reg_display_name);
        mCreatebtn=(Button)findViewById(R.id.reg_create_btn);

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mRootRef= FirebaseDatabase.getInstance().getReference();
        mGetNameData=FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mCreatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email=mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();

//                    String email="abc@a.com";
//                    String password="qwerty";
                if(!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password))
                {

                    mLoginProgress.setTitle("Creating Group ...");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email,password);
                }


            }
        });





    }

    private void loginUser(final String email, final String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()){

                    mLoginProgress.dismiss();

                    final String current_user_id=mAuth.getCurrentUser().getUid();



                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            createGroup(email,password,current_user_id);
                        }
                    });




                }else
                {
                    mLoginProgress.hide();
                    Toast.makeText(GroupCreateActivity.this,"Error Finding Group Email or Password!",Toast.LENGTH_LONG).show();
                }

            }
        });



    }



    private void createGroup(String email, String password,String cuid) {


        String token=mToken.getEditText().getText().toString();

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
         uid=cuid;

        String name=mProjectName.getEditText().getText().toString();
        String description=mProjectdescription.getEditText().getText().toString();


        mDataBase= FirebaseDatabase.getInstance().getReference().child("Groups").child(uid);
        HashMap<String,String> userMap= new HashMap<String, String>();
        userMap.put("name",name);
        userMap.put("status",description);
        userMap.put("image","default");
        userMap.put("thumb_image","default");
        userMap.put("token",token);
        userMap.put("members",uid);
        mDataBase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {

                    String current_user_id=mAuth.getCurrentUser().getUid();


                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            Intent mainIntent =new Intent(GroupCreateActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();



                        }
                    });

                }
                else{

                    Toast.makeText(GroupCreateActivity.this, "User Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });











    }





}
