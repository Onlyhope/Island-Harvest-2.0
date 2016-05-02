package com.example.aaron.islandharvest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 5/1/2016.
 */
public class FoodEntryRequest extends StringRequest {

    private static final String FOOD_ENTRY_REQUEST_URL = "http://ihtest.comxa.com/UpdateFood.php";
    private Map<String, String> params;

    public FoodEntryRequest(int id, String foodDescrip, String foodType, double foodAmount, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, FOOD_ENTRY_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("ID", id + "");
        params.put("foodDescrip", foodDescrip);
        params.put("foodType", foodType);
        params.put("foodAmount", foodAmount + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

