package com.example.managehouse.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.LoginFragment;
import com.example.managehouse.R;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    public LottieAnimationView lavLogin;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapping();
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        if(sharedPreferences.getString("access_token",null) != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.flMain, new LoginFragment());
            fragmentTransaction.commit();
            Common.token = "login";
        }

    }

    public void mapping() {
        lavLogin = findViewById(R.id.lavLogin);
    }

    public void replaceFragment(Fragment fragment, boolean backStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.flMain, fragment);
        if(backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
