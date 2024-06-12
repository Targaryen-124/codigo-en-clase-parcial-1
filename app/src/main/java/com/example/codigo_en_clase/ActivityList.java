package com.example.codigo_en_clase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import utilidades.Personas;
import utilidades.SQLiteConexion;
import utilidades.Trans;


public class ActivityList extends AppCompatActivity {


  SQLiteConexion conexion;
  ListView listPerson;

  ArrayList<Personas> lista;

  ArrayList<String> Arreglo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_list);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    conexion = new SQLiteConexion(this, Trans.DBname, null, Trans.Version);
    listPerson = findViewById(R.id.listPerson);

    ObtenerInfo();

    ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Arreglo);
    listPerson.setAdapter(adp);

    listPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String ElementoSeleccionado = (String) parent.getItemAtPosition(position);

        Toast.makeText(getApplicationContext(), ElementoSeleccionado, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void ObtenerInfo() {
    SQLiteDatabase db = conexion.getReadableDatabase();
    Personas person = null;
    lista = new ArrayList<Personas>();

    // Cursor para recorrer los datos de l a tabla
    Cursor cursor = db.rawQuery(Trans.SelectAllPerson, null);

    while (cursor.moveToNext()) {
      person = new Personas();
      person.setId(cursor.getInt(0));
      person.setNombres(cursor.getString(1));
      person.setApellidos(cursor.getString(2));
      person.setEdad(cursor.getInt(3));
      person.setCorreo(cursor.getString(4));

      lista.add(person);
    }

    cursor.close();

    FillData();
  }

  private void FillData() {
    Arreglo = new ArrayList<String>();
    for (int i = 0; i < lista.size(); i++) {
      Arreglo.add(lista.get(i).getId() + " " +
              lista.get(i).getNombres() + " " +
              lista.get(i).getApellidos());
    }
  }
}