package ru.astar.geolocatorgetter;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Astar on 17.10.2017.
 */

public class GeoGetService extends Service {

    public static final String TAG = "Main Activity";

    public static final String SERVER_NAME = "http://rrogea75.siteme.org/geo_service.php?";
    public static final int DEFAULT_NOTIFICATION_ID = 101;
    public static final int RESTART_SERVICE_SECONDS = 3000;

    private NotificationManager notificationManager;
    private MyLocation myLocation;
    private SharedPreferences preferences;

    private int userId;
    private int delaySendQuery;  // время задержки для отправки запроса

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = getSharedPreferences(Util.PREF_APP, MODE_PRIVATE);
        if (preferences != null) {
            if (!preferences.contains(Util.PREF_USER_ID)) stopSelf();
            userId = preferences.getInt(Util.PREF_USER_ID, 0);
            if (userId <= 0) stopSelf();
        }

        delaySendQuery = 10000;

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        myLocation = new MyLocation(getApplicationContext());
        myLocation.removeListener();
        myLocation.reloadListener();

        sendNotification("", "", "");

        if (myLocation != null) {
            myLocation.removeListener();
            myLocation.reloadListener();
        }

        doTask();

        return START_REDELIVER_INTENT;
    }

    public void sendNotification(String ticker, String title, String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis());

        Notification notification;

        if (Build.VERSION.SDK_INT <= 15) {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        restartApp();
    }

    private void restartApp() {
        Intent intentService = new Intent(this, getClass());
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 1, intentService, PendingIntent.FLAG_ONE_SHOT);
        am.setExact(AlarmManager.RTC, System.currentTimeMillis() + RESTART_SERVICE_SECONDS, pi);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
        stopSelf();
    }

    private void doTask() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    LatLng latLng = myLocation.getLocation();
                    int gpsEnabled = (myLocation.isGpsEnabled()) ? 1 : 0;
                    String urlPost = SERVER_NAME + "action=put&user_id=" + userId + "&"
                            + "latitude=" + latLng.getLatitude() + "&"
                            + "longitude=" + latLng.getLongitude() + "&"
                            + "gps_enabled=" + gpsEnabled;

                    Log.d(TAG, urlPost);

                    URL url = new URL(urlPost);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "" + connection.getResponseCode());
                    }
                } catch (Exception e) {
                    timer.cancel();
                    restartApp();
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }, 0, delaySendQuery);
    }

}
