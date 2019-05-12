package com.example.scb;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startbtn);
        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, homepage.class);
                startActivity(in);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        new MainActivity.Background_get().execute("getCons");

    }

    private void pushNotification(String title, String text){
        Intent intent = new Intent(this, Consum.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Circuit Breaker", "Circuit Breaker", importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder NB = new NotificationCompat.Builder((Context)MainActivity.this, "Circuit Breaker");
            NB.setSmallIcon(R.drawable.ic_launcher_background);
            NB.setContentTitle(title);
            NB.setContentText(text);
            NB.setContentIntent(pendingIntent);
            NB.setAutoCancel(true);
            NB.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(1, NB.build());
        }
    }

    private class Background_get extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        int cons = 0, cost = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog((Context)MainActivity.this);
            progressDialog.setTitle("Connecting....");
            progressDialog.setMessage("please wait ...." + "\n" + "Connecting to the system");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

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
                    progressDialog.dismiss();
                    SharedPreferences sharedPreferences = getSharedPreferences("cons_limit_pref",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("cons",cons);
                    editor.putInt("cost",cost);
                    int limit =  sharedPreferences.getInt("consLimit", 100);
                    if (cost >limit) {
                        pushNotification("Consumption limit", "you have exceeded thee consumption limit");
                    }


                }
            }catch (Exception e){
                progressDialog.setMessage(e.getMessage());
                progressDialog.setCancelable(true);
                Toast.makeText((Context)MainActivity.this, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder((Context)MainActivity.this);
            dlgAlert.setMessage("Error connecting to the system! \nCheck your connection and try again.");
            dlgAlert.setTitle("Error");
            dlgAlert.setCancelable(false);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

    }

}

