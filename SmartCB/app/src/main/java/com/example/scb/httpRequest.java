package com.example.scb;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class httpRequest extends AsyncTask<String, Void, String> {


    @Override
    protected void onPostExecute(String s) {

    }
    @Override
    protected String doInBackground(String... params) {
        try {
            /* Change the IP to the IP you set in the arduino sketch */
            URL url = new URL("http://192.168.1.178/?" + params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result.append(inputLine).append("\n");

            in.close();
            connection.disconnect();
            return result.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}