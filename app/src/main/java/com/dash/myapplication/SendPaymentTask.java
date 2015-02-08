package com.dash.myapplication;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
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

/**
 * Created by Matt on 2/8/2015.
 */
public class SendPaymentTask extends AsyncTask<Integer, Integer, Boolean> {
    public static final String TAG = SendPaymentTask.class.getSimpleName();
    public static final String EMAIL = "mricha56@jhu.edu";

    HttpClient httpclient = new DefaultHttpClient();
    Context mContext;

    public SendPaymentTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        Log.d(TAG, "Payment successful...? " + aBoolean);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {

        HttpPost httppost = new HttpPost("https://api.venmo.com/v1/payments");
        String accessToken = ((MapsActivity)mContext).venmoAuthToken;

        try {
            // add your data
            java.util.List<NameValuePair> nameValuePairs = new java.util.ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
            nameValuePairs.add(new BasicNameValuePair("email", EMAIL));
            nameValuePairs.add(new BasicNameValuePair("amount", integers[0].toString()));
            nameValuePairs.add(new BasicNameValuePair("note", "Da$h cash."));
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
}
