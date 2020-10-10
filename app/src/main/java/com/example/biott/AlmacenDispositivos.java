package com.example.biott;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class AlmacenDispositivos {
    private static String FICHERO = "dispositivosBiott.txt";
    private Context context;
    private String linea;

    public AlmacenDispositivos(Context context){
        this.context = context;
    }

    public void guardaDispositivo(String IPAddress, String MAC, String nombre, String tipo){
        try{
            try {
                FileInputStream fileInputStream = context.openFileInput(FICHERO);
                BufferedReader bufferedReader = new BufferedReader(new
                        InputStreamReader(fileInputStream));
                linea = bufferedReader.readLine();
                while (linea != null && !linea.equals(IPAddress)) {
                    linea = bufferedReader.readLine();
                }
                fileInputStream.close();
            }

            catch (Exception e){
                Log.e("Almacenamiento", e.getMessage(), e);
            }

            if (linea == null){
                FileOutputStream fileOutputStream =
                        context.openFileOutput(FICHERO, Context.MODE_APPEND);
                String string = IPAddress + "\n";
                fileOutputStream.write(string.getBytes());
                string = MAC + "\n";
                fileOutputStream.write(string.getBytes());
                string = nombre + "\n";
                fileOutputStream.write(string.getBytes());
                string = tipo + "\n";
                fileOutputStream.write(string.getBytes());
                fileOutputStream.close();
            }
        }

        catch (Exception e){
            Log.e("Almacenamiento", e.getMessage(), e);
        }
    }

    public void editaDispositivo(String nuevoNombre, int posicion){
        try{
            try {
                FileInputStream fileInputStream = context.openFileInput(FICHERO);
                BufferedReader bufferedReader = new BufferedReader(new
                        InputStreamReader(fileInputStream));
                int index = posicion*4 + 2;
                for(int i = 0; i <= index; i++) {
                    linea = bufferedReader.readLine();
                }
                fileInputStream.close();
            }

            catch (Exception e){
                Log.e("Almacenamiento", e.getMessage(), e);
            }

            FileOutputStream fileOutputStream =
                        context.openFileOutput(FICHERO, Context.MODE_APPEND);
            String string = nuevoNombre;
            fileOutputStream.write(string.getBytes());
            fileOutputStream.close();
        }

        catch (Exception e){
            Log.e("Almacenamiento", e.getMessage(), e);
        }
    }

    public Vector<String> listaDispositivos(){
        Vector<String> vector = new Vector<String>();

        try{
            FileInputStream fileInputStream = context.openFileInput(FICHERO);
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(fileInputStream));
            String linea;
            while ((linea = bufferedReader.readLine()) != null){
                vector.add(linea);
            }
            fileInputStream.close();
        }

        catch (Exception e){
            Log.e("Almacenamiento", e.getMessage(), e);
        }

        return vector;
    }
}
