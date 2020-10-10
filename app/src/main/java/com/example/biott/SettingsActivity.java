package com.example.biott;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    WifiManager wifiManager;
    WifiInfo wifiInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Button bPerfil = (Button)findViewById(R.id.perfilUsuario);
        Button bConexionWiFi = (Button)findViewById(R.id.conexionWiFi);
        Button bIdioma = (Button)findViewById(R.id.idioma);


        bConexionWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, UpdateInfoWiFiActivity.class);
                startActivity(intent);
            }
        });
    }
}
