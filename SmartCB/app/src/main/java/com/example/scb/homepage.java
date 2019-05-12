package com.example.scb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class homepage extends AppCompatActivity {
    Button insbtn ,roombtn,consbtn;
    TextView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        insbtn = findViewById(R.id.button);
        insbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent in = new Intent(homepage.this,Instructions.class);
                startActivity(in);
            }
        });
        roombtn = findViewById(R.id.button2);
        roombtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent in = new Intent(homepage.this,rooms.class);
                startActivity(in);
            }
        });
        back = findViewById(R.id.instructionsback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homepage.this.finish();
            }
        });

        consbtn = findViewById(R.id.consbtn);
        consbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(homepage.this, Consum.class);
                startActivity(in);
            }
        });
    }
}
