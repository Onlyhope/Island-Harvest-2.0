package com.example.aaron.islandharvest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 5/1/2016.
 */
public class FetchRouteDataRequest extends StringRequest {

    private static final String FETCH_ROUTE_DATA_URL = "http://ihtest.comxa.com/FetchRoute.php";
    private Map<String, String> params;

    public FetchRouteDataRequest(int id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, FETCH_ROUTE_DATA_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("ID", id + "");
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }
}
