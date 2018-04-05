package com.example.divindivakaran.myaichat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {

    public DatabaseReference mUserDataBase;
    public FirebaseUser mCurrentuser;

    private CircleImageView mImageView;
    private TextView mName;
    private TextView mStatus;
    private Button mStatusbtn;
    private Button mImagebtn;

    public static final int GALLERY_PICK=1;

    public StorageReference mImageStorage;
    private ProgressDialog mProgress;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mImageView=(CircleImageView)findViewById(R.id.settings_image);
        mName=(TextView)findViewById(R.id.settings_display_name);
        mStatus=(TextView)findViewById(R.id.settings_status);

        mStatusbtn=(Button)findViewById(R.id.settings_status_btn);
        mImagebtn=(Button)findViewById(R.id.settings_image_btn);

        mImageStorage= FirebaseStorage.getInstance().getReference();


        mCurrentuser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentuser.getUid();



        mUserDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDataBase.keepSynced(true);


        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                Toast.makeText(SettingsActivity.this,dataSnapshot.toString(),Toast.LENGTH_LONG).show();
                String name=dataSnapshot.child("name").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();




                        mName.setText(name);
                        mStatus.setText(status);
                if (!image.equals("default")){

                   // Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mImageView);

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {



                        }

                        @Override
                        public void onError() {

                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mImageView);

                        }
                    });
                }





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String status_value= mStatus.getText().toString();
                Intent status_intent= new Intent(SettingsActivity.this,StatusActivity.class);
                status_intent.putExtra("status_value",status_value);

                startActivity(status_intent);

            }
        });

        mImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {






                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);



                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                */
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode==GALLERY_PICK && resultCode==RESULT_OK){

                Uri imageUri=data.getData();

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(this);
                //Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_LONG).show();
            }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress=new ProgressDialog(SettingsActivity.this);
                mProgress.setTitle("Uploading Image");
                mProgress.setMessage("please wait while we upload your profile picture.");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();


                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                String current_user_id=mCurrentuser.getUid();

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filePath=mImageStorage.child("profile_images").child(current_user_id+".jpg");
                final StorageReference thumbfilepath=mImageStorage.child("profile_images").child("thumbs").child(current_user_id+".jpg");


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                            if (task.isSuccessful()){
//
//                               String download_Url= task.getResult().getDownloadUrl().toString();
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = task.getResult().getDownloadUrl();

                                final String download_Url=downloadUrl.toString();


                                UploadTask uploadTask = thumbfilepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        String thumb_DownloadUrl =thumb_task.getResult().getDownloadUrl().toString();


                                        if (thumb_task.isSuccessful())
                                        {
                                            Map update_hashmap = new HashMap<String, String>();
                                            update_hashmap.put("image",download_Url);
                                            update_hashmap.put("thumb_image",thumb_DownloadUrl);


                                            mUserDataBase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        mProgress.dismiss();
                                                        Toast.makeText(SettingsActivity.this,"Profile Picture Updated Successfully!",Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });
                                        }
                                        else
                                        {
                                            Toast.makeText(SettingsActivity.this,"Error in uploading Thumbnail!",Toast.LENGTH_LONG).show();
                                            mProgress.dismiss();

                                        }



                                    }
                                });




                            }else{
                                Toast.makeText(SettingsActivity.this,"Error Changing Profile Picture!",Toast.LENGTH_LONG).show();
                                mProgress.dismiss();
                            }

                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
}
