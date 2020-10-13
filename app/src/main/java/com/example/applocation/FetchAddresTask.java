package com.example.applocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddresTask extends AsyncTask<Location, Void,String> {
    private Context mContext;
    private OnTaskCompleted mListener;

    public FetchAddresTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    private final String TAG = FetchAddresTask.class.getSimpleName();

    @Override
    protected String doInBackground(Location... params) {
        Geocoder geocoder =  new Geocoder(mContext, Locale.getDefault());

        Location location = params[0];
        List<Address> addresses = null;
        String resultsMessage = "";

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            resultsMessage = mContext.getString(R.string.service_not_available);
            Log.e(TAG, resultsMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException){

            resultsMessage = mContext.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, resultsMessage+". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0){
            if (resultsMessage.isEmpty()){
                resultsMessage = mContext.getString(R.string.no_address_found);
                Log.e(TAG, resultsMessage);
            }
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                addressParts.add(address.getAddressLine(i));
            }
            resultsMessage = TextUtils.join("\n", addressParts);
        }
        return resultsMessage;
    }

    @Override
    protected void onPostExecute(String address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }
}
