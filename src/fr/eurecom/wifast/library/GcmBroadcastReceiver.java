package fr.eurecom.wifast.library;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import fr.eurecom.wifast.MainActivity;
import fr.eurecom.wifast.R;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	static final String TAG = "GCMDemo";
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.i(TAG, "notification received");
    	
    	Bundle extras = intent.getExtras();
    	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        Log.i(TAG, "messageType: "+messageType);
        Log.i(TAG, "extras: "+extras.toString());

        if (!extras.isEmpty()) {
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                // Post notification of received message.
                Log.i(TAG, "Received: " + extras.toString());
                mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
                
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.hamburger_icon)
                .setContentTitle(extras.getString("title"))
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(extras.getString("title")))
                .setContentText(extras.getString("text"));

                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                
                long[] pattern = {0, 500, 500, 500, 500, 500, 500, 500};
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(pattern, -1); // Only once
            }
        }
    	
        setResultCode(Activity.RESULT_OK);
    }
}