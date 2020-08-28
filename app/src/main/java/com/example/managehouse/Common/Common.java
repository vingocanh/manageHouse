package com.example.managehouse.Common;

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

}
