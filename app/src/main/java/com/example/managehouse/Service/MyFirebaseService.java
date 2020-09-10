package com.example.managehouse.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (Common.currentUser != null) sendNotification(remoteMessage.getData());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        saveDeviceToken(s);
    }

    public void saveDeviceToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("device", token);
        editor.putBoolean("notification", true);
        editor.commit();
    }

    public void sendNotification(Map data) {
        try {
            Intent intentCreateBill = new Intent(this, HomeActivity.class);
            intentCreateBill.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent intentDelayInput = new Intent(this, HomeActivity.class);
            intentDelayInput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentDelayInput.putExtra("type", 1);

            /* value type
            * 0 - thông báo tạo hóa đơn thu tiền phòng trọ
            * 1 - thống báo thu tiền phòng trọ khi nhập vào số ngày báo lại sẽ không replace fragment
            * */
            String title = (String) data.get("title");
            String body = (String) data.get("body");
            int type = Integer.parseInt((String) data.get("type"));
            intentCreateBill.putExtra("type", type);
            switch (type) {
                case 0 : {
                    if(data.get("khutro_id") != null && data.get("phongtro_id") != null) {
                        intentCreateBill.putExtra("khutro_id", Integer.parseInt((String) data.get("khutro_id")));
                        intentCreateBill.putExtra("phongtro_id", Integer.parseInt((String) data.get("phongtro_id")));
                        intentDelayInput.putExtra("phongtro_id", Integer.parseInt((String) data.get("phongtro_id")));
                    }
                    break;
                }
            }
            int notificationId = new Random().nextInt();
            int requestCode = (int) System.currentTimeMillis();

            PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intentCreateBill, PendingIntent.FLAG_UPDATE_CURRENT);
            String channelId = "notification-"+System.currentTimeMillis();
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            RemoteInput remoteInput = new RemoteInput.Builder("12121997")
                    .setLabel("Nhập số ngày báo lại")
                    .build();
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_stay_home, "Hẹn ngày báo lại",PendingIntent.getActivity(this, 0, intentDelayInput, PendingIntent.FLAG_UPDATE_CURRENT))
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(true)
                    .build();
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .addAction(action)
                            .setOngoing(false)
                            .setShowWhen(true)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_stay_home)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setSound(defaultSoundUri,attributes);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(notificationId, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
