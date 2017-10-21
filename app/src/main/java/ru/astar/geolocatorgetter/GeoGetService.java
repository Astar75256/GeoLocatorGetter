package ru.astar.geolocatorgetter;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by molot on 17.10.2017.
 */

public class GeoGetService extends Service {

    private NotificationManager notificationManager;
    private MyLocation myLocation;
    public static final int DEFAULT_NOTIFICATION_ID =  101;
    public static final int RESTART_SERVICE_SECONDS = 3000;


    @Override
    public void onCreate() {
        super.onCreate();
        myLocation = new MyLocation(getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification("Ticker", "Title", "Text");
        Log.d("GeoGetService", "onStartCommand");
        doTask();

        return START_REDELIVER_INTENT;
    }

    public void sendNotification(String ticker, String title, String text) {
        Log.d("GeoGetService", "sendNotification");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);;

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
        Log.d("GeoGetService", "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
        Intent intentService = new Intent(this, getClass());
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 1, intentService, PendingIntent.FLAG_ONE_SHOT);
        am.setExact(AlarmManager.RTC, System.currentTimeMillis() + RESTART_SERVICE_SECONDS, pi);
    }

    @Override
    public void onDestroy() {
        Log.d("GeoGetService", "onDestroy");
        super.onDestroy();
        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
        stopSelf();
    }

    private void doTask() {
        Log.d("GeoGetService", "doTask");

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // код отправки данных на сервер...

                // ...

                Toast.makeText(getApplicationContext(), myLocation.getLocationData(), Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, 3000);
            }
        });
    }

}
