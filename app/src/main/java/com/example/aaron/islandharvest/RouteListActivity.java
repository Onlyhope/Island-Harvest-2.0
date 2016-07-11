package com.example.aaron.islandharvest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteListActivity extends AppCompatActivity {

    private ListView routeListView;
    private List<Route> routes;
    private RouteListAdapter routeAdapter;
    private Route selectedRoute;

    private ArrayList<Integer> listOfRouteID;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userID = getIntent().getIntExtra("userID", -1);

        fetchUserRoutes(userID);
        routes = new ArrayList<>();
        for (int routeID : listOfRouteID) {
            fetchRouteInfo(routeID);
        }

        initializeEventHandlers();

        routeAdapter = new RouteListAdapter(this, R.layout.route_list_layout, routes);
        routeListView = (ListView) findViewById(R.id.routeListView);
        routeListView.setAdapter(routeAdapter);


    }

    private void initializeEventHandlers() {
        routeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedRoute.equals(routes.get(position))) {
                    Intent goToMainActivity = new Intent(RouteListActivity.this, MainActivity.class);
                    goToMainActivity.putExtra("routeID", selectedRoute.getID());
                    goToMainActivity.putExtra("userID", selectedRoute.getUserID());
                    goToMainActivity.putExtra("donorID", selectedRoute.getDonorID());
                    goToMainActivity.putExtra("agencyID", selectedRoute.getAgencyID());
                    startActivity(goToMainActivity);
                } else {
                    selectedRoute = routes.get(position);
                }
            }
        });
    }

    // Server Code
    private void fetchUserRoutes(final int userID) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        while (true) {
                            int id = 0;
                            listOfRouteID.add(id);
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RouteListActivity.this);
                        builder.setMessage("Routes not fetched")
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
                Toast.makeText(RouteListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        FetchUserRoutesRequest fetchUserRoutesRequest = new FetchUserRoutesRequest(userID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(RouteListActivity.this);
        queue.add(fetchUserRoutesRequest);
    }

    private void fetchRouteInfo(final int routeID) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        int userID = Integer.parseInt(jsonResponse.getString("userID"));
                        int donorID = Integer.parseInt(jsonResponse.getString("donorID"));
                        int agencyID = Integer.parseInt(jsonResponse.getString("agencyID"));
                        String donorAddress = jsonResponse.getString("donorAddr");
                        String agencyAddress = jsonResponse.getString("agencyAddr");

                        Route route = new Route(routeID, userID, agencyID, donorID);
                        routes.add(route);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RouteListActivity.this);
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
                Toast.makeText(RouteListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        FetchRouteDataRequest fetchRouteDataRequest = new FetchRouteDataRequest(routeID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(RouteListActivity.this);
        queue.add(fetchRouteDataRequest);
    }
}

class FetchUserRoutesRequest extends StringRequest {

    private static final String FETCH_USER_ROUTE_URL = "http://ihtest.comxa.com/FetchUserRoute.php";
    private Map<String, String> params;

    public FetchUserRoutesRequest(int userID, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, FETCH_USER_ROUTE_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("userID", userID + "");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

