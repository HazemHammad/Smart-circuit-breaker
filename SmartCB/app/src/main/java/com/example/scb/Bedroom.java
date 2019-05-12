package com.example.scb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class Bedroom extends AppCompatActivity {
    ToggleButton device1,device2,device3,device4;
    TextView d1name,d2name,d3name,d4name;
    private ImageButton mSpeakBtn;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private FloatingActionButton editbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroom);

        device1 = (ToggleButton) findViewById(R.id.device1);
        device2 = (ToggleButton) findViewById(R.id.device2);
        device3 = (ToggleButton) findViewById(R.id.device3);
        device4 = (ToggleButton) findViewById(R.id.device4);
        d1name= findViewById(R.id.D1txt);
        d2name= findViewById(R.id.D2txt);
        d3name= findViewById(R.id.D3txt);
        d4name= findViewById(R.id.D4txt);
        editbtn = findViewById(R.id.floatingActionButton);

        setDevicesnames();

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
        
        device1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /* Switch is device 1 */
                    new Bedroom.Background_get().execute("device1=1");
                } else {
                    new Bedroom.Background_get().execute("device1=0");
                }
            }
        });

        device2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    /* Toggle button is device 2 */
                    new Bedroom.Background_get().execute("device2=1");
                } else {
                    new Bedroom.Background_get().execute("device2=0");
                }
            }
        });

        device3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    /* Toggle button is device 2 */
                    new Bedroom.Background_get().execute("device3=1");
                } else {
                    new Bedroom.Background_get().execute("device3=0");
                }
            }
        });
        device4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    /* Toggle button is device 2 */
                    new Bedroom.Background_get().execute("device4=1");
                } else {
                    new Bedroom.Background_get().execute("device4=0");
                }
            }
        });
        mSpeakBtn = findViewById(R.id.btnspeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();

            }
        });


    }



    protected void onStart() {
        super.onStart();
        new Bedroom.Background_get().execute("DevicesStates");
    }

    private void setDevicesnames() {
        SharedPreferences sharedPreferences = getSharedPreferences("Bedroom_Devices", MODE_PRIVATE);
        d1name.setText(sharedPreferences.getString("Device1","Device 1"));
        d2name.setText(sharedPreferences.getString("Device2","Device 2"));
        d3name.setText(sharedPreferences.getString("Device3","Device 3"));
        d4name.setText(sharedPreferences.getString("Device4","Device 4"));
    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Bedroom.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Bedroom.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText1 = (EditText) promptView.findViewById(R.id.edittext1);
        final EditText editText2 = (EditText) promptView.findViewById(R.id.edittext2);
        final EditText editText3 = (EditText) promptView.findViewById(R.id.edittext3);
        final EditText editText4 = (EditText) promptView.findViewById(R.id.edittext4);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPreferences = getSharedPreferences("Bedroom_Devices", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (editText1.getText().toString().matches("([a-zA-Z1-9]+ *[a-zA-Z1-9]*)+")) editor.putString("Device1", editText1.getText().toString());
                        if (editText2.getText().toString().matches("([a-zA-Z1-9]+ *[a-zA-Z1-9]*)+")) editor.putString("Device2", editText2.getText().toString());
                        if (editText3.getText().toString().matches("([a-zA-Z1-9]+ *[a-zA-Z1-9]*)+")) editor.putString("Device3", editText3.getText().toString());
                        if (editText4.getText().toString().matches("([a-zA-Z1-9]+ *[a-zA-Z1-9]*)+")) editor.putString("Device4", editText4.getText().toString());
                        editor.commit();
                        setDevicesnames();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,10);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(result.get(0).toUpperCase().equals((d1name.getText()+" on").toUpperCase())){
                        new Bedroom.Background_get().execute("device1=1");

                    }
                    else if (result.get(0).toUpperCase().equals((d1name.getText()+" off").toUpperCase())){
                        new Bedroom.Background_get().execute("device1=0");
                    }
                    else if(result.get(0).toUpperCase().equals((d2name.getText()+" on").toUpperCase())){
                        new Bedroom.Background_get().execute("device2=1");

                    }
                    else if (result.get(0).toUpperCase().equals((d2name.getText()+" off").toUpperCase())){
                        new Bedroom.Background_get().execute("device2=0");
                    }
                    else if(result.get(0).toUpperCase().equals((d3name.getText()+" on").toUpperCase())){
                        new Bedroom.Background_get().execute("device3=1");

                    }
                    else if (result.get(0).toUpperCase().equals((d3name.getText()+" off").toUpperCase())){
                        new Bedroom.Background_get().execute("device3=0");
                    }
                    else if(result.get(0).toUpperCase().equals((d4name.getText()+" on").toUpperCase())){
                        new Bedroom.Background_get().execute("device4=1");

                    }
                    else if (result.get(0).toUpperCase().equals((d4name.getText()+" off").toUpperCase())){
                        new Bedroom.Background_get().execute("device4=0");
                    }
                }
                break;
            }

        }
    }

    private class Background_get extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Bedroom.this);
            progressDialog.setTitle("Connecting....");
            progressDialog.setMessage("please wait ...." + "\n" + "Connecting to the system");
            progressDialog.show();
            progressDialog.setCancelable(false);
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
                cancel(true);
            } catch (Exception e) {
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (!isCancelled()) {
                super.onPostExecute(s);
                if (s.contains("device1=1")) {
                    Bedroom.this.device1.setChecked(true);
                } else if (s.contains("device1=0")) {
                    Bedroom.this.device1.setChecked(false);
                }
                if (s.contains("device2=1")) {
                    Bedroom.this.device2.setChecked(true);
                } else if (s.contains("device2=0")) {
                    Bedroom.this.device2.setChecked(false);
                }
                if (s.contains("device3=1")) {
                    Bedroom.this.device3.setChecked(true);
                } else if (s.contains("device3=0")) {
                    Bedroom.this.device3.setChecked(false);
                }
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Bedroom.this);
            dlgAlert.setMessage("Error connecting to the system! \nCheck your connection and try again.");
            dlgAlert.setTitle("Error");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bedroom.this.finish();
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

    }


}