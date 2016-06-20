package com.example.aaron.islandharvest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static int routeID;
    private static int userID;
    private static int agencyID;
    private static int donorID;
    private static int foodID;

    private TextView tvDonorAddr;
    private TextView tvAgencyAddr;
    private Chronometer chrmTrip;
    private Button btnStartTimeLog;
    private Button btnCompleteTimeLog;

    public static final String USER_PREFERENCES = "userPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "routeID: " + routeID + " " + userID + " " + agencyID + " " + donorID + " " + foodID, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        routeID = Integer.parseInt(sharedPref.getString("routeID", ""));

        Toast.makeText(this, "routeID: " + routeID, Toast.LENGTH_LONG).show();

        tvDonorAddr = (TextView) findViewById(R.id.donorTextView);
        tvAgencyAddr = (TextView) findViewById(R.id.agencyTextView);
        chrmTrip = (Chronometer) findViewById(R.id.tripChronometer);
        btnStartTimeLog = (Button) findViewById(R.id.startTimeLogButton);
        btnCompleteTimeLog = (Button) findViewById(R.id.completeTimeLogButton);

        initializeButtons();

        // Retrieve RouteData from MySQL database
        fetchRouteInfo();

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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

            Intent takeUserToLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(takeUserToLogin);
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
            Intent goToFoodEntry = new Intent(MainActivity.this, FoodEntryActivity.class);
            goToFoodEntry.putExtra("foodID", foodID);
            startActivity(goToFoodEntry);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeButtons() {
        btnStartTimeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chrmTrip.setBase(SystemClock.elapsedRealtime());
                chrmTrip.start();
                updateStartTime();
            }
        });

        btnCompleteTimeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chrmTrip.stop();
                Toast.makeText(MainActivity.this, chrmTrip.getText().toString(), Toast.LENGTH_SHORT).show();

                if (FoodEntryActivity.LAST_IMAGE == null
                        || AgencyInfoActivity.LAST_IMAGE == null
                        || DonorInfoActivity.LAST_IMAGE == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Signatures were not found.")
                            .setNegativeButton("Ok", null)
                            .create()
                            .show();
                } else {
                    uploadSignatures();
                    updateEndTime();
                }
            }
        });
    }

    private void resetApp() {
        chrmTrip.stop();
        chrmTrip.setBase(SystemClock.elapsedRealtime());
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

    public void fetchRouteInfo() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.d("x123", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        String donorAddress = "Donor Address:\n";
                        String agencyAddress = "Agency Address:\n";

                        userID = Integer.parseInt(jsonResponse.getString("userID"));
                        donorID = Integer.parseInt(jsonResponse.getString("donorID"));
                        agencyID = Integer.parseInt(jsonResponse.getString("agencyID"));
                        foodID = Integer.parseInt(jsonResponse.getString("foodID"));
                        tvDonorAddr.setText(donorAddress + jsonResponse.getString("donorAddr"));
                        tvAgencyAddr.setText(agencyAddress + jsonResponse.getString("agencyAddr"));

                        SharedPreferences sharedPref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userID", userID + "");
                        editor.putString("donorID", donorID + "");
                        editor.putString("agencyID", agencyID + "");
                        editor.putString("foodID", foodID + "");
                        editor.apply();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Route data not fetched")
                                .setNegativeButton("Ok", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        FetchRouteDataRequest fetchRouteDataRequest = new FetchRouteDataRequest(routeID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(fetchRouteDataRequest);
    }

    private void updateStartTime() {

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

        UpdateStartTimeRequest updateStartTimeRequest = new UpdateStartTimeRequest(routeID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(updateStartTimeRequest);
    }

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

        UpdateEndTimeRequest updateEndTimeRequest = new UpdateEndTimeRequest(routeID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(updateEndTimeRequest);
    }

    private void uploadSignatures() {

        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Dismissing the progress dialog
                // Showing toast message of the response
                Log.v("log_error", response);
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
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

        Bitmap volunteerBMP = BitmapFactory.decodeFile(FoodEntryActivity.LAST_IMAGE);
        String volunteerSig = getStringImage(volunteerBMP);

        UploadImageRequest uploadAgencySigRequest = new UploadAgencyImageRequest(routeID, name, agencySig, responseListener, errorListener);
        UploadImageRequest uploadDonorSigRequest = new UploadDonorImageRequest(routeID, name, donorSig, responseListener, errorListener);
        UploadImageRequest uploadVolunteerSigRequest = new UploadVolunteerImageRequest(routeID, name, volunteerSig, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(uploadAgencySigRequest);
        queue.add(uploadDonorSigRequest);
        queue.add(uploadVolunteerSigRequest);

        for ( String entry : FoodEntryActivity.submissionInfo) {
            entry.replaceAll(" ", "");
            String[] params = entry.split(",", 3);
            Log.v("log_tag", params.toString());
            FoodEntryRequest foodEntryRequest = new FoodEntryRequest(routeID, params[0], params[1], params[2], responseListener, errorListener);
            queue.add(foodEntryRequest);
        }

        FoodEntryActivity.submissionInfo.clear();
        resetApp();

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

    public UpdateEndTimeRequest(int id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, UPDATE_END_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("ID", id + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}

