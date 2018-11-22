package com.example.edgar.tpdm_u3_ejercicio_2_edgar_efren_pozas_bogarin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener fsl;

    private Button ingresar,registrar;
    private EditText correo, contraseña;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();

        correo=findViewById(R.id.correo);
        contraseña=findViewById(R.id.contraseña);
        ingresar=findViewById(R.id.ingresar);
        registrar=findViewById(R.id.registrar);

        fsl=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario=firebaseAuth.getCurrentUser();
                if(usuario!=null){
                    startActivity(new Intent(MainActivity.this,Main2Activity.class));
                    finish();
                }
            }
        };
        ingresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(correo.getText().toString().isEmpty()||contraseña.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Llene todos los campos",Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(correo.getText().toString(),contraseña.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Login exitoso",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,Main2Activity.class));
                        }else{
                            Toast.makeText(MainActivity.this,"Revise los datos",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(correo.getText().toString().isEmpty()||contraseña.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Llene todos los campos",Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(correo.getText().toString(),contraseña.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(MainActivity.this,task.isSuccessful()?"Creado correcto":"No se pudo crear", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(fsl);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(fsl);
    }
}
