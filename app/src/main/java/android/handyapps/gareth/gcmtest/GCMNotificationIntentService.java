package android.handyapps.gareth.gcmtest;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Gareth on 2015-01-15.
 */

/**
 * IntentService is a base class for Services that handle asynchronous requests (expressed as Intents) on demand
 */
public class GCMNotificationIntentService extends IntentService {

    public static final int notifyID = 9001;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    /**
     * This method is invoked on the worker thread with a request to process
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    Log.v("Send error: ", extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    Log.v("Deleted messages on server:", extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    Log.v("Message Received from Google GCM Server:", extras.getString("message"));
                    notification(extras.getString("message"));
                    break;
            }
        }
        /**
         *  completeWakefulIntent finishes the execution from a previous startWakefulService(Context, Intent).
         */
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void notification(String msg){

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("msg", msg);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager notificationManager;

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("New Notification")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_launcher);
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);

        // Enable auto cancel
        mNotifyBuilder.setAutoCancel(true);

        // Post a notification
        notificationManager.notify(notifyID, mNotifyBuilder.build());
    }
}
