package com.newitventure.hoteltv.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leviathan on 5/31/2017.
 */

public class HttpHandler {
    private static String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;


        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(is);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException:" + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream in){

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine())!= null) {
                sb.append(line).append('\n');
            }
            in.close();

        } catch (IOException e) {
            Log.e(TAG,"IOException: " +e.getMessage());
        }
        return sb.toString();
    }
}
