package com.dash.myapplication;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
public class SendPrizeMoneyTask extends AsyncTask<Integer, Integer, Boolean> {
    public static final String TAG = SendPrizeMoneyTask.class.getSimpleName();
    public static String EMAIL = "mrich520@aol.com";//"mricha56@jhu.edu";
    public static final String ACCESS_TOKEN = "B3e7fbU5tEsBNYvE5zRLFEZ4Uzsw47fk";

    HttpClient httpclient = new DefaultHttpClient();
    Context mContext;

    public SendPrizeMoneyTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        String aId = ((MapsActivity) mContext).androidId;

        if (aId.equals("493363329f7d669")) {
            EMAIL = "mrich520@aol.com";
        } else if (aId.equals("b0422120814c5ce7")) {
            EMAIL = "ronboger99@yahoo.com";
        } else if (aId.equals("f12e70ef3a7e170b")) {
            EMAIL = "max.yelsky@gmail.com";
        } else if (aId.equals("e68a9cac58d88c80")) {
            EMAIL = "elliotbinder@gmail.com";
        } else {
            Log.d(TAG, "waaattt");
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        Log.d(TAG, "Prize successfuly awarded...? " + aBoolean);

        String resultMessage;
        if (aBoolean) {
            resultMessage = "Claim your $1 prize in Venmo!";
        } else {
            resultMessage = "ERROR: Payment didn't go through!";
        }
        Toast.makeText(mContext, resultMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {

        HttpPost httppost = new HttpPost("https://api.venmo.com/v1/payments");

        try {
            // add your data
            java.util.List<NameValuePair> nameValuePairs = new java.util.ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("access_token", ACCESS_TOKEN));
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
