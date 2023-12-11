package com.example.tamagotchi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

public class Login extends AppCompatActivity {

    TextView createNewAccount;
    TextView pruebaMqtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        createNewAccount=findViewById(R.id.createNewAccount);
        pruebaMqtt=findViewById(R.id.pruebaMqtt);


        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registro.class));
            }


        });

        pruebaMqtt.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){startActivity(new Intent(Login.this, CapsulaMQTT.class));}
        });
    }

    public void Entrar(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}