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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FoodEntryActivity extends AppCompatActivity {

    private EditText etFoodAmount;
    private Spinner spinFoodDescrip;
    private Spinner spinFoodType;
    private Button btnSubmitFood;

    private static final String USER_PREFERENCES = "userPreferences";

    private int ID;


    private ArrayAdapter<CharSequence> foodTypeAdapter;
    private ArrayAdapter<CharSequence> foodDescripAdapter;

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

        etFoodAmount = (EditText) findViewById(R.id.foodAmountEditText);
        btnSubmitFood = (Button) findViewById(R.id.submitInfoButton);

        ID = getIntent().getExtras().getInt("foodID");

        Toast.makeText(this, "foodID: " + ID, Toast.LENGTH_LONG).show();

        fetchFoodInfo();
    }


    // Initializes the foodType Spinner
    private void initializeSpinner() {
        spinFoodType = (Spinner) findViewById(R.id.foodTypeSpinner);

        foodTypeAdapter = ArrayAdapter.createFromResource(this, R.array.food_type, android.R.layout.simple_spinner_item);
        foodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFoodType.setAdapter(foodTypeAdapter);

        spinFoodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(), parent.getSelectedItem().toString() + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinFoodDescrip = (Spinner) findViewById(R.id.foodDescripSpinner);

        foodDescripAdapter = ArrayAdapter.createFromResource(this, R.array.food_descrip, android.R.layout.simple_spinner_item);
        foodDescripAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFoodDescrip.setAdapter(foodDescripAdapter);

        spinFoodDescrip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(), parent.getSelectedItem().toString() + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeButtons() {
        btnSubmitFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFoodDescrip(v);
            }
        });
    }

    public void editFoodDescrip(View view) {
        final String foodDescrip = spinFoodDescrip.getSelectedItem().toString();
        final String foodType = spinFoodType.getSelectedItem().toString();
        final double foodAmount = Double.parseDouble(etFoodAmount.getText().toString().trim());

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodEntryActivity.this);
                builder.setMessage(response)
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FoodEntryActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        Toast.makeText(FoodEntryActivity.this, "Submitting Information...", Toast.LENGTH_SHORT).show();

        FoodEntryRequest foodEntryRequest = new FoodEntryRequest(ID, foodDescrip, foodType, foodAmount, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(FoodEntryActivity.this);
        queue.add(foodEntryRequest);
    }

    // Server Code

    public void fetchFoodInfo() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        etFoodAmount.setText(jsonResponse.getString("foodAmount"));
                        String selectionType = jsonResponse.getString("foodType");

                        switch (selectionType) {
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

                        String selectionDescrip = jsonResponse.getString("foodDescrip");
                            switch (selectionDescrip) {
                                case "Non Foods": spinFoodDescrip.setSelection(1);
                                    break;
                                case "Baby Food / Formula": spinFoodDescrip.setSelection(2);
                                    break;
                                case "Beverage": spinFoodDescrip.setSelection(3);
                                    break;
                                case "Bread &amp; Bakery": spinFoodDescrip.setSelection(4);
                                    break;
                                case "Meals / Entrees / Soups": spinFoodDescrip.setSelection(5);
                                    break;
                                case "Dairy Products": spinFoodDescrip.setSelection(6);
                                    break;
                                case "Health &amp; Beauty Care": spinFoodDescrip.setSelection(7);
                                    break;
                                case "Cleaning Products": spinFoodDescrip.setSelection(8);
                                    break;
                                case "Juice 100%": spinFoodDescrip.setSelection(9);
                                    break;
                                case "Meat / Fish / Poultry": spinFoodDescrip.setSelection(10);
                                    break;
                                case "Mixed &amp; Assorted": spinFoodDescrip.setSelection(11);
                                    break;
                                case "Pet Food/Care": spinFoodDescrip.setSelection(12);
                                    break;
                                case "Produce Fresh": spinFoodDescrip.setSelection(13);
                                    break;
                                case "Prepared &amp; Perishable": spinFoodDescrip.setSelection(14);
                                default: spinFoodDescrip.setSelection(0);
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


