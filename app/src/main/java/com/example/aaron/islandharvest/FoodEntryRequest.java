package com.example.aaron.islandharvest;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 5/1/2016.
 */
public class FoodEntryRequest extends StringRequest {

    private static final String FOOD_ENTRY_REQUEST_URL = "http://ihtest.comxa.com/FoodEntry.php";
    private Map<String, String> params;

    public FoodEntryRequest(int routeID, String foodDescrip, String foodType, String foodAmount, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, FOOD_ENTRY_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("routeID", routeID + "");
        params.put("foodDescrip", foodDescrip);
        params.put("foodType", foodType);
        params.put("foodAmount", foodAmount + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

