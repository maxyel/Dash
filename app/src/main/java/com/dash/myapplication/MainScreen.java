package com.dash.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainScreen extends ActionBarActivity {

    public static final String TAG = MainScreen.class.getSimpleName();
    public static final String AUTH_EXTRA = "com.dash.myapplication.AUTH_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void mapTime(View view) {
        // Receive the auth token from the intent that started MainScreen
        final String prefix = "access_token=";
        String fullURL= getIntent().getDataString();
        int tokenStart = fullURL.indexOf(prefix) + prefix.length();
        int tokenEnd = fullURL.indexOf("&", tokenStart);
        if (tokenEnd < 0)
            tokenEnd = fullURL.length();

        String authToken = fullURL.substring(tokenStart, tokenEnd);
        Log.d(TAG, "Auth: " + authToken);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(AUTH_EXTRA, authToken);
        startActivity(intent);
    }
}
