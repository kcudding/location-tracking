package net.codebot.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Date;

public class MainActivity extends Activity {
    // used for permission checks
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // tag for debugging
    public static final String TAG = "GPSTRACKER";

    // data to fetch from the location service
    String deviceId = "unavailable", imei = "unavailable", meid = "unavailable";

    @SuppressLint("HardwareIds")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check permissions
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get data from tracking service
        GPSTracker.Builder().setContext(this.getApplicationContext());

        // make scrollable
        final TextView text = (TextView) findViewById(R.id.editText);
        text.setMovementMethod(new ScrollingMovementMethod());

        // start tracking handler
        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View arg0) {
                // create a work request to fetch location data
                // first request runs immediately and schedules the next one
                OneTimeWorkRequest locationRequest = new OneTimeWorkRequest.Builder(LocationWorker.class).build();
                WorkManager.getInstance(getApplicationContext()).enqueue(locationRequest);
                text.setText("Start location tracking\n\n");
            }
        });

        // start tracking handler
        final Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(event -> {
                text.append(LocationWorker.updated + "\n"
                + "  deviceID: " + deviceId + "\n"
                + "  imei: " + imei + "\n"
                + "  meid: " + meid + "\n"
                + "  provider: " + LocationWorker.location.getProvider() + "\n"
                + "  fetched: " + new Date(LocationWorker.location.getTime()) + "\n"
                + "  latitude: " + LocationWorker.location.getLatitude() + "\n"
                + "  longitude: " + LocationWorker.location.getLongitude() + "\n"
                + "  altitude: " + LocationWorker.location.getAltitude() + "\n"
                + "  accuracy: " + LocationWorker.location.getAccuracy() + "\n\n");
            });
    }
}