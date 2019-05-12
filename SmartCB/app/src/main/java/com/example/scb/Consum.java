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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consum extends AppCompatActivity {
    Button limitbtn;
    EditText limittxt;
    TextView consview,costview;
    int cons,cost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consum);
        consview = findViewById(R.id.consview);
        costview = findViewById(R.id.costview);
        limittxt = findViewById(R.id.limittxt);
        limitbtn = findViewById(R.id.limitbtn);

        limitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {

                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("cons_limit_pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("consLimit", Integer.parseInt(limittxt.getText().toString()));
                    editor.commit();

                }catch (Exception e){
                    Toast.makeText((Context) Consum.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
         });


    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("cons_limit_pref", MODE_PRIVATE);
            limittxt.setText(sharedPreferences.getInt("consLimit", 100) + "");
            consview.setText(sharedPreferences.getInt("cons", 0) + "");
            costview.setText(sharedPreferences.getInt("cost", 0) + "");
            new Consum.Background_get().execute("getCons");
        }catch (Exception e){
            Toast.makeText((Context) Consum.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private class Background_get extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog((Context)Consum.this);
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
                    Toast.makeText(Consum.this,s,Toast.LENGTH_LONG).show();
                    Pattern p = Pattern.compile("(?<=cons=)\\d+");
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                       consview.setText(m.group()+"");
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
                        costview.setText(cost + "");
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
                Toast.makeText((Context)Consum.this, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder((Context)Consum.this);
            dlgAlert.setMessage("Error connecting to the system! \nCheck your connection and try again.");
            dlgAlert.setTitle("Error");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Consum.this.finish();
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

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
            NotificationCompat.Builder NB = new NotificationCompat.Builder((Context)Consum.this, "Circuit Breaker");
            NB.setSmallIcon(R.drawable.ic_launcher_background);
            NB.setContentTitle(title);
            NB.setContentText(text);
            NB.setContentIntent(pendingIntent);
            NB.setAutoCancel(true);
            NB.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(1, NB.build());
        }
    }


}
