package com.example.aaron.islandharvest;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FetchFoodRequest extends StringRequest {

    private static final String FOOD_INFO_FETCH_REQUEST_URL = "http://ihtest.comxa.com/FetchFood.php";
    private Map<String, String> params;

    public FetchFoodRequest(int id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, FOOD_INFO_FETCH_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("ID", id + "");
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }
}
