package com.example.edgar.tpdm_u3_ejercicio_2_edgar_efren_pozas_bogarin;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.Inflater;

public class Main2Activity extends AppCompatActivity {

    private ListView ls;
    private ArrayList<Articulos> articulos;
    private Articulos articulo_sel;
    private EditText nombre,precio;

    private DatabaseReference db;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        auth=FirebaseAuth.getInstance();

        db=FirebaseDatabase.getInstance().getReference();
        ls=findViewById(R.id.lista);

        articulo_sel=null;

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                articulo_sel=articulos.get(position);
                mostrar_mensaje();
            }
        });
        db.child("articulos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0){
                    mensaje("No hay articulos");
                    return;
                }
                articulos=new ArrayList<>();
                for (final DataSnapshot ds:dataSnapshot.getChildren()){
                    Articulos ar=ds.getValue(Articulos.class);
                    if(ar==null)
                        continue;
                    articulos.add(ar);
                }
                actualizar_lista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R. id.agregar){
            articulo_sel=null;
            mostrar_mensaje();
        }else if(item.getItemId()==R.id.cerrar){
            auth.signOut();
            mensaje("Sesión cerrada");
            startActivity(new Intent(Main2Activity.this,MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private void mostrar_mensaje(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle(articulo_sel!=null?"Modificar":"Agregar");
        View v=getLayoutInflater().inflate(R.layout.emergente,null);
        nombre=v.findViewById(R.id.nombre);
        precio=v.findViewById(R.id.precio);
        if(articulo_sel!=null){
            nombre.setText(articulo_sel.getNombre());
            precio.setText(articulo_sel.getPrecio());
        }
        alert.setView(v);
        alert.setPositiveButton(articulo_sel!=null?"Actualizar":"Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(nombre.getText().toString().isEmpty()||precio.getText().toString().isEmpty()){
                    mensaje("No puede haber campos vacios");
                    return;
                }
                String id=generar_cadena(10);
                Map<String,Object> datos=new HashMap<>();
                datos.put("id_articulo",articulo_sel!=null?articulo_sel.getId_articulo():id);
                datos.put("nombre",nombre.getText().toString());
                datos.put("precio",precio.getText().toString());
                db.child("articulos").child(articulo_sel!=null?articulo_sel.getId_articulo():id).setValue(datos);
                mensaje(articulo_sel!=null?"Actualizado":"Agregado");
            }
        });
        if(articulo_sel!=null){
            alert.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.child("articulos").child(articulo_sel.getId_articulo()).setValue(null);
                }
            });
        }
        alert.show();

    }
    private void mensaje(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    private void actualizar_lista(){
        ArrayList<String> txts=new ArrayList<>();
        for(Articulos ar:articulos) {
            txts.add(ar.getNombre()+","+ar.getPrecio());
        }
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,txts);
        ls.setAdapter(adapter);
    }
    public static String generar_cadena(int tam) {
        String cad="";
        String letras="abcdefghijklmnopqrstuvwxyz123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random r=new Random();
        for (int i=0;i<tam;i++){
            int index=r.nextInt(letras.length()-1);
            cad+=letras.substring(index,index+1);
        }
        return cad;
    }
}
