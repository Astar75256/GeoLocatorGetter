package ru.astar.geolocatorgetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Astar on 20.10.2017.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentService = new Intent(context, GeoGetService.class);

        if (!Util.isMyServiceRunning(context, GeoGetService.class)) {
            context.startService(intentService);
        }
    }
}
