package com.example.managehouse.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.LoginFragment;
import com.example.managehouse.R;
import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.Slide;

import java.util.ArrayList;
import java.util.List;

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
            initSlide();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.flMain, new LoginFragment());
            fragmentTransaction.commit();
        }

    }

    public void mapping() {
        lavLogin = findViewById(R.id.lavLogin);
    }



    public void initSlide() {
        sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("on", false) == false) {
            new IntroductionBuilder(this).withSlides(generateSlides()).introduceMyself();
        }
    }


    public void replaceFragment(Fragment fragment, boolean backStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.flMain, fragment);
        if(backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private List<Slide> generateSlides() {
        List<Slide> result = new ArrayList<>();

        result.add(new Slide()
                .withTitle("Quản lý thông tin")
                .withDescription("Quản lý thông tin khu trọ, phòng trọ, người trọ...")
                .withColorResource(R.color.colorBlue)
                .withImage(R.drawable.ic_hotel_white)
        );

        result.add(new Slide()
                .withTitle("Hóa đơn thanh toán")
                .withDescription("Tạo hóa đơn thanh toán tiền phòng, chia sẻ hóa đơn qua tin nhắn facebook, zalo...")
                .withColorResource(R.color.colorGreen)
                .withImage(R.drawable.ic_bill)
        );
        result.add(new Slide()
                .withTitle("Thông báo thu tiền")
                .withDescription("Hiển thị thông báo khi đến ngày thu tiền phòng trọ.")
                .withColorResource(R.color.colorViolet)
                .withImage(R.drawable.ic_notification_white)
        );
        result.add(new Slide()
                .withTitle("Thống kê doanh thu")
                .withDescription("Thống kê số tiền thu được của khu trọ, phòng trọ theo mốc thời gian.")
                .withColorResource(R.color.colorRed)
                .withImage(R.drawable.ic_return_on_investment_white)
        );

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IntroductionBuilder.INTRODUCTION_REQUEST_CODE && resultCode == RESULT_OK) {
            sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("on", true);
            editor.commit();
        }
    }
}
