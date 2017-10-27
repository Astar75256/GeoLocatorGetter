package ru.astar.geolocatorgetter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonStartService;

    Intent intentService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStartService = (Button) findViewById(R.id.buttonStartService);
        buttonStartService.setOnClickListener(this);
        intentService = new Intent(this, GeoGetService.class);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonStartService) {
            if (!Util.isMyServiceRunning(getApplicationContext(), GeoGetService.class)) {
                startService(intentService);
            } else {
                stopService(intentService);
            }
        }
    }

}