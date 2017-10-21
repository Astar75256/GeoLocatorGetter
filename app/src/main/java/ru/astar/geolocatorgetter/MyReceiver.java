package ru.astar.geolocatorgetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by molot on 20.10.2017.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, GeoGetService.class);
        context.startService(intentService);
    }
}
