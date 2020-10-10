package com.example.biott;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    public TextView deviceModel,deviceType;
    public ImageView imageType;

    public DeviceAdapter(Context context){
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Devices.deviceVector.removeAll(Devices.deviceVector);
        //Devices.add(new Device("","", "Biott-RCA", DeviceType.INDUCTIONSTOVE));
    }

    public View getView(int position, View recycleView, ViewGroup father){
        Device device = Devices.element(position);

        if(recycleView == null){
            recycleView = inflater.inflate(R.layout.deviceelement,null);
        }

        deviceModel = (TextView) recycleView.findViewById(R.id.deviceModel);
        deviceModel.setText(device.getDeviceModel());
        imageType = (ImageView)recycleView.findViewById(R.id.deviceIcon);
        deviceType = (TextView) recycleView.findViewById(R.id.deviceType);
        deviceType.setText(device.getType().getText());
        int id = R.drawable.induction;
            /*switch (device.getType()){
                case INDUCTIONSTOVE:
                    id = R.drawable.induction;
                break;
            }*/
        imageType.setImageResource(id);
        imageType.setScaleType(ImageView.ScaleType.FIT_END);
        return recycleView;
    }

    public void addDevice(String IP, String MAC, String nombre, String tipo){
        switch (tipo){
            case "Parrilla":
                Devices.add(new Device(IP,MAC, nombre, DeviceType.INDUCTIONSTOVE));
                break;
                default:
            break;
        }
    }

    public int getCount(){
        return Devices.size();
    }

    public  Object getItem(int position){
        return Devices.element(position);
    }

    public long getItemId(int position){
        return position;
    }
}
