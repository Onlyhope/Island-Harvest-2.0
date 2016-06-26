package com.example.aaron.islandharvest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodEntryActivity extends AppCompatActivity {

    private static final int SIGNATURE_ACTIVITY = 1;
    private static final int OTHER_ACTIVITY = 2;
    public static String LAST_IMAGE; // TODO eliminate dependency of static variables
    public static ArrayList<String> submissionInfo = new ArrayList<>();
    private EditText etFoodAmount;
    private Spinner spinFoodDescrip;
    private Spinner spinFoodType;
    private ImageView signatureIV;
    private Button btnSubmitFood;
    private Button btnViewSubmission;
    private ArrayAdapter<CharSequence> foodTypeAdapter;
    private ArrayAdapter<CharSequence> foodDescripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        etFoodAmount = (EditText) findViewById(R.id.foodAmountEditText);
        btnSubmitFood = (Button) findViewById(R.id.submitInfoButton);
        btnViewSubmission = (Button) findViewById(R.id.viewSubmissionButotn);
        signatureIV = (ImageView) findViewById(R.id.signatureImageView);

        if (LAST_IMAGE != null) {
            Bitmap image = BitmapFactory.decodeFile(LAST_IMAGE);
            signatureIV.setImageBitmap(image);
        }

        initializeSpinner();
        initializeOnClickListeners();
    }


    /**
     * Initializes the spinners
     */
    private void initializeSpinner() {
        spinFoodType = (Spinner) findViewById(R.id.foodTypeSpinner);

        foodTypeAdapter = ArrayAdapter.createFromResource(this, R.array.food_type, android.R.layout.simple_spinner_item);
        foodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFoodType.setAdapter(foodTypeAdapter);

        spinFoodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView) parent.getChildAt(position)).setTextColor(Color.rgb(104,159,56));
                Toast.makeText(parent.getContext(), parent.getSelectedItem().toString() + " selected", Toast.LENGTH_SHORT).show();
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
//                ((TextView) parent.getChildAt(position)).setTextColor(Color.rgb(104,159,56));
                Toast.makeText(parent.getContext(), parent.getSelectedItem().toString() + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * Initializes the onClickListeners
     */
    private void initializeOnClickListeners() {
        btnViewSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodEntryActivity.this);
                String submission = getSubmissionInfo();
                builder.setMessage(submission)
                        .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submissionInfo.clear();
                                Toast.makeText(FoodEntryActivity.this, "Submission Info resetted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("Ok", null)
                        .create()
                        .show();
            }
        });

        btnSubmitFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signatureIV.getDrawable() == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FoodEntryActivity.this);
                    builder.setMessage("Please sign to submit info...")
                            .setNegativeButton("Ok", null)
                            .create()
                            .show();
                    return;
                }
                addDonationEntry();
            }
        });

        signatureIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeUserToCaptureSignature = new Intent(FoodEntryActivity.this, CaptureSignature.class);
                takeUserToCaptureSignature.putExtra("caller_class", "volunteer");
                startActivityForResult(takeUserToCaptureSignature, SIGNATURE_ACTIVITY);
            }
        });
    }

    /**
     * Result from SignatureActivity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SIGNATURE_ACTIVITY:    // Passes earlier as 1 (value is arbitrary)
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String status = bundle.getString("status");
                    String filePath = bundle.getString("filePath");

                    if (filePath != null) {
                        LAST_IMAGE = filePath;
                        Bitmap image = BitmapFactory.decodeFile(LAST_IMAGE);
                        signatureIV.setImageBitmap(image);
                    }

                    if (status.equalsIgnoreCase("done")) {
                        Toast toast = Toast.makeText(this, "Signature capture successful!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                break;
            case OTHER_ACTIVITY:
                break;
            default:
        }
    }

    /**
     * Adds an entry on to the submissionInfo ArrayList
     */
    private void addDonationEntry() {
        String foodDescrip = spinFoodDescrip.getSelectedItem().toString();
        String foodType = spinFoodType.getSelectedItem().toString();
        String foodAmount = etFoodAmount.getText().toString().trim();

        StringBuilder sb = new StringBuilder();
        sb.append(foodDescrip);
        sb.append(", ");
        sb.append(foodType);
        sb.append(", ");
        sb.append(foodAmount);
        sb.append(" LB");

        submissionInfo.add(sb.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(FoodEntryActivity.this);
        builder.setMessage("Added")
                .setNegativeButton("Ok", null)
                .create()
                .show();
    }

    /**
     * Returns the submissionInfo ArrayList as a string.
     * Use for inserting into SQL
     *
     * @return
     */
    private String getSubmissionInfo() {
        StringBuilder sb = new StringBuilder();
        for (String entry : submissionInfo) {
            sb.append(entry);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Method is not used.
     */
    public void editFoodDescrip() {
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

        int ID = 0; // placeHolder variable, for method not used

//        FoodEntryRequest foodEntryRequest = new FoodEntryRequest(ID, foodDescrip, foodType, foodAmount, responseListener, errorListener);
//        RequestQueue queue = Volley.newRequestQueue(FoodEntryActivity.this);
//        queue.add(foodEntryRequest);
    }

    // Server Code

    /**
     * Method is not used anymore
     */
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
                            case "Dry":
                                spinFoodType.setSelection(1);
                                break;
                            case "Frozen":
                                spinFoodType.setSelection(2);
                                break;
                            case "Perishable":
                                spinFoodType.setSelection(3);
                                break;
                            case "Non-Perishable":
                                spinFoodType.setSelection(4);
                                break;
                            default:
                                spinFoodType.setSelection(0);
                                break;
                        }

                        String selectionDescrip = jsonResponse.getString("foodDescrip");
                        switch (selectionDescrip) {
                            case "Non Foods":
                                spinFoodDescrip.setSelection(1);
                                break;
                            case "Baby Food / Formula":
                                spinFoodDescrip.setSelection(2);
                                break;
                            case "Beverage":
                                spinFoodDescrip.setSelection(3);
                                break;
                            case "Bread & Bakery":
                                spinFoodDescrip.setSelection(4);
                                break;
                            case "Meals / Entrees / Soups":
                                spinFoodDescrip.setSelection(5);
                                break;
                            case "Dairy Products":
                                spinFoodDescrip.setSelection(6);
                                break;
                            case "Health & Beauty Care":
                                spinFoodDescrip.setSelection(7);
                                break;
                            case "Cleaning Products":
                                spinFoodDescrip.setSelection(8);
                                break;
                            case "Juice 100%":
                                spinFoodDescrip.setSelection(9);
                                break;
                            case "Meat / Fish / Poultry":
                                spinFoodDescrip.setSelection(10);
                                break;
                            case "Mixed & Assorted":
                                spinFoodDescrip.setSelection(11);
                                break;
                            case "Pet Food/Care":
                                spinFoodDescrip.setSelection(12);
                                break;
                            case "Produce Fresh":
                                spinFoodDescrip.setSelection(13);
                                break;
                            case "Prepared & Perishable":
                                spinFoodDescrip.setSelection(14);
                                break;
                            default:
                                spinFoodDescrip.setSelection(0);
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

        int ID = 0; // placeholder variable for not used method

        FetchFoodRequest fetchFoodRequest = new FetchFoodRequest(ID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(FoodEntryActivity.this);
        queue.add(fetchFoodRequest);
    }
}


