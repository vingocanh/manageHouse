package com.example.managehouse.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.example.managehouse.Model.Hoadon;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.User;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Retrofit.RetrofitClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Common {

    public static User currentUser = null;
    public static String token = "";
    public static boolean checkFormChange = false;
    public static List<Hoadon> hoadonList = new ArrayList<>();
    public static Fragment currentFragment = null;
    public static int posMenu = 0;

    public static API getAPI(){
        return RetrofitClient.getInstance().create(API.class);
    }

    public static String formatNumber(int value, boolean money) {
        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        return (money) ? numberFormat.format(value) + " VNĐ" : numberFormat.format(value);
    }

    public static int clearMoney(String value) {
        int price = 0;
        if(value.indexOf("VNĐ") == -1) return price;
        String cMoney = value.substring(0, value.indexOf("VNĐ") - 1);
        while (cMoney.indexOf(".") > -1) {
            cMoney = cMoney.replace(".", "");
        }
        price = Integer.parseInt(cMoney);
        return price;
    }

    public static String getDeviceToken(Context context) {
        String token = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("token", context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            token = sharedPreferences.getString("device", null);
        }
        return token;
    }

}
