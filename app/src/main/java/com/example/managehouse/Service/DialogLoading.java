package com.example.managehouse.Service;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.managehouse.R;


public class DialogLoading {

    private Activity activity;
    private String body;
    private AlertDialog alertDialog;

    public DialogLoading(Activity activity, String body) {
        this.activity = activity;
        this.body = body;
    }

    public void showDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);
        TextView txtBody = view.findViewById(R.id.txtBody);
        txtBody.setText(body);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void hideDialog() {
        alertDialog.dismiss();
    }

    public boolean isShow() {
        return alertDialog.isShowing();
    }
}
