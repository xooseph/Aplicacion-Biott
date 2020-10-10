package com.example.biott;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RetryConectionActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    WifiManager wifiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retry_conection);

        requestQueue = Volley.newRequestQueue(this);

        Button button = (Button)findViewById(R.id.bReintentar);
        Bundle bundle = getIntent().getExtras();
        final int netid = bundle.getInt("red");
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String aux = "http://192.168.4.1/?IPAP=1";
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, aux,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Intent intent = new Intent();
                                intent.putExtra("direccionIP", response);
                                setResult(RESULT_OK, intent);
                                finish();

                                // Display the first 500 characters of the response string.
                                //Toast toast = Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG);
                                //toast.show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //textView.setText("That didn't work!")
                    }
                });

                // Add the request to the RequestQueue.
                requestQueue.add(stringRequest);
            }
        });
    }
}
