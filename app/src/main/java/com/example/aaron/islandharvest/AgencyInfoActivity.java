package com.example.aaron.islandharvest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AgencyInfoActivity extends AppCompatActivity {

    private static final int SIGNATURE_ACTIVITY = 1;
    public static String LAST_IMAGE;
    private TextView agencyAddrTV;
    private ImageView agencySignatureIV;

    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ID = getIntent().getExtras().getInt("agencyID");

        agencyAddrTV = (TextView) findViewById(R.id.agencyAddressTextView);
        agencySignatureIV = (ImageView) findViewById(R.id.agencySignatureImageButton);

        String agencyAddr = getIntent().getStringExtra("agencyInfo");
        agencyAddrTV.setText(agencyAddr);

        if (LAST_IMAGE != null) {
            Bitmap image = BitmapFactory.decodeFile(LAST_IMAGE);
            agencySignatureIV.setImageBitmap(image);
        }

        initializeOnClickListeners();
    }

    private void initializeOnClickListeners() {
        agencySignatureIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeUserToCaptureSignature = new Intent(AgencyInfoActivity.this, CaptureSignature.class);
                takeUserToCaptureSignature.putExtra("caller_class", "agency");
                startActivityForResult(takeUserToCaptureSignature, SIGNATURE_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SIGNATURE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String status = bundle.getString("status");
                    String filePath = bundle.getString("filePath");

                    if (filePath != null) {
                        LAST_IMAGE = filePath;
                        Bitmap image = BitmapFactory.decodeFile(LAST_IMAGE);
                        agencySignatureIV.setImageBitmap(image);
                    }

                    if (status.equalsIgnoreCase("done")) {
                        Bitmap image = BitmapFactory.decodeFile(LAST_IMAGE);
                        agencySignatureIV.setImageBitmap(image);
                    }
                }
                break;
        }
    }

}
