package com.example.aaron.islandharvest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FoodEntryActivity extends AppCompatActivity {

    private EditText etFoodDescrip;
    private EditText etFoodAmount;
    private Spinner spinFoodType;
    private Button btnSubmitFood;

    private static final String USER_PREFERENCES = "userPreferences";

    private int ID;


    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeSpinner();

        etFoodDescrip = (EditText) findViewById(R.id.foodDescripEditText);
        etFoodAmount = (EditText) findViewById(R.id.foodAmountEditText);
        btnSubmitFood = (Button) findViewById(R.id.submitInfoButton);

        SharedPreferences sharedPref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        ID = Integer.parseInt(sharedPref.getString("foodID", ""));

        Toast.makeText(this, "foodID: " + ID, Toast.LENGTH_LONG).show();

        fetchFoodInfo();
    }


    // Initializes the foodType Spinner
    private void initializeSpinner() {
        spinFoodType = (Spinner) findViewById(R.id.foodTypeSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.food_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFoodType.setAdapter(adapter);

        spinFoodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(), parent.getItemIdAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeButtons() {

    }

    public void editFoodDescrip(View view) {
        final String foodDescrip = etFoodDescrip.getText().toString().trim();
        final String foodType = spinFoodType.getSelectedItem().toString();
        final double foodAmount = Double.parseDouble(etFoodAmount.getText().toString().trim());

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FoodEntryActivity.this);
                        builder.setMessage("Food entry was not submitted")
                                .setNegativeButton("Retry", null)
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
                Toast.makeText(FoodEntryActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        Toast.makeText(FoodEntryActivity.this, "Submitting Information...", Toast.LENGTH_SHORT).show();

        FoodEntryRequest foodEntryRequest = new FoodEntryRequest(foodDescrip, foodType, foodAmount, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(FoodEntryActivity.this);
        queue.add(foodEntryRequest);
    }

    // Server Code

    public void fetchFoodInfo() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("x123", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        etFoodDescrip.setText(jsonResponse.getString("foodDescrip"));
                        etFoodAmount.setText(jsonResponse.getString("foodAmount"));
                        String selection = jsonResponse.getString("foodType");

                        switch (selection) {
                            case "Dry": spinFoodType.setSelection(1);
                                break;
                            case "Frozen": spinFoodType.setSelection(2);
                                break;
                            case "Perishable": spinFoodType.setSelection(3);
                                break;
                            case "Non-Perishable": spinFoodType.setSelection(4);
                                break;
                            default: spinFoodType.setSelection(0);
                                break;
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FoodEntryActivity.this);
                        builder.setMessage("Food data not fetched")
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
                Toast.makeText(FoodEntryActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        FetchFoodRequest fetchFoodRequest = new FetchFoodRequest(ID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(FoodEntryActivity.this);
        queue.add(fetchFoodRequest);
    }

}
