package com.dash.myapplication;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxyel on 2/8/15.
 */
public class UploadPinTask extends AsyncTask<LatLng, Integer, Boolean> {

        public static final String TAG = UploadPinTask.class.getSimpleName();
        HttpClient httpclient = new DefaultHttpClient();
        Context mContext;

        public UploadPinTask(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(LatLng... loc) {
            HttpPost httppost = new HttpPost("http://ec2-52-0-239-131.compute-1.amazonaws.com/uploadlocation");

            try {
                // add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("uid", "pin"));
                nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(loc[0].latitude)));
                nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(loc[0].longitude)));
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
            Log.d(TAG, "Starting to upload pin...\n");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.d(TAG, "Finished uploading pin.\n");
        }




}
