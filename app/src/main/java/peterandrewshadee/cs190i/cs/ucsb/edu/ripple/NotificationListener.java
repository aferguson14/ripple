package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.provider.MediaStore;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;


/**
 * Created by Ferg on 6/8/17.
 */

public class NotificationListener extends NotificationListenerService {

    public static boolean isNotificationAccessEnabled = false;
    public MediaController.Callback mSessionCallback;

    public NotificationListener() {
    }

    public static final String SPOTIFY_PACK_NAME = "com.spotify.music";


    @Override
    public IBinder onBind(Intent mIntent) {
        IBinder mIBinder = super.onBind(mIntent);
        isNotificationAccessEnabled = true;
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent mIntent) {
        boolean mOnUnbind = super.onUnbind(mIntent);
        isNotificationAccessEnabled = false;
        return mOnUnbind;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        String packageName = sbn.getPackageName();
        Log.d("NotificationListner", sbn.getPackageName());

        if(packageName.equals(SPOTIFY_PACK_NAME)){
            Log.d("NotificationListner", "SPOTIFY NOTIFICATION FOUND");
//            Log.d("NotificationListner", "spotBarNotif" + sbn.toString());
//            Log.d("NotificationListner", "spotNotif tag: " + sbn.getTag());
//            Log.d("NotificationListner", "spotNotif tostring: " + sbn.getNotification().toString());
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
//        int notificationCode = matchNotificationCode(sbn);
//
//        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
//
//            StatusBarNotification[] activeNotifications = this.getActiveNotifications();
//
//            if(activeNotifications != null && activeNotifications.length > 0) {
//                for (int i = 0; i < activeNotifications.length; i++) {
//                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
//                        Intent intent = new  Intent("com.github.chagall.notificationlistenerexample");
//                        intent.putExtra("Notification Code", notificationCode);
//                        sendBroadcast(intent);
//                        break;
//                    }
//                }
//            }
//        }
    }


}