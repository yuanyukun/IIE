package com.horem.parachute.pushmessage;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.horem.parachute.R;


/**
 * Created by user on 2015/8/13.
 */
public class NotificationUtil {
	public static void sendNotification(Context context, Intent intent, String edtTitle, String edtContent, int id) {
        Log.d("YYQ", "NotificationUtil sendNotification");
        try {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification();
            notification.icon = R.mipmap.circle_light;
            notification.tickerText = edtTitle;
            notification.when = System.currentTimeMillis();

            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent pendingIntent=PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            notification.setLatesteEventInfo(context,edtTitle,edtContent,pendingIntent);
//            notification.setLatestEventInfo(context, edtTitle, edtContent, pendingIntent);
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            long[] vibrate = { 0, 100, 200, 300 };
            notification.vibrate = vibrate;

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_LIGHTS;

            notification.ledARGB = 0xff00ff00;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;

            notification.flags = Notification.FLAG_AUTO_CANCEL;

            manager.notify(id, notification);
        } catch (Exception e) {
            Log.e("YYQ", "Exception: " + e.getClass().getName());
        }
    }
	public static void sendNotificationBuilder(Context context, Intent intent, String edtTitle, String edtContent, int id) {
        Log.d("YYQ", "NotificationUtil sendNotification");
        try {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.circle_light);
            builder.setContentTitle(edtTitle);
            builder.setContentText(edtContent);

            long[] vibrate = { 0, 100, 200, 300 };
            builder.setVibrate(vibrate);
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            builder.setLights(0xff00ff00,300,1000);
            builder.setShowWhen(true);
            builder.setAutoCancel(true);
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            // Adds the back stack for the Intent (but not the Intent itself)
//            stackBuilder.addParentStack(MyTouChuanMessage_Receiver.class);
//            // Adds the Intent that starts the Activity to the top of the stack
//            stackBuilder.addNextIntent(intent);
//            PendingIntent resultPendingIntent =
//                    stackBuilder.getPendingIntent(
//                            0,
//                            PendingIntent.FLAG_UPDATE_CURRENT
//                    );
            builder.setContentIntent(pendingIntent);

            manager.notify(id, builder.build());

        } catch (Exception e) {
            Log.e("YYQ", "Exception: " + e.getClass().getName());
        }
    }
}
