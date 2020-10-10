package com.example.biott;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Vector;

public class UpdateInfoWiFiActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private String redSSID;
    private AlmacenRedesWiFi almacenRedesWiFi;
    private TextView tRedWiFi;
    private TextView tContraseniaWiFi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_info_wifi);

        almacenRedesWiFi = new AlmacenRedesWiFi(this);

        Button bActualizarWiFi = (Button) findViewById(R.id.bAddWiFi);
        bActualizarWiFi.setEnabled(false);
        tRedWiFi = (TextView) findViewById(R.id.tSSID);
        //tContraseniaWiFi = (TextView) findViewById(R.id.tPassword);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Vector<String> vector = almacenRedesWiFi.listaRedes();
        boolean i = true;
        for (String string : vector) {
            if (i) tRedWiFi.append(string + '\n');
            //else tContraseniaWiFi.append(string + '\n');
            i = !i;
        }

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            bActualizarWiFi.setEnabled(true);

            bActualizarWiFi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiInfo = wifiManager.getConnectionInfo();
                    redSSID = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);

                    Intent intent = new Intent(UpdateInfoWiFiActivity.this, AccesoAPActivity.class);
                    intent.putExtra("red", redSSID);
                    startActivityForResult(intent, 1234);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1234&&resultCode==RESULT_OK){
            String password = data.getExtras().getString("resultado");

            if(!password.equals("")){
                almacenRedesWiFi.guardaRed(redSSID, password);
                Vector<String> vector = almacenRedesWiFi.listaRedes();
                tRedWiFi.setText("");
                //tContraseniaWiFi.setText("");
                boolean i = true;
                for(String string : vector){
                    if(i) tRedWiFi.append(string + '\n');
                    //else  tContraseniaWiFi.append(string + '\n');
                    i = !i;
                }
            }
        }
    }
}