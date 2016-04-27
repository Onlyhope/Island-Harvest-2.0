package com.example.aaron.islandharvest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String USER_PREFERENCES = "userPreferences";

    // Layout Components
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializing Layout Components
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        initalizeButtons();
    }

    private void initalizeButtons() {
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity(v);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });
    }

    private void loginUser(View view) {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        String email = jsonResponse.getString("email");
                        String fullName = jsonResponse.getString("fullName");

                        SharedPreferences sharedPref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", email);
                        editor.putString("fullName", fullName);
                        editor.apply();

                        Intent takeUserToMain = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(takeUserToMain);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Login Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        LoginRequest loginRequest = new LoginRequest(email, password, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }

    private void goToRegisterActivity(View view) {
        Intent takeUserToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(takeUserToRegister);
    }
}