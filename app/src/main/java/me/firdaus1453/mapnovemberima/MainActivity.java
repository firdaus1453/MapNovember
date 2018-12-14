package me.firdaus1453.mapnovemberima;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnMap, btnPlacePicker, btnRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisiasi widget ke java
        btnMap = findViewById(R.id.btn_map);
        btnPlacePicker = findViewById(R.id.btn_place_picker);
        btnRoute = findViewById(R.id.btn_route);

        // Membuat onclick
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Berpindah activity menuju MapsActivity
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

        btnPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Berpindah menuju PlacePicker
                startActivity(new Intent(getApplicationContext(), PlacePickerActivity.class));

            }
        });

        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RouteActivity.class));
            }
        });
    }
}
