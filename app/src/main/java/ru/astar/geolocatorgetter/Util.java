package ru.astar.geolocatorgetter;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Astar on 27.10.2017.
 */

public class Util {

    public static final String PREF_APP = "GeoGetter";
    public static final String PREF_FIRST_RUN = "first_run";
    public static final String PREF_USER_ID = "user_id";

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }

        return false;
    }
}
