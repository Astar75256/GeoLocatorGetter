package ru.astar.geolocatorgetter;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Astar on 17.10.2017.
 */

public class GeoGetService extends Service {

    public static final String TAG = "Main Activity";

    public static final String SERVER_NAME = "http://rrogea75.siteme.org/geo_service.php?";
    public static final int USER_ID = 2;
    public static final int DEFAULT_NOTIFICATION_ID = 101;
    public static final int RESTART_SERVICE_SECONDS = 3000;

    private NotificationManager notificationManager;
    private MyLocation myLocation;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        myLocation.removeListener();
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    LatLng latLng = myLocation.getLocation();
                    String urlPost = SERVER_NAME + "action=put&user_id=" + USER_ID + "&"
                            + "latitude=" + latLng.getLatitude() + "&"
                            + "longitude=" + latLng.getLongitude();

                    Log.d(TAG, urlPost);

                    URL url = new URL(urlPost);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "" + connection.getResponseCode());
                    }
                } catch (MalformedURLException e) {
                    restartApp();
                    e.printStackTrace();
                } catch (IOException e) {
                    restartApp();
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }, 0, 10000);
    }

}
