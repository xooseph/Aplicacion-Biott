package com.example.biott;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ControlActivity extends AppCompatActivity {
    private String web_server;
    private RequestQueue requestQueue;
    private long time = 0;
    private ToggleButton tOnOff;
    private ToggleButton tBloqueo;
    private Switch swTempo;
    private TextView tTempo;
    private LinearLayout linTempo;
    private LinearLayout linTemp;
    private LinearLayout linFunciones;
    private String temp;
    private boolean onOff = false;
    private String html;
    private ScheduledExecutorService scheduledExecutorService;
    private String estado;
    public TextView textName, textType, textTime;
    public ImageView imageType;
    private Button bMenos;
    private Button bMas;
    public Button sigFuncion;
    public int CONTADOR_FUNCION = 0;
    private Button actualizaTemp;
    private TextView temperatura;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_main);

        Functions.add(new Function("Olla caliente", FunctionType.HERVIR));
        Functions.add(new Function("Olla caliente", FunctionType.HERVIR));
        Functions.add(new Function("Freír", FunctionType.FREIR));
        Functions.add(new Function("Mantener Caliente", FunctionType.HEAT));
        Functions.add(new Function("Sopa", FunctionType.SOPA));
        Functions.add(new Function("Agua", FunctionType.AGUA));
        Functions.add(new Function("Leche", FunctionType.LECHE));

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        tOnOff = (ToggleButton)findViewById(R.id.onButton);
        bMas = (Button)findViewById(R.id.bMas);
        bMenos = (Button)findViewById(R.id.bMenos);
        tTempo = (TextView)findViewById(R.id.tTempo);
        swTempo = (Switch)findViewById(R.id.swTemp);
        tBloqueo = (ToggleButton)findViewById(R.id.tBloqueo);
        linTempo = (LinearLayout)findViewById(R.id.linTempo);
        linTemp = (LinearLayout)findViewById(R.id.linTemp);
        linFunciones = (LinearLayout)findViewById(R.id.linFunciones);
        Bundle bundle = getIntent().getExtras();
        web_server = bundle.getString("IP");
        textName = (TextView) findViewById(R.id.functionName);
        textName.setText(Functions.element(CONTADOR_FUNCION).getName());
        textType = (TextView) findViewById(R.id.functionType);
        textType.setText(Functions.element(CONTADOR_FUNCION).getType().getText());
        imageType = (ImageView)findViewById(R.id.functionTypeIcon);
        textType = (TextView) findViewById(R.id.functionType);
        sigFuncion = (Button)findViewById(R.id.sigFuncion);
        int id = R.drawable.boil;
        imageType.setImageResource(id);
        imageType.setScaleType(ImageView.ScaleType.FIT_END);
        temperatura = (TextView)findViewById(R.id.temperatura);
        actualizaTemp = (Button)findViewById(R.id.actualizarTemp);

        bloquearActividad(false,false,false,false);
        tBloqueo.setChecked(true);

        actualizaTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        HttpClient httpClient = new DefaultHttpClient();

                        try {
                            HttpResponse httpResponse = httpClient.execute(new HttpGet(
                                    "http://" + web_server + "/?status=1"));
                            InputStream inputStream = httpResponse.getEntity().getContent();
                            BufferedReader bufferedReader = new BufferedReader(
                                    new InputStreamReader(inputStream));
                            StringBuilder stringBuilder = new StringBuilder();
                            String linea = null;

                            while ((linea = bufferedReader.readLine()) != null) {
                                stringBuilder.append(linea);
                            }
                            inputStream.close();

                            html = stringBuilder.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        int i = 0;

                        while (!html.substring(i, i + 6).equals("<BODY>")) {
                            i++;
                        }

                        int j = i + 6;
                        while (!html.substring(j, j + 7).equals("</BODY>")) {
                            j++;
                        }

                        estado = html.substring(i + 6, j);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                temperatura.setText("Temperatura: " + estado.substring(9) + " °C");
                            }
                        });
                    }
                }.execute();
            }
        });

        bMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int aux1;
                String temp="";

                if (time==3540000)
                    bMas.setEnabled(true);

                if (time>0)
                    time  -= 60000;

                if (time==0)
                    bMenos.setEnabled(false);

                aux1 = (int) (time/60000);
                if(aux1<10)
                    temp="0";
                tTempo.setText(temp + Integer.toString(aux1) + ":" + Integer.toString((int) (time%60000)) + "0");

                solicitudCliente("http://" + web_server + "/?Boton=3");
            }
        });

        bMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int aux1;
                String temp="";

                if(time>=0)
                    bMenos.setEnabled(true);

                if(time<3540000)
                    time  += 60000;

                if(time==3540000)
                    bMas.setEnabled(false);

                aux1 = (int) (time/60000);
                if(aux1<10)
                    temp="0";
                tTempo.setText(temp + Integer.toString(aux1) + ":" + Integer.toString((int) (time%60000)) + "0");

                solicitudCliente("http://" + web_server + "/?Boton=4");
            }
        });

        sigFuncion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitudCliente("http://" + web_server + "/?Boton=5");

                CONTADOR_FUNCION =
                        CONTADOR_FUNCION < 6 ? CONTADOR_FUNCION + 1 : 1;
                textName.setText(Functions.element(CONTADOR_FUNCION).getName());
                textType.setText(Functions.element(CONTADOR_FUNCION).getType().getText());
                int id = R.drawable.boil;
                switch (Functions.element(CONTADOR_FUNCION).getType()){
                    case HERVIR:
                        id = R.drawable.boil;
                        break;
                    case FREIR:
                        id = R.drawable.fry;
                        break;
                    case HEAT:
                        id = R.drawable.heat;
                        break;
                    case SOPA:
                        id = R.drawable.sopa;
                        break;
                    case AGUA:
                        id = R.drawable.agua;
                        break;
                    case LECHE:
                        id = R.drawable.leche;
                        break;
                    default:
                        break;
                }

                imageType.setImageResource(id);
                imageType.setScaleType(ImageView.ScaleType.FIT_END);

            }
        });

        /*scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                respuestaCliente();
            }
        }, 0, 4, TimeUnit.SECONDS);*/
    }

    public void clickON_OFF(View view){
        //Se comenta
        onOff = !onOff;

        if(onOff){
            tBloqueo.setChecked(false);
            tBloqueo.setEnabled(true);
            bloquearActividad(true,true,true,true);
        }

        else{
            swTempo.setChecked(false);
            tTempo.setText("00:00");
            time = 0;
            tBloqueo.setChecked(true);
            tBloqueo.setEnabled(false);
            CONTADOR_FUNCION = 0;
            textName.setText(Functions.element(CONTADOR_FUNCION).getName());
            textType.setText(Functions.element(CONTADOR_FUNCION).getType().getText());
            int id = R.drawable.boil;
            imageType.setImageResource(id);
            imageType.setScaleType(ImageView.ScaleType.FIT_END);
            temperatura.setText("Temperatura: ");
            bloquearActividad(false,false,false,false);
        }

        solicitudCliente("http://" + web_server + "/?Boton=1");
    }

    public void switchTemp(View view){
        //Se comenta
        if(swTempo.isChecked()){
            for(int i=0; i<linTemp.getChildCount();i++){
                View view1 = linTemp.getChildAt(i);
                view1.setEnabled(true);
            }
        }

        else{
            for(int i=0; i<linTemp.getChildCount();i++){
                View view1 = linTemp.getChildAt(i);
                view1.setEnabled(false);
            }
        }

        solicitudCliente("http://" + web_server + "/?Boton=2");
    }

    public void clickBloqueo(View view){
        temp="0";

        if(!tBloqueo.isChecked()){
            temp="1";
        }

        //Se comenta
        else{
            bloquearActividad(false, false,false,false);
            tBloqueo.setEnabled(true);
        }

        solicitudCliente("http://" + web_server + "/?Boton=6" + temp);



        //Se comenta
        if(temp=="1") {
            try{
                Thread.sleep(4000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            bloquearActividad(true,true,true, true);
        }
    }

    public void bloquearActividad(boolean lTempo, boolean lFunciones, boolean lTempe, boolean lock){
        for(int i=0; i<linTempo.getChildCount();i++){
            View view1 = linTempo.getChildAt(i);
            view1.setEnabled(lTempo);
        }

        for(int i=0; i<linTemp.getChildCount();i++){
            View view1 = linTemp.getChildAt(i);
            view1.setEnabled(lTempo);
        }

        tTempo.setEnabled(lTempo);

        for(int i=0; i<linFunciones.getChildCount();i++){
            View view1 = linFunciones.getChildAt(i);
            view1.setEnabled(lFunciones);
        }

        temperatura.setEnabled(lTempe);

        actualizaTemp.setEnabled(lTempe);

        tBloqueo.setEnabled(lock);
    }

    public void solicitudCliente(final String url){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                try {
                    httpClient.execute(httpGet);
                }

                catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

                .execute();
    }

    public void respuestaCliente(){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                HttpClient httpClient = new DefaultHttpClient();

                try {
                    HttpResponse httpResponse = httpClient.execute(new HttpGet(
                            "http://" + web_server + "/?status=1"));
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String linea = null;

                    while((linea = bufferedReader.readLine()) != null){
                        stringBuilder.append(linea);
                    }
                    inputStream.close();

                    html = stringBuilder.toString();
                }

                catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                try{
                    int i = 0;

                    while(!html.substring(i, i+6).equals("<BODY>")){
                        i++;
                    }

                    int j = i+6;
                    while(!html.substring(j, j+7).equals("</BODY>")){
                        j++;
                    }

                    estado = html.substring(i+6, j);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Led ON_OFF
                            if(estado.substring(0,1).equals("0")){
                                tOnOff.setChecked(false);
                                bloquearActividad(false,false,false,false);
                            }

                            else{
                                tOnOff.setChecked(true);
                                bloquearActividad(true,true,true, true);
                            }

                            //Led Temporizador
                            if(estado.substring(1,2).equals("0")){
                                swTempo.setChecked(false);
                            }

                            else{
                                swTempo.setChecked(true);
                            }

                            //Led Bloqueo
                            if(estado.substring(2,3).equals("0")){
                                bloquearActividad(true,true,true,true);
                                tBloqueo.setChecked(true);
                            }

                            else{
                                bloquearActividad(false,false,false,true);
                                tBloqueo.setChecked(false);
                            }

                            //Led Olla Caliente
                            if(estado.substring(3,4).equals("1"))
                                CONTADOR_FUNCION = 1;

                                //Led Freír
                            else if(estado.substring(4,5).equals("1"))
                                CONTADOR_FUNCION = 2;

                                //Led Mantener Caliente
                            else if(estado.substring(5,6).equals("1"))
                                CONTADOR_FUNCION = 3;

                                //Led Sopa
                            else if(estado.substring(6,7).equals("1"))
                                CONTADOR_FUNCION = 4;

                                //Agua
                            else if(estado.substring(7,8).equals("1"))
                                CONTADOR_FUNCION = 5;

                                //Leche
                            else if(estado.substring(8,9).equals("1"))
                                CONTADOR_FUNCION = 6;

                            else
                                CONTADOR_FUNCION = 0;
                        }
                    });
                }

                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
                .execute();
    }
}
