package com.example.biott;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WiFiAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    public TextView deviceModel,deviceType;
    public ImageView imageType;

    public WiFiAdapter(Context context){
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        WiFiDevices.deviceVector.removeAll(WiFiDevices.deviceVector);
    }

    public View getView(int position, View recycleView, ViewGroup father){
        Device device = WiFiDevices.element(position);

        if(recycleView == null){
            recycleView = inflater.inflate(R.layout.deviceelement,null);
        }

        deviceModel = (TextView) recycleView.findViewById(R.id.deviceModel);
        switch (device.getDeviceModel().substring(9)){
            case "Parrilla":
                deviceModel.setText("Biott Parrilla Serie 4");
                break;
            default:
                break;
        }
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

    public int getCount(){
        return WiFiDevices.size();
    }

    public  Object getItem(int position){
        return WiFiDevices.element(position);
    }

    public long getItemId(int position){
        return position;
    }
}
