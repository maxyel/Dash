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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by elliott on 2/7/2015.
 */
public class DownloadLocationsTask extends AsyncTask<Integer, Integer, Map<String, Location>>  {

    public static final String TAG = DownloadLocationsTask.class.getSimpleName();
    HttpClient httpclient = new DefaultHttpClient();

    Context mContext;

    public DownloadLocationsTask(Context context) {
        mContext = context;
    }

    @Override
    protected Map<String, Location> doInBackground(Integer... dummyParams) {
        HttpGet httpget = new HttpGet("http://ec2-52-0-239-131.compute-1.amazonaws.com/downloadlocations");

        HttpResponse response;

        try {
            // Execute HTTP Post Request
            response = httpclient.execute(httpget);
            InputStream inputStream = response.getEntity().getContent();
            String rawJSON = convertInputStreamToString(inputStream);
            //Log.d(TAG, rawJSON);

            JSONArray array = new JSONArray(rawJSON);
            HashMap<String, Location> locs = new HashMap();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Location loc = new Location("server");
                loc.setAccuracy(10);
                loc.setLatitude(obj.getDouble("lat"));
                loc.setLongitude(obj.getDouble("lon"));
                locs.put(obj.getString("uid"), loc);
            }

            //Log.d(TAG, locs.toString());

            return locs;

        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "Starting to download locations...\n");
    }

    @Override
    protected void onPostExecute(Map<String, Location> stringLocationDictionary) {
        ((MapsActivity)mContext).mLocations = stringLocationDictionary;
        Log.d(TAG, "Finished downloading locations.\n");
    }
}
