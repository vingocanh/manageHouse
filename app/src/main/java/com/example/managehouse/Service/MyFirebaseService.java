package com.example.managehouse.Service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Activity.MainActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.CreateBillFragment;
import com.example.managehouse.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("cuong", "ok");
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
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            /* value type
            * 0 - thông báo tạo hóa đơn thu tiền phòng trọ
            * */
            String title = (String) data.get("title");
            String body = (String) data.get("body");
            int type = Integer.parseInt((String) data.get("type"));
            intent.putExtra("type", type);
            switch (type) {
                case 0 : {
                    intent.putExtra("khutro_id", Integer.parseInt((String) data.get("khutro_id")));
                    intent.putExtra("phongtro_id", Integer.parseInt((String) data.get("phongtro_id")));
                    break;
                }
            }
            int notificationId = new Random().nextInt();
            int requestCode = (int) System.currentTimeMillis();



            PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String channelId = "notification-"+System.currentTimeMillis();
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
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
