package com.example.aaron.islandharvest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final String USER_PREFERENCES = "userPreferences";
    private static final String START_TIME = "start_time";
    private static long startTime;
    private static boolean btnStartTimeLogIsEnabled = true;
    private static boolean btnCompleteTimeLogIsEnabled = true;
    private static boolean isChrmMeterRunning = false;
    private int routeID;
    private int userID;
    private int agencyID;
    private int donorID;
    private String donorAddress;
    private String agencyAddress;

    private TextView tvDonorAddr;
    private TextView tvAgencyAddr;
    private Chronometer chrmTrip;
    private Button btnStartTimeLog;
    private Button btnCompleteTimeLog;
    private View.OnClickListener completeTimeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chrmTrip.stop();
            isChrmMeterRunning = false;
            Toast.makeText(MainActivity.this, "Trip took " + chrmTrip.getText().toString(), Toast.LENGTH_SHORT).show();

            if (DonationEntryActivity.LAST_IMAGE == null
                    || AgencyInfoActivity.LAST_IMAGE == null
                    || DonorInfoActivity.LAST_IMAGE == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Signatures were not found.")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
            } else if (DonationEntryActivity.submissionInfo.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("You do not have any submissions")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
            } else {
                uploadSignatures();
                updateEndTime();
                completeRoute(routeID);
            }
        }
    };
    private View.OnClickListener startTimeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            startTime = SystemClock.elapsedRealtime();
            chrmTrip.setBase(startTime);
            editor.putLong(START_TIME, startTime);

            chrmTrip.start();
            isChrmMeterRunning = true;
            updateStartTime();

            btnStartTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnStartTimeLog.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            btnStartTimeLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Do you want to reset route?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetApp();
                                }
                            })
                            .create()
                            .show();
                }
            });
            btnStartTimeLogIsEnabled = false;
        }
    };
    private View.OnClickListener disableOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Do you want to restart the route?")
                    .setPositiveButton("No", null)
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reEnableApp();
                            restartRoute(routeID);
                        }
                    })
                    .create()
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvDonorAddr = (TextView) findViewById(R.id.donorTextView);
        tvAgencyAddr = (TextView) findViewById(R.id.agencyTextView);
        chrmTrip = (Chronometer) findViewById(R.id.tripChronometer);
        btnStartTimeLog = (Button) findViewById(R.id.startTimeLogButton);
        btnCompleteTimeLog = (Button) findViewById(R.id.completeTimeLogButton);

        chrmTrip.setBase(SystemClock.elapsedRealtime());

        // Todo need to create a separate .java file to hold all key values
        SharedPreferences sharedPref = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        userID = sharedPref.getInt("userID", -1);
        routeID = sharedPref.getInt("routeID", -1);
        donorID = sharedPref.getInt("donorID", -1);
        agencyID = sharedPref.getInt("agencyID", -1);
        donorAddress = "Donor Address:\n";
        agencyAddress = "Agency Address:\n";
        donorAddress = donorAddress + sharedPref.getString("donorAddress", null);
        agencyAddress = agencyAddress + sharedPref.getString("agencyAddress", null);

        tvDonorAddr.setText(donorAddress);
        tvAgencyAddr.setText(agencyAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isChrmMeterRunning) {
            chrmTrip.setBase(startTime);
            chrmTrip.start();
        }

        if (btnStartTimeLogIsEnabled) {
            btnStartTimeLog.setOnClickListener(startTimeOnClickListener);
            btnStartTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            btnStartTimeLog.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            btnStartTimeLog.setOnClickListener(disableOnClickListener);
            btnStartTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnStartTimeLog.setTextColor(getResources().getColor(R.color.white));
        }

        if (btnCompleteTimeLogIsEnabled) {
            btnCompleteTimeLog.setOnClickListener(completeTimeOnClickListener);
            btnCompleteTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            btnCompleteTimeLog.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            btnCompleteTimeLog.setOnClickListener(disableOnClickListener);
            btnCompleteTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnCompleteTimeLog.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_ro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent takeUserToAbout = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(takeUserToAbout);

        } else if (id == R.id.action_logout) {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

            Intent takeUserToLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(takeUserToLogin);
            resetApp();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_food_entry) {
            Intent goToDonationEntry = new Intent(MainActivity.this, DonationEntryActivity.class);
            startActivity(goToDonationEntry);
        } else if (id == R.id.nav_donor) {
            Intent goToDonorInfo = new Intent(MainActivity.this, DonorInfoActivity.class);
            goToDonorInfo.putExtra("donorID", donorID);
            goToDonorInfo.putExtra("donorInfo", tvDonorAddr.getText().toString());
            startActivity(goToDonorInfo);
        } else if (id == R.id.nav_agency) {
            Intent goToAgencyInfo = new Intent(MainActivity.this, AgencyInfoActivity.class);
            goToAgencyInfo.putExtra("agencyID", agencyID);
            goToAgencyInfo.putExtra("agencyInfo", tvAgencyAddr.getText().toString());
            startActivity(goToAgencyInfo);
        } else if (id == R.id.nav_reset) {
            resetApp();
        } else if (id == R.id.nav_route_list) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to exit the current route?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent goToRouteList = new Intent(MainActivity.this, RouteListActivity.class);
                    RouteListActivity.selectedRoute.setComplete(true);
                    startActivity(goToRouteList);
                    reEnableApp();
                    finish();
                }
            });
            builder.setPositiveButton("No", null);
            builder.create();
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void resetApp() {
        disableApp();
        reEnableApp();
    }

    private void disableApp() {
        chrmTrip.stop();
        isChrmMeterRunning = false;
        btnStartTimeLog.setOnClickListener(disableOnClickListener);
        btnStartTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnStartTimeLog.setTextColor(getResources().getColor(R.color.white));
        btnStartTimeLogIsEnabled = false;
        btnCompleteTimeLog.setOnClickListener(disableOnClickListener);
        btnCompleteTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnCompleteTimeLog.setTextColor(getResources().getColor(R.color.white));
        btnCompleteTimeLogIsEnabled = false;

    }

    /**
     * Stops the timer and resets it.
     */
    private void reEnableApp() {

        startTime = SystemClock.elapsedRealtime();
        chrmTrip.setBase(startTime);

        AgencyInfoActivity.LAST_IMAGE = null;
        DonorInfoActivity.LAST_IMAGE = null;
        DonationEntryActivity.LAST_IMAGE = null;

        DonationEntryActivity.submissionInfo.clear();

        btnStartTimeLog.setOnClickListener(startTimeOnClickListener);
        btnStartTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        btnStartTimeLog.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        btnStartTimeLogIsEnabled = true;
        btnCompleteTimeLog.setOnClickListener(completeTimeOnClickListener);
        btnCompleteTimeLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        btnCompleteTimeLog.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        btnCompleteTimeLogIsEnabled = true;

    }

    /**
     * Retrieving string encoding of bitmap
     *
     * @param bmp
     * @return
     */
    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    // Server Code

    private void completeRoute(int routeID) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RouteListActivity.selectedRoute.setComplete(true);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        CompleteRouteRequest completeRouteRequest = new CompleteRouteRequest(routeID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(completeRouteRequest);
    }

    private void restartRoute(int routeID) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RouteListActivity.selectedRoute.setComplete(false);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        RestartRouteRequest restartRouteRequest = new RestartRouteRequest(routeID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(restartRouteRequest);
    }


    /**
     * Logged the start time as a timestamp in the database.
     */
    private void updateStartTime() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        UpdateStartTimeRequest updateStartTimeRequest = new UpdateStartTimeRequest(routeID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(updateStartTimeRequest);
    }

    /**
     * Logged the end time as a timestamp in the database.
     */
    private void updateEndTime() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Time logged")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        String tripTime = chrmTrip.getText().toString();

        UpdateEndTimeRequest updateEndTimeRequest = new UpdateEndTimeRequest(routeID, tripTime, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(updateEndTimeRequest);
    }

    /**
     * Uploads the signatures and completes the trips.
     */
    private void uploadSignatures() {

        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Dismissing the progress dialog
                // Showing toast message of the response
                Log.v("log_error", response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("log_tag", error.toString());
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        String name = "temporaryName";

        Bitmap agencyBMP = BitmapFactory.decodeFile(AgencyInfoActivity.LAST_IMAGE);
        String agencySig = getStringImage(agencyBMP); // Encodes the bitmap into a Base64 string

        Bitmap donorBMP = BitmapFactory.decodeFile(DonorInfoActivity.LAST_IMAGE);
        String donorSig = getStringImage(donorBMP);

        Bitmap volunteerBMP = BitmapFactory.decodeFile(DonationEntryActivity.LAST_IMAGE);
        String volunteerSig = getStringImage(volunteerBMP);

        UploadImageRequest uploadAgencySigRequest = new UploadAgencyImageRequest(routeID, name, agencySig, responseListener, errorListener);
        UploadImageRequest uploadDonorSigRequest = new UploadDonorImageRequest(routeID, name, donorSig, responseListener, errorListener);
        UploadImageRequest uploadVolunteerSigRequest = new UploadVolunteerImageRequest(routeID, name, volunteerSig, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(uploadAgencySigRequest);
        queue.add(uploadDonorSigRequest);
        queue.add(uploadVolunteerSigRequest);

        for (String entry : DonationEntryActivity.submissionInfo) {
            entry.replaceAll(" ", "");
            String[] params = entry.split(",", 3);
            Log.v("log_tag", params.toString());
            FoodEntryRequest foodEntryRequest = new FoodEntryRequest(routeID, params[0], params[1], params[2], responseListener, errorListener);
            queue.add(foodEntryRequest);
        }

        disableApp();

        loading.dismiss();
    }


}

abstract class UploadImageRequest extends StringRequest {

    private Map<String, String> params;

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_ID = "ID";

    public UploadImageRequest(String url, int id, String name, String image, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        params = new HashMap<>();
        params.put(KEY_ID, id + "");
        params.put(KEY_NAME, name);
        params.put(KEY_IMAGE, image);

    }

    public Map<String, String> getParams() {
        return params;
    }
}

class UploadAgencyImageRequest extends UploadImageRequest {

    private static final String UPLOAD_AGENCY_URL = "http://ihtest.comxa.com/agencySigs/AgencySignatureUpload.php";

    public UploadAgencyImageRequest(int id, String name, String image, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(UPLOAD_AGENCY_URL, id, name, image, listener, errorListener);
    }
}

class UploadDonorImageRequest extends UploadImageRequest {

    private static final String UPLOAD_DONOR_URL = "http://ihtest.comxa.com/donorSigs/DonorSignatureUpload.php";

    public UploadDonorImageRequest(int id, String name, String image, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(UPLOAD_DONOR_URL, id, name, image, listener, errorListener);
    }
}

class UploadVolunteerImageRequest extends UploadImageRequest {

    private static final String UPLOAD_VOLUNTEER_URL = "http://ihtest.comxa.com/volunteerSigs/VolunteerSignatureUpload.php";

    public UploadVolunteerImageRequest(int id, String name, String image, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(UPLOAD_VOLUNTEER_URL, id, name, image, listener, errorListener);
    }
}

class UpdateStartTimeRequest extends StringRequest {

    private static final String UPDATE_START_URL = "http://ihtest.comxa.com/StartTimeLog.php";
    private Map<String, String> params;

    public UpdateStartTimeRequest(int id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, UPDATE_START_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("ID", id + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

class UpdateEndTimeRequest extends StringRequest {

    private static final String UPDATE_END_URL = "http://ihtest.comxa.com/EndTimeLog.php";
    private Map<String, String> params;

    public UpdateEndTimeRequest(int id, String tripTime, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, UPDATE_END_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("ID", id + "");
        params.put("tripTime", tripTime);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}

class CompleteRouteRequest extends StringRequest {

    private static final String COMPLETE_ROUTE_URL = "http://ihtest.comxa.com/CompleteRoute.php";
    private Map<String, String> params;

    public CompleteRouteRequest(int id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, COMPLETE_ROUTE_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("routeID", id + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

class RestartRouteRequest extends StringRequest {

    private static final String RESTART_ROUTE_URL = "http://ihtest.comxa.com/RestartRoute.php";
    private Map<String, String> params;

    public RestartRouteRequest(int id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, RESTART_ROUTE_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("routeID", id + "");
    }
}

