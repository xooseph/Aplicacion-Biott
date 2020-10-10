package com.example.biott;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, PopupMenu.OnMenuItemClickListener {

    private ImageButton imageButton;
    public DeviceAdapter adapter;
    private AlmacenDispositivos almacenDispositivos;
    private int  selectedListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }

        almacenDispositivos = new AlmacenDispositivos(this);
        adapter = new DeviceAdapter(this);

        Vector<String> vectorDispositivos = almacenDispositivos.listaDispositivos();
        ListView listView = (ListView)findViewById(R.id.deviceList);


        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        int tam = vectorDispositivos.size();
        if (tam>0){
            for(int i=0; i<tam; i+=4){
                adapter.addDevice(vectorDispositivos.elementAt(i),vectorDispositivos.elementAt(i+1),
                        vectorDispositivos.elementAt(i+2),vectorDispositivos.elementAt(i+3));
            }
        }
        listView.setOnItemClickListener(this);


        imageButton = (ImageButton)findViewById(R.id.menubutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.main_menu);
                popup.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        /*Vector<String> vectorDispositivos = almacenDispositivos.listaDispositivos();

        for(int i=0; i<Devices.size(); i++){
            Devices.erase(Devices.size()-1);
        }

        int tam = vectorDispositivos.size();
        if (tam>0){
            for(int i=0; i<tam; i+=4){
                adapter.addDevice(vectorDispositivos.elementAt(i),vectorDispositivos.elementAt(i+1),
                        vectorDispositivos.elementAt(i+2),vectorDispositivos.elementAt(i+3));
            }
        }

        adapter.notifyDataSetChanged();*/
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addDevice:
                Intent intent = new Intent(this,SearchDevicesActivity.class);
                startActivity(intent);
                
                return true;
            case R.id.Config:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Vector<String> vectorDispositivos = almacenDispositivos.listaDispositivos();
        Intent intent = new Intent(this,ControlActivity.class);
        intent.putExtra("IP", vectorDispositivos.elementAt(i*3+i));
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        if (view.getId()==R.id.deviceList){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedListId = info.position;
            menu.setHeaderTitle("Opciones");
            String [] menuItems = {"Cambiar nombre", "Eliminar"};
            for(int i = 0; i < menuItems.length; i++){
                menu.add(Menu.NONE, i, i, menuItems [i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;

        switch (index){
            case 0:
                almacenDispositivos.editaDispositivo("Master Chief", selectedListId);
                break;

            default:
                break;
        }
        return true;
    }
}

