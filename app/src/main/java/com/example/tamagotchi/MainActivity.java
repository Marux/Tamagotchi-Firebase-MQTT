package com.example.tamagotchi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import android.location.Location;
import com.google.android.gms.tasks.OnSuccessListener;
import androidx.core.app.ActivityCompat;

import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;





    public class MainActivity extends AppCompatActivity implements TamagotchiListener, SensorEventListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private SensorManager sensorManager;
    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private static final int PHYISCAL_ACTIVITY = 101; // Puedes elegir cualquier valor
    private EditText txtLatitud, txtLongitud;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Tamagotchi tamagotchi = new Tamagotchi("Traumagotchi");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tamagotchi.addObserver(this); // Registra MainActivity como Observador


        // Inicializar el cliente de ubicación fusionada
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Verificar y solicitar permisos
        checkAndRequestPermission();

        loadData();
        resetSteps();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // maps
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Obtén el AutoCompleteTextView desde el diseño
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        // Define un ArrayAdapter utilizando el recurso string-array y un diseño de lista predeterminado
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.menu, android.R.layout.simple_dropdown_item_1line);

        // Aplica el adaptador al AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter);
        setupAutoCompleteTextView();


    }


    private void checkAndRequestPermission() {
        int activityRecognitionPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (activityRecognitionPermission == PackageManager.PERMISSION_DENIED ||
                fineLocationPermission == PackageManager.PERMISSION_DENIED ||
                coarseLocationPermission == PackageManager.PERMISSION_DENIED) {

            // Solicitar permisos
            String[] permissions = {
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            requestPermissions(permissions, PHYISCAL_ACTIVITY);
        }
        // Si los permisos ya funcionan, no será necesario volver a solicitarlos.
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHYISCAL_ACTIVITY) {
            // Verificar si el usuario otorgó el permiso
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado
                // Puedes realizar acciones relacionadas con el permiso aquí
            } else {
                // Permiso denegado
                // Puedes tomar acciones específicas cuando el permiso es denegado
                Toast.makeText(this, "Permiso denegado, algunas funcionalidades pueden no estar disponibles", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null) {
            Toast.makeText(this, "Sensor no detectado en este dispositivo.", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "STEP_COUNTER no detectado");
        } else {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView tv_stepsTaken = findViewById(R.id.tv_stepsTaken);

        if (running) {
            totalSteps = event.values[0];

            int currentSteps = (int) (totalSteps - previousTotalSteps);

            tv_stepsTaken.setText(String.valueOf(currentSteps));
        }
    }

    public void resetSteps() {
        TextView tv_stepsTaken = findViewById(R.id.tv_stepsTaken);

        tv_stepsTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Toca prolongadamente para reiniciar los pasos.", Toast.LENGTH_SHORT).show();
            }
        });

        tv_stepsTaken.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previousTotalSteps = totalSteps;
                tv_stepsTaken.setText("0");
                saveData();
                return true;
            }
        });
    }

    private void saveData() {
        Context context = getApplicationContext();
        // Shared Preferences will allow us to save and retrieve data in the form of key,value pair.
        // In this function we will save data
        SharedPreferences sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("key1", previousTotalSteps);
        editor.apply();
    }

    private void loadData() {
        Context context = getApplicationContext();
        // In this function we will retrieve data
        SharedPreferences sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key1", 0f);

        // Log.d is used for debugging purposes
        Log.d("MainActivity", String.valueOf(savedNumber));

        previousTotalSteps = savedNumber;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // We do not have to write anything in this function for this app
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        // Obtener la posición actual y centrar el mapa en esa ubicación
        obtenerPosicionActual();
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText(String.valueOf(latLng.latitude));
        txtLongitud.setText(String.valueOf(latLng.longitude));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText(String.valueOf(latLng.latitude));
        txtLongitud.setText(String.valueOf(latLng.longitude));
    }

    private void obtenerPosicionActual() {
        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la última ubicación conocida
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Crear un objeto LatLng con la ubicación actual
                                LatLng ubicacionActual = new LatLng(location.getLatitude(), location.getLongitude());

                                // Añadir marcador y mover la cámara
                                mMap.addMarker(new MarkerOptions().position(ubicacionActual).title("Ubicación Actual"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionActual));
                            }
                        }
                    });
        } else {
            // Si no hay permisos, solicitarlos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    @Override
    public void onTamagotchiAction(String action) {
        runOnUiThread(() -> {
            // Actualizar el TextView con la acción recibida
            TextView textView = findViewById(R.id.mensaje);
            textView.setText("Estado actual del Tamagotchi: \n" + action);
        });

    }

        @Override
        public void addObserver(TamagotchiListener observer) {

        }

        private void setupAutoCompleteTextView() {

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAction = (String) parent.getItemAtPosition(position);
            tamagotchi.onTamagotchiAction(selectedAction);
            handleSelectedAction(selectedAction);
        });
    }

    private void handleSelectedAction(String selectedAction) {
        // Realiza acciones específicas según la acción seleccionada
        switch (selectedAction) {
            case "MostrarEstado":
                gifEstado();
                break;
            case "Alimentar":
                gifAlimentar();
                break;
            case "Jugar":
                gifJugar();
                break;
            case "PasarTiempo":
                gifPasarTiempo();
                break;
            case "MandarloChambear":
                gifTrabajar();
                break;
            case "Bailar":
                gifBailar();
                break;
            case "Salir":
                Salir();
            default:
        }
    }

    public void gifEstado() {
        // Obtener el ImageView
        ImageView gifImageView = findViewById(R.id.gifImageView1);
        Glide.with(this)
                .asGif()
                .load(R.drawable.mostrarstado)  // Reemplaza "tu_gif" con el nombre de tu archivo GIF en la carpeta "res/drawable"
                .into(gifImageView);
        // Hacer visible el ImageView
        gifImageView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            gifImageView.setVisibility(View.INVISIBLE);
        }, 5000);

    }
    public void gifAlimentar() {
        // Obtener el ImageView
        ImageView gifImageView = findViewById(R.id.gifImageView1);
        Glide.with(this)
                .asGif()
                .load(R.drawable.comiendo)  // Reemplaza "tu_gif" con el nombre de tu archivo GIF en la carpeta "res/drawable"
                .into(gifImageView);
        // Hacer visible el ImageView
        gifImageView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            gifImageView.setVisibility(View.INVISIBLE);
        }, 5000);

    }
    public void gifJugar() {
        // Obtener el ImageView
        ImageView gifImageView = findViewById(R.id.gifImageView1);
        Glide.with(this)
                .asGif()
                .load(R.drawable.jugar)  // Reemplaza "tu_gif" con el nombre de tu archivo GIF en la carpeta "res/drawable"
                .into(gifImageView);
        // Hacer visible el ImageView
        gifImageView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            gifImageView.setVisibility(View.INVISIBLE);
        }, 5000);

    }
    public void gifPasarTiempo() {
        // Obtener el ImageView
        ImageView gifImageView = findViewById(R.id.gifImageView1);
        Glide.with(this)
                .asGif()
                .load(R.drawable.pasartiempo)  // Reemplaza "tu_gif" con el nombre de tu archivo GIF en la carpeta "res/drawable"
                .into(gifImageView);
        // Hacer visible el ImageView
        gifImageView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            gifImageView.setVisibility(View.INVISIBLE);
        }, 5000);

    }
    public void gifTrabajar() {
        // Obtener el ImageView
        ImageView gifImageView = findViewById(R.id.gifImageView1);
        Glide.with(this)
                .asGif()
                .load(R.drawable.trabajar)  // Reemplaza "tu_gif" con el nombre de tu archivo GIF en la carpeta "res/drawable"
                .into(gifImageView);
        // Hacer visible el ImageView
        gifImageView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            gifImageView.setVisibility(View.INVISIBLE);
        }, 5000);
    }
    public void gifBailar() {
        // Obtener el ImageView
        ImageView gifImageView = findViewById(R.id.gifImageView1);
        Glide.with(this)
                .asGif()
                .load(R.drawable.bailar)  // Reemplaza "tu_gif" con el nombre de tu archivo GIF en la carpeta "res/drawable"
                .into(gifImageView);
        // Hacer visible el ImageView
        gifImageView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            gifImageView.setVisibility(View.INVISIBLE);
        }, 5000);

    }

    private void Salir(){
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }
}

