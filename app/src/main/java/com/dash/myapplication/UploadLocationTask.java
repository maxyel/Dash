package com.dash.myapplication;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URL;

import static java.security.AccessController.getContext;

/**
 * Created by elliott on 2/7/2015.
 */
public class UploadLocationTask extends AsyncTask<Location, Integer, Boolean> {

    public static final String TAG = UploadLocationTask.class.getSimpleName();
    HttpClient httpclient = new DefaultHttpClient();
    String androidId;
    Context mContext;

    public UploadLocationTask(Context context) {
        mContext = context;
        androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    protected Boolean doInBackground(Location... locations) {
        HttpPost httppost = new HttpPost("http://ec2-52-0-239-131.compute-1.amazonaws.com/uploadlocation");

        try {
            // add your data
            java.util.List<NameValuePair> nameValuePairs = new java.util.ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("uid", androidId));
            nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(locations[0].getLatitude())));
            nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(locations[0].getLongitude())));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "Starting to upload location...\n");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Log.d(TAG, "Finished uploading location.\n");
    }

}
