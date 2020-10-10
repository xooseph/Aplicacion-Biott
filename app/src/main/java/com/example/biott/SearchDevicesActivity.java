package com.example.biott;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.Vector;

public class SearchDevicesActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {
    WifiManager wifiManager;
    WifiInfo wiFiInfo;
    private String SSID;
    private WiFiAdapter adapter;
    private String redSSID;
    private String IPAddress;
    private String passwordSSID;
    private String web_service = "http://192.168.4.1";
    private AlmacenRedesWiFi almacenRedesWiFi;
    private AlmacenDispositivos almacenDispositivos;
    private RequestQueue requestQueue;
    private String html;
    private int netid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchdevices);
        almacenRedesWiFi = new AlmacenRedesWiFi(this);
        almacenDispositivos = new AlmacenDispositivos(this);
        escanearDispositivos();
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data){
        if(requestcode==1234&&resultcode==RESULT_OK){
            String password = data.getExtras().getString("resultado");
            SSID = data.getExtras().getString("resultadoSSID");
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = String.format("\"%s\"",SSID);
            wifiConfiguration.preSharedKey = String.format("\"%s\"", password);
            netid = wifiManager.addNetwork(wifiConfiguration);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netid,true);
            wifiManager.reconnect();

            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            while(!connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());

            Vector<String> vector = almacenRedesWiFi.listaRedes();
            int i = 0;
            passwordSSID = "";
            redSSID = redSSID.substring(1, redSSID.length()-1);

            while(i < vector.size()){
                if(vector.elementAt(i).equals(redSSID)){
                    passwordSSID = vector.elementAt(i+1);
                    break;
                }
                i++;
            }

            requestQueue = Volley.newRequestQueue(this);

            String aux = web_service + "/?SSID=" + redSSID + "&password=" + passwordSSID;
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, aux,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    wifiManager.enableNetwork(netid, true);
                    wifiManager.reconnect();

                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    while(!connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());

                    Intent intent = new Intent(getApplicationContext(), RetryConectionActivity.class);
                    intent.putExtra("red", netid);
                    startActivityForResult(intent, 5678);

                }
            });

            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);


        }

        else if(requestcode==5678&&resultcode==RESULT_OK){
            almacenDispositivos.guardaDispositivo(data.getExtras().getString("direccionIP"),
                    "", "Biott Parrilla Serie 4", "Parrilla");

            wifiManager.removeNetwork(netid);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.update_devices_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.actualizar){
            escanearDispositivos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void escanearDispositivos(){
        adapter = new WiFiAdapter(this);
        wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wiFiInfo = wifiManager.getConnectionInfo();
        redSSID = wiFiInfo.getSSID();
        int ip = wiFiInfo.getIpAddress();
        IPAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        wifiManager.startScan();


        List<ScanResult> wifilist = wifiManager.getScanResults();

        final ListView listView = (ListView)findViewById(R.id.foundDevicesList);

        String clave = "biott";
        for ( int i = 0; i < wifilist.size(); i++) {
            String red;

            if(wifilist.get(i).SSID.length()>4){

                red = wifilist.get(i).SSID.substring(0,5);

                if (clave.equals(red)) {
                    SSID = wifilist.get(i).SSID;

                    listView.setAdapter(adapter);

                    WiFiDevices.add(new Device("","", SSID, DeviceType.INDUCTIONSTOVE));

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(SearchDevicesActivity.this,AccesoAPActivity.class);

                            intent.putExtra("red", WiFiDevices.element(position).getDeviceModel());
                            startActivityForResult(intent,1234);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}
