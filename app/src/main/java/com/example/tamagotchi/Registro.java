package com.example.tamagotchi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Registro extends AppCompatActivity {

    //declaramos variables
    private EditText inputEmail, inputPassword, inputNameMascota, inputName;
    private ListView viewlista;

    //variable conexion firestore
    private FirebaseFirestore db;

    private TextView yaTienesCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewlista = findViewById(R.id.viewlista);

        CargarListaFirestore();
        //inicio firestore.
        db = FirebaseFirestore.getInstance();

        //unir las variables
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputNameMascota = findViewById(R.id.inputNameMascota);
        inputName = findViewById(R.id.inputName);

        // volver al login
        yaTienesCuenta=findViewById(R.id.yaTienesCuenta);
        yaTienesCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registro.this, Login.class));
            }
        });
    }

    //Metodo envioar datos
    public void Registrarse(View view){
        String Email = inputEmail.getText().toString();
        String Password = inputPassword.getText().toString();
        String NameMascota = inputNameMascota.getText().toString();
        String Name = inputName.getText().toString();

        if (Email.isEmpty() || Password.isEmpty() || NameMascota.isEmpty() || Name.isEmpty()) {
            Toast.makeText(Registro.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        //Mapeamos
        Map<String, Object> tamagotchi = new HashMap<>();
        tamagotchi.put("Email", Email);
        tamagotchi.put("Password", Password);
        tamagotchi.put("NameMascota", NameMascota);
        tamagotchi.put("Name", Name);

        //enviar datos al firestore
        db.collection("Tamagotchi")
                .document(Email)
                .set(tamagotchi)
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(Registro.this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        inputEmail.setText("");
        inputPassword.setText("");
        inputNameMascota.setText("");
        inputName.setText("");
    }
    //boton cargar lista
    public void CargarLista(View view){
        CargarListaFirestore();
    }

    //cargar lista.
    public void CargarListaFirestore(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Tamagotchi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<String> listaTamagotchi = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()){
                                String linea = "Email: " + document.getString("Email") + " || Nombre Mascota: " +
                                        //document.getString("Password") + " || " +
                                        document.getString("NameMascota") + " || Dueño: " +
                                        document.getString("Name");
                                listaTamagotchi.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(Registro.this,
                                    android.R.layout.simple_list_item_1, listaTamagotchi);
                            viewlista.setAdapter(adaptador);
                            Toast.makeText(Registro.this, "Lista cargada con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("TAG", "Error para obtener datos de Firestore", task.getException());
                        }
                    }
                });
    }
}