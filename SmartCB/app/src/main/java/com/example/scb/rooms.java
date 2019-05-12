package com.example.scb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class rooms extends AppCompatActivity {
    TextView back,reception,kitchen,bathroom,bedroom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        back=findViewById(R.id.roomsback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rooms.this.finish();
            }
        });
        reception=findViewById(R.id.livingtxt);
        reception.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(rooms.this, recep.class);
                startActivity(in);
            }
        });
        kitchen=findViewById(R.id.kitchentxt);
        kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(rooms.this,Kitchenroom.class);
                startActivity(in);
            }
        });

        bathroom=findViewById(R.id.bathtxt);
        bathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(rooms.this,Bathroom.class);
                startActivity(in);
            }
        });

        bedroom=findViewById(R.id.bedtxt);
        bedroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(rooms.this,Bedroom.class);
                startActivity(in);
            }
        });

    }
}
