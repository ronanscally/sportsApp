package com.example.sportsapp;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Calendar;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";
    public static final String TITLE_MESSAGE = "title";
    public static final String DISTANCE_MESSAGE = "distance";
    public static final String FRIEND_MESSAGE = "friendAdd";
    public static final String EVENT_MESSAGE = "eventInvite";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            	sendDefaultNotification(Calendar.getInstance().getTimeInMillis(), "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            	sendDefaultNotification(Calendar.getInstance().getTimeInMillis(), "Deleted messages on server: " + extras.toString());
            // If it's a message about an event, do this
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType) && (extras.containsKey(DISTANCE_MESSAGE))) {
                // DELAY LOOP
            	// This loop represents the service doing some work.
//                for (int i = 0; i < 5; i++) {
//                    Log.i(TAG, "Working... " + (i + 1)
//                            + "/5 @ " + SystemClock.elapsedRealtime());
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                    }
//                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendEventNotification(Calendar.getInstance().getTimeInMillis(), extras.getString(TITLE_MESSAGE)+", "+extras.getString(DISTANCE_MESSAGE)+" away");
                Log.i(TAG, "Received: " + extras.toString());
            // If it's a message about a friend, do this
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType) && (extras.containsKey(FRIEND_MESSAGE))) {
                // DELAY LOOP
            	// This loop represents the service doing some work.
//                for (int i = 0; i < 5; i++) {
//                    Log.i(TAG, "Working... " + (i + 1)
//                            + "/5 @ " + SystemClock.elapsedRealtime());
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                    }
//                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendFriendRequestNotification(Calendar.getInstance().getTimeInMillis(), extras.getString(FRIEND_MESSAGE));
                Log.i(TAG, "Received: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType) && (extras.containsKey(EVENT_MESSAGE))) {
                // DELAY LOOP
            	// This loop represents the service doing some work.
//                for (int i = 0; i < 5; i++) {
//                    Log.i(TAG, "Working... " + (i + 1)
//                            + "/5 @ " + SystemClock.elapsedRealtime());
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                    }
//                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendEventInviteNotification(Calendar.getInstance().getTimeInMillis(), extras.getString(EVENT_MESSAGE));
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendEventNotification(long when, String notificationMsg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GCM_Registration.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("PlayerX")
        .setWhen(when)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(notificationMsg))
        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND)
        .setContentText(notificationMsg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify((int) when, mBuilder.build());
    }
    
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendDefaultNotification(long when, String notificationMsg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent showIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showIntent, 0);
        
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, GCM_Registration.class), 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("PlayerX")
        .setWhen(when)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(notificationMsg))
        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND)
        .setContentText(notificationMsg);


        mBuilder.setContentIntent(contentIntent);        
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        mBuilder.setVibrate(pattern);

        mNotificationManager.notify((int) when, mBuilder.build());
    }
    
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendFriendRequestNotification(long when, String notificationMsg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, GCM_Registration.class), 0);
        Intent showIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showIntent, 0);
        
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("PlayerX")
        .setWhen(when)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(notificationMsg))
        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND)
        .setContentText(notificationMsg);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setContentIntent(contentIntent);        
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        mBuilder.setVibrate(pattern);

        mNotificationManager.notify((int) when, mBuilder.build());
    }
    
    
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendEventInviteNotification(long when, String notificationMsg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GCM_Registration.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("PlayerX")
        .setWhen(when)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(notificationMsg))
        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND)
        .setContentText(notificationMsg);
        
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setContentIntent(contentIntent);        
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        mBuilder.setVibrate(pattern);
        mBuilder.setContentIntent(contentIntent);
        
        mNotificationManager.notify((int) when, mBuilder.build());
    }
}
