package net.codebot.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class LocationWorker extends Worker {
    public static Date updated;
    public static Location location;

    // debugging
    public static final String TAG = "LOCATION_WORKER";

    // used for network requests
    String url = "https://www.student.cs.uwaterloo.ca/~j2avery/test.php?";

    public LocationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Result doWork() {
        // check if GPS enabled
        GPSTracker gps = GPSTracker.Builder();
        if(gps.canGetLocation()) {

            // get device data
            String imei = "", meid = "";
            String deviceId = "A" + Settings.Secure.getString(gps.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            TelephonyManager telephonyManager = (TelephonyManager) gps.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                try {
                    imei = telephonyManager.getImei();
                    meid = telephonyManager.getMeid();
                } catch (Exception ignored) {
                }
            }

            // fetch data
            updated = new Date();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String formatted = dateformat.format(updated);
            location = gps.getLocation();

            if (location != null) {
                Log.d(TAG, "date:" + formatted + ", lat:" + location.getLatitude() + ", long:" + location.getLongitude());
            } else {
                Log.d(TAG, "Location returned null");
            }

            // web GET request
            url += ("capturedate=" + formatted);
            url += ("&id=" + deviceId);
            url += ("&imei=" + imei);
            url += ("&mei=" + meid);
            url += ("&lat="+location.getLatitude());
            url += ("&long="+location.getLongitude());
            url += ("&alt="+location.getAltitude());

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.d(TAG, "Response is: "+ response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

            // TODO
            // queue if that fails

        } else {
            // can't get location, so ask user to enable GPS/network in settings
            Log.d(TAG, "GPS cannot get location");
            gps.showSettingsAlert();
        }

        // setup next request
        // actual time when it runs will be applied as a delay
        int delay = calculateTimeToNextRequest();
        OneTimeWorkRequest locationRequest = new OneTimeWorkRequest.Builder(LocationWorker.class)
                .setInitialDelay(Duration.of(delay, ChronoUnit.MILLIS))
                .build();

        // queue the request
        WorkManager.getInstance(getApplicationContext())
                .enqueue(locationRequest);

        // return key:value pairs
        return Result.success();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int calculateTimeToNextRequest() {
//        Calendar currentDate = Calendar.getInstance();
//        Calendar dueDate = Calendar.getInstance();
//        dueDate.set(Calendar.HOUR_OF_DAY, 13);
//        dueDate.set(Calendar.MINUTE, 54);
//        dueDate.set(Calendar.SECOND, 0);
//        if (dueDate.before(currentDate)) {
//            dueDate.add(Calendar.HOUR_OF_DAY, 24);
//        }
//        return (int) (dueDate.getTimeInMillis() - currentDate.getTimeInMillis());

        // return delay in ms
        return 10 * 1000; // testing
    }

}
