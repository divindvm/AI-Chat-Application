package com.example.divindivakaran.myaichat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar mToolBar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPageAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){

            mUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }


        mToolBar =(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("AI CHAT");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out


                }

            }
        };

        mViewPager = (ViewPager)findViewById(R.id.main_tabPager);
        mSectionsPageAdapter=new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPageAdapter);
        mTabLayout=(TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);




    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser= mAuth.getCurrentUser();
        if (currentUser==null){

           sendToStart();


        }else{

            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser= mAuth.getCurrentUser();
        if (currentUser!=null){

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
//            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }



    }

    private void sendToStart() {

        Intent startIntent= new Intent(MainActivity.this, Splash_screen.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.main_logout_btn)
        {

            mAuth.addAuthStateListener(mAuthListener);
            FirebaseUser currentUser= mAuth.getCurrentUser();
            if (currentUser!=null){

                mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
//            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            }

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if (item.getItemId()==R.id.main_settings_btn){

            Intent SettingsIntent= new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(SettingsIntent);
        }

        if(item.getItemId()==R.id.main_all_btn)
        {
            Intent SettingsIntent= new Intent(MainActivity.this,UsersActivity.class);
            startActivity(SettingsIntent);

        }

        return true;
    }
}
