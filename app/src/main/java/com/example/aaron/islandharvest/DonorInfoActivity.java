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
import android.widget.Toast;

public class DonorInfoActivity extends AppCompatActivity {

    private static final int SIGNATURE_ACTIVITY = 1;
    public static String LAST_IMAGE;
    private TextView donorAddrTV;
    private ImageView donorSignatureIV;

    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ID = getIntent().getExtras().getInt("donorID");
        Toast.makeText(this, "donorID = " + ID, Toast.LENGTH_SHORT).show();

        donorAddrTV = (TextView) findViewById(R.id.donorAddressTextView);
        donorSignatureIV = (ImageView) findViewById(R.id.donorSignatureImageButton);

        String donorAddr = getIntent().getStringExtra("donorInfo");
        donorAddrTV.setText(donorAddr);

        if (LAST_IMAGE != null) {
            Bitmap image = BitmapFactory.decodeFile(LAST_IMAGE);
            donorSignatureIV.setImageBitmap(image);
        }

        initializeOnClickListeners();
    }

    private void initializeOnClickListeners() {
        donorSignatureIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeUserToCaptureSignature = new Intent(DonorInfoActivity.this, CaptureSignature.class);
                takeUserToCaptureSignature.putExtra("caller_class", "donor");
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
                        donorSignatureIV.setImageBitmap(image);
                    }

                    if (status.equalsIgnoreCase("done")) {
                        Bitmap image = BitmapFactory.decodeFile(LAST_IMAGE);
                        donorSignatureIV.setImageBitmap(image);
                        Toast toast = Toast.makeText(this, "Signature capture successful!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                break;
        }
    }

}
