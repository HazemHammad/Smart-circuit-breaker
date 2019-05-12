package com.example.scb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckConsum extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;
    int cons = 0, cost = 0;
    String error = "NO";

    @Override
    protected String doInBackground(String... params) {
        try {

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
            cancel(true);
        } catch (Exception e) {
            cancel(true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            if (!isCancelled()) {
                super.onPostExecute(s);
                Pattern p = Pattern.compile("(?<=cons=)\\d+");
                Matcher m = p.matcher(s);
                if (m.find()) {
                    cons = Integer.parseInt(m.group());
                    cost = 0;
                    if (cons <= 50)
                        cost = cons * 22;
                    else if (cons <= 100)
                        cost = (50 * 22) + (cons - 50) * 30;
                    else if (cons <= 200)
                        cost = (50 * 22) + (50 * 30) + (cons - 100) * 36;
                    else if (cons <= 350)
                        cost = (50 * 22) + (50 * 30) + (100 * 36) + (cons - 200) * 70;
                    else if (cons <= 650)
                        cost = (50 * 22) + (50 * 30) + (100 * 36) + (150 * 70) + (cons - 350) * 90;
                    else if (cons <= 1000)
                        cost = (50 * 22) + (50 * 30) + (100 * 36) + (150 * 70) + (300 * 90) + (cons - 650) * 135;
                    else if (cons > 1000)
                        cost = cons * 145;
                }
            }
        }catch (Exception e){
            error = e.getMessage();
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}


