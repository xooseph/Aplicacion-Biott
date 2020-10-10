package com.example.biott;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FunctionAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    public TextView textName, textType, textTime;
    public ImageView imageType;
    private Function function;

    public FunctionAdapter(Context context){
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Functions.functionVector.removeAll(Functions.functionVector);
        Functions.add(new Function("Olla caliente", FunctionType.HERVIR));
        Functions.add(new Function("Freír", FunctionType.FREIR));
    }

    public View getView(int position, View recycleView, ViewGroup father){
        Function function = Functions.element(position);

        if(recycleView == null){
            recycleView = inflater.inflate(R.layout.functionlayout,null);
        }


        textName = (TextView) recycleView.findViewById(R.id.functionName);
        textName.setText(function.getName());
        textType = (TextView) recycleView.findViewById(R.id.functionType);
        textType.setText(function.getType().getText());
        textTime = (TextView) recycleView.findViewById(R.id.functionTime);
        imageType = (ImageView)recycleView.findViewById(R.id.functionTypeIcon);
        textType = (TextView) recycleView.findViewById(R.id.functionType);

        int id = R.drawable.boil;
            switch (function.getType()){
                case HERVIR:
                    id = R.drawable.boil;
                break;

                case FREIR:
                    id = R.drawable.fry;
                    break;
                    default:
                        break;
            }
        imageType.setImageResource(id);
        imageType.setScaleType(ImageView.ScaleType.FIT_END);
        return recycleView;
    }

    public int getCount(){
        return Functions.size();
    }

    public  Object getItem(int position){
        return Functions.element(position);
    }

    public long getItemId(int position){
        return position;
    }
}
