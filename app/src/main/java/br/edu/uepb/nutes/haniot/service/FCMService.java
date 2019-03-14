package br.edu.uepb.nutes.haniot.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.utils.Log;

/**
 * Class to handle and display messages from Firebase Cloud Message.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class FCMService extends FirebaseMessagingService {
    private final String TAG = "FCMService";

    private final String MESSAGE_TYPE_FALL = "fall";
    private final String MESSAGE_TYPE_ALERT = "alert";
    private final String CHANNEL_ID = "fcm_default_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /**
         * Verify that a message contains a payload of data.
         * The application waits for date type messages.
         * Messages of notification type are ignored.
         */
        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage.getData());
        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification: " +
                    remoteMessage.getNotification().getTitle()
                    + " | " + remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data {@link Map<String, String>}
     */
    private void sendNotification(Map<String, String> data) {
        int id = (int) (System.currentTimeMillis() / 1000);

        if (data.containsKey("type")) {
            switch (data.get("type")) {
                case MESSAGE_TYPE_FALL:
                    fallNotification(data, id);
                    break;
                case MESSAGE_TYPE_ALERT:
                    alertNotification(data, id);
                    break;
                default:
                    defaultNotification(data, id);
                    break;
            }
        } else {
            defaultNotification(data, id);
        }
    }

    /**
     * Create notification type default/general
     *
     * @param data {@link Map<String, String>}
     * @param id   int
     */
    private void defaultNotification(Map<String, String> data, int id) {
        Intent intent;
        if (data.containsKey("click_action") && !data.get("click_action").isEmpty())
            intent = new Intent(data.get("click_action"));
        else intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentTitle(data.get("title"))
                        .setContentText(data.get("message"))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(data.get("message")))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[]{1000, 1000})
                        .setContentIntent(pendingIntent);

        sendNotification(notificationBuilder, id);
    }

    /**
     * Create notification type fall.
     *
     * @param data {@link Map<String, String>}
     * @param id   int
     */
    private void fallNotification(Map<String, String> data, int id) {
//        Intent intent = new Intent(this, ElderlyMonitoredActivity.class);
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
//        taskStackBuilder.addParentStack(ElderlyMonitoredActivity.class);
//        taskStackBuilder.addNextIntent(intent);

//        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * Action emergency
         */
//        Intent emergencyIntent = new Intent(Intent.ACTION_DIAL,
//                Uri.parse("tel:".concat(getResources().getString(R.string.number_emergency))));
//        PendingIntent emergencyAction = PendingIntent.getActivity(this, 0, emergencyIntent, 0);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.app_logo)
//                        .setContentTitle(data.get("title"))
//                        .setContentText(data.get("message"))
//                        .setSubText(String.format(getResources().getString(
//                                R.string.fall_register_time_title),
//                                DateUtils.formatDate(Long.parseLong(data.get("timestamp")),
//                                        getResources().getString(R.string.time_format_simple2))))
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setVibrate(new long[]{1000, 1000})
//                        .setContentIntent(pendingIntent)
//                        .setStyle(new NotificationCompat.BigTextStyle().bigText(data.get("message")))
//                        .addAction(R.drawable.ic_action_live_help,
//                                getResources().getString(R.string.call_emergency_title), emergencyAction);
//
//        /**
//         * Action contact
//         */
//        if (data.containsKey("phone"))
//            notificationBuilder.addAction(actionPhone(data.get("phone"), R.drawable.ic_person));
//
//        sendNotification(notificationBuilder, id);
    }


    /**
     * Create notification type fall.
     *
     * @param data {@link Map<String, String>}
     * @param id   int
     */
    private void alertNotification(Map<String, String> data, int id) {
//        Intent intent = new Intent(this, ElderlyMonitoredActivity.class);
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
//        taskStackBuilder.addParentStack(ElderlyMonitoredActivity.class);
//        taskStackBuilder.addNextIntent(intent);
//
//        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.app_logo)
//                        .setContentTitle(data.get("title"))
//                        .setContentText(data.get("message"))
//                        .setSubText(String.format(getResources().getString(
//                                R.string.alert_register_time_title),
//                                DateUtils.formatDate(Long.parseLong(data.get("timestamp")),
//                                        getResources().getString(R.string.time_format_simple2))))
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setVibrate(new long[]{1000, 1000})
//                        .setContentIntent(pendingIntent)
//                        .setStyle(new NotificationCompat.BigTextStyle().bigText(data.get("message")));
//
//        /**
//         * Action contact
//         */
//        if (data.containsKey("phone"))
//            notificationBuilder.addAction(actionPhone(data.get("phone"), R.drawable.ic_person));
//
//        sendNotification(notificationBuilder, id);
    }

    /**
     * Create action call phone.
     *
     * @param number {@link String}
     * @param icon   int
     * @return {@link NotificationCompat.Action}
     */
    private NotificationCompat.Action actionPhone(String number, int icon) {
        Intent intentContact = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:".concat(number)));
        PendingIntent contactAction = PendingIntent
                .getActivity(this, 0, intentContact, 0);

        return new NotificationCompat.Action(icon, getResources().getString(
                R.string.call_elderly_title), contactAction);
    }

    /**
     * Send notification.
     *
     * @param notificationBuilder
     * @param id
     */
    private void sendNotification(NotificationCompat.Builder notificationBuilder, int id) {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id, notificationBuilder.build());
    }
}
