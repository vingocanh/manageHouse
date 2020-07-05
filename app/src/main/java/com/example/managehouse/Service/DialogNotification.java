package com.example.managehouse.Service;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.managehouse.R;

import java.util.Arrays;

public class DialogNotification {
    private Activity activity;
    private String[] body;
    private String type;

    public DialogNotification(Activity activity, String[] body, String type) {
        this.activity = activity;
        this.body = body;
        this.type = type;
    }

    public void showDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_notify, null);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        ListView lvMessage = view.findViewById(R.id.lvMessage);
        ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, Arrays.asList(body));
        lvMessage.setAdapter(adapter);
        LinearLayout llTitle = view.findViewById(R.id.llTitle);
        if(type.equals("error")) {
            llTitle.setBackgroundResource(R.color.colorError);
            txtTitle.setText("Lỗi");
        }
        if(type.equals("warning")) {
            llTitle.setBackgroundResource(R.color.colorWarning);
            txtTitle.setText("Cảnh báo");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
