package cl.marcer.die_app;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Alberto Galleguillos on 4/3/17.
 */

public class FCMReceiver extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";
    private int badgeCount;

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        //Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Set Icon Badge + 1
        final SharedPreferences mSharedPreferences= getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        badgeCount = mSharedPreferences.getInt(getString(R.string.unread), 0);
        badgeCount += 1;
        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt(getString(R.string.unread), badgeCount).apply();

        String message = remoteMessage.getData().get("message");
        sendNotification(message);

        super.onMessageReceived(remoteMessage);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_die)
                .setContentTitle("Die App")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}

