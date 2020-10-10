package com.example.biott;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class AlmacenRedesWiFi {
    private static String FICHERO = "redWiFi.txt";
    private Context context;
    private String linea;

    public AlmacenRedesWiFi(Context context){
        this.context = context;
    }

    public void guardaRed(String SSID, String password){
        try{
            try{
                FileInputStream fileInputStream = context.openFileInput(FICHERO);
                BufferedReader bufferedReader = new BufferedReader(new
                        InputStreamReader(fileInputStream));
                linea = bufferedReader.readLine();
                while (linea!=null&&!linea.equals(SSID)){
                    linea = bufferedReader.readLine();
                }
                fileInputStream.close();
            }

            catch (Exception e){
                Log.e("Almacenamiento",e.getMessage(),e);
            }
            if(linea == null) {
                FileOutputStream fileOutputStream =
                        context.openFileOutput(FICHERO, Context.MODE_APPEND);
                String string = SSID + "\n";
                fileOutputStream.write(string.getBytes());
                string = password + "\n";
                fileOutputStream.write(string.getBytes());
                fileOutputStream.close();
            }
        }

        catch (Exception e){
            Log.e("Almacenamiento", e.getMessage(),e);
        }
    }

    public Vector<String> listaRedes(){
        Vector<String> vector = new Vector<String>();

        try {
            FileInputStream fileInputStream = context.openFileInput(FICHERO);
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(fileInputStream));
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                vector.add(linea);
            }
            fileInputStream.close();
        }

        catch (Exception e){
            Log.e("Almacenamiento",e.getMessage(),e);
        }
        return vector;
    }
}
