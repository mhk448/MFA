package ir.mhk448.android.myfriendavatar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		int status = NetworkUtil.getConnectivityStatusString(context);
		Log.e("Sulod sa network reciever", "Sulod sa network reciever");
		if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			CharSequence message = "";
			if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
				// disc
				message = "disc";
			} else {
				message = "con";
				// con
			}
			CharSequence title = "Hello";
			NotificationManager notificationManager;
			notificationManager = (NotificationManager) context
					.getSystemService("notification");
			Notification notification;
			notification = new Notification(R.drawable.ic_launcher,
					"Notifiy.. ", System.currentTimeMillis());
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					new Intent(context,Main.class), 0);
			notification.setLatestEventInfo(context, title, message,
					pendingIntent); 
			notification.sound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			notificationManager.notify(1010, notification);

		}
	}
}

class NetworkUtil {
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	public static final int NETWORK_STATUS_NOT_CONNECTED = 0,
			NETWORK_STAUS_WIFI = 1, NETWORK_STATUS_MOBILE = 2;

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}

	public static int getConnectivityStatusString(Context context) {
		int conn = NetworkUtil.getConnectivityStatus(context);
		int status = 0;
		if (conn == NetworkUtil.TYPE_WIFI) {
			status = NETWORK_STAUS_WIFI;
		} else if (conn == NetworkUtil.TYPE_MOBILE) {
			status = NETWORK_STATUS_MOBILE;
		} else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
			status = NETWORK_STATUS_NOT_CONNECTED;
		}
		return status;
	}
}