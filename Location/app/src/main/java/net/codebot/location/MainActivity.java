package net.codebot.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.toolbox.StringRequest;

import java.util.Arrays;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends Activity {
    // used for permission checks
    private static final int REQUEST_CODE_PERMISSION = 2;
    //String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    String[] mPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    // tag for debugging
    public static final String TAG = "GPSTRACKER";

    // data to fetch from the location service

    @SuppressLint("HardwareIds")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check permissions
        try {
            boolean perms = true;
            for(String s: mPermissions){
                perms = perms && (ActivityCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_GRANTED);
            }
            if(!perms){
                ActivityCompat.requestPermissions(this, mPermissions, REQUEST_CODE_PERMISSION);
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
                text.append(
                        LocationWorker.updated + "\n"
                );});
        final Button enableButton = (Button) findViewById(R.id.app_settings);
        enableButton.setOnClickListener(event -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}