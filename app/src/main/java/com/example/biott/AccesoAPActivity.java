package com.example.biott;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AccesoAPActivity extends AppCompatActivity {
    private TextView textView;
    private EditText editText;
    private Button bAceptar;
    private Button bCancelar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceso_ap);

        textView = (TextView)findViewById(R.id.red);
        editText = (EditText)findViewById(R.id.password);
        bAceptar = (Button)findViewById(R.id.bAceptar);
        bCancelar = (Button)findViewById(R.id.bCancelar);

        Bundle bundle = getIntent().getExtras();

        textView.setText(bundle.getString("red"));


        bAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("resultado", editText.getText().toString());
                intent.putExtra("resultadoSSID", textView.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        bCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

}
