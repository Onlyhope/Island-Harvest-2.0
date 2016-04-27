package com.example.aaron.islandharvest;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 4/25/2016.
 */
public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://ihtest.comxa.com/Register.php";
    private Map<String, String> params;

    public RegisterRequest(String fullName, String email, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener); // 4th Params is an ErrorListener
        params = new HashMap<>();
        params.put("fullName", fullName);
        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
