package com.example.applocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements FetchAddresTask.OnTaskCompleted{
    private TextView mTxtLocation;
    private Button mBtnLocation;
    private ImageView mImageView;
    private boolean mTrackingLocaton;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private AnimatorSet mRotateAnim;

    private static final int REQUEST_LOCATION_PERISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.img_android);
        mTxtLocation = findViewById(R.id.txt_location);
        mBtnLocation = findViewById(R.id.btn_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mRotateAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.rotate);
        mRotateAnim.setTarget(mImageView);

        if (savedInstanceState != null){
            mTrackingLocaton = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);

        }

        mBtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTrackingLocaton){
                    startTrackingLocation();
                } else {
                    stopTrackingLocation();
                }
            }
        });

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (mTrackingLocaton){
                    new FetchAddresTask(MainActivity.this, MainActivity.this).execute(locationResult.getLastLocation());
                }
            }
        };

    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERISSION);
        } else {
            mTrackingLocaton = true;
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);

            mTxtLocation.setText(getString(R.string.address_text,
                    getString(R.string.loading),
                    System.currentTimeMillis()));
            mTxtLocation.setText(R.string.stop_tracking_location);
            mRotateAnim.start();

        }
    }

    private void stopTrackingLocation() {
        if (mTrackingLocaton){
            mTrackingLocaton = false;
            mBtnLocation.setText(R.string.start_tracking_location);
            mTxtLocation.setText(R.string.txtview_hint);
            mRotateAnim.end();
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocaton);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        if (mTrackingLocaton){
            mTxtLocation.setText(getString(R.string.address_text, result, System.currentTimeMillis()));
        }
    }

    @Override
    protected void onPause() {
        if (mTrackingLocaton){
            stopTrackingLocation();
            mTrackingLocaton = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mTrackingLocaton){
            startTrackingLocation();
        }
        super.onResume();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}