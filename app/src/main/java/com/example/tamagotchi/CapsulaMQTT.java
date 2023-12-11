package com.example.tamagotchi;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.w3c.dom.Text;

public class CapsulaMQTT extends AppCompatActivity {

    String clienteId = "";

    //Conecion al servidor
    static String MQQTHOST = "tcp://tamagotchi-mqtt.cloud.shiftr.io:1883";
    static String MQQTUSER = "tamagotchi-mqtt";
    static String MQTTPASS = "T7xc65sdwfL47kgt";
    static String TOPIC = "LED";
    static String TOPIC_MSG_ON = "Encender";
    static String TOPIC_MSG_OFF = "Apagar";

    boolean permisoPublicar;


    MqttAndroidClient cliente;
    MqttConnectOptions opciones;

    private static final int CODIGO_PERMISOS = 1;

    private String[] permisos = {
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_NETWORK_STATE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capsula_mqtt);

        verificarYRequestPermisos();

        getNombreCliente();
        connectBroker();

        Button btnOn = findViewById(R.id.btnOn);
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensaje(TOPIC, TOPIC_MSG_ON);
            }
        });

        Button btnOff = findViewById(R.id.btnOff);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensaje(TOPIC, TOPIC_MSG_OFF);
            }
        });

    }

    private void checkConnection(){
        if (this.cliente.isConnected()){
            this.permisoPublicar = true;
        }else{
            this.permisoPublicar = false;
            connectBroker();
        }
    }

    private void enviarMensaje(String topic, String msg){

        checkConnection();
        if (this.permisoPublicar){
            try {
                int qos = 0;
                this.cliente.publish(topic, msg.getBytes(), qos, false);
                Toast.makeText(getBaseContext(), topic + " : " + msg, Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void verificarYRequestPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkearPermisos()) {
                ActivityCompat.requestPermissions(this, permisos, CODIGO_PERMISOS);
            }
        }
    }

    private boolean checkearPermisos() {
        for (String permiso : permisos) {
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISOS) {
            if (checkearPermisos()) {
                getNombreCliente();
                connectBroker();
            } else {
                Toast.makeText(this, "Es necesario otorgar todos los permisos para el correcto funcionamiento de la aplicación", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void connectBroker(){
        this.cliente = new MqttAndroidClient(this.getApplicationContext(), MQQTHOST, this.clienteId);
        this.opciones = new MqttConnectOptions();
        this.opciones.setUserName(MQQTUSER);
        this.opciones.setPassword(MQTTPASS.toCharArray());

        try {
            IMqttToken token = this.cliente.connect(opciones);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getBaseContext(), "CONECTADO", Toast.LENGTH_SHORT).show();
                    suscribirseTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getBaseContext(), "CONECCION FALLIDA", Toast.LENGTH_SHORT).show();
                }
            });


        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    private void suscribirseTopic(){
        try {
            this.cliente.subscribe(TOPIC, 0);
        } catch (MqttSecurityException e) {
            throw new RuntimeException(e);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        this.cliente.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getBaseContext(), "Se desconecto el servidor", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                TextView txtInfo = findViewById(R.id.txtInfo);

                if (topic.matches(TOPIC)){
                    String msg = new String(message.getPayload());
                    if (msg.matches(TOPIC_MSG_ON)){
                        txtInfo.setText(msg);
                        txtInfo.setBackgroundColor(GREEN);
                    }
                    if (msg.matches(TOPIC_MSG_OFF)){
                        txtInfo.setText(msg);
                        txtInfo.setBackgroundColor(RED);
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    private void getNombreCliente(){
        String manufacturer = Build.MANUFACTURER;
        String modelName = Build.MODEL;
        this.clienteId = manufacturer + " " + modelName;
        TextView txtIdCliente = findViewById(R.id.txtIdCliente);
        txtIdCliente.setText(this.clienteId);
    }

    public void volver(View view){
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}