package com.example.codigo_en_clase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import utilidades.Personas;
import utilidades.SQLiteConexion;
import utilidades.Trans;

public class ActivityCombo extends AppCompatActivity {

  SQLiteConexion conexion;
  Spinner combopersonas;
  EditText nombres, apellidos, correo;
  ArrayList<Personas> lista;
  ArrayList<String> Arreglo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_combo);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    conexion = new SQLiteConexion(this, Trans.DBname, null, Trans.Version);
    combopersonas = (Spinner) findViewById(R.id.spinner);
    nombres = (EditText) findViewById(R.id.cbnombre);
    apellidos = (EditText) findViewById(R.id.cbapellido);
    correo = (EditText) findViewById(R.id.cbcorreo);

    ObtenerInfo();
  }

  private void ObtenerInfo() {
    // Traer los datos de la base de datos para que sean leidos.
    SQLiteDatabase db = conexion.getReadableDatabase();

    //Crear una variable "person" del tipo "Personas" que no haga referncia a ningun objeto "null".
    Personas person = null;

    // Crear un array que contenga los objetos del tipo "Personas".
    lista = new ArrayList<Personas>();

    // Creamos un "Cursor" que permita ejecutar la consulta de SQL.
    Cursor cursor = db.rawQuery(Trans.SelectAllPerson, null);

    // El bucle while nos dice que mientras hayan filas en el cursor esta devolvera true
    // permitiendo que valla a la siguiente fila con el metodo ".moveToNext()".
    while (cursor.moveToNext()) {
      // Creamos una nueva instancia de "Personas"
      person = new Personas();
      // Para las demas lineas "seteamos" el valor dependiendo del "columnIndex" que tenga.
      person.setId(cursor.getInt(0));
      person.setNombres(cursor.getString(1));
      person.setApellidos(cursor.getString(2));
      person.setCorreo(cursor.getString(4));

      // En cada vuelta del bucle agregamos los datos al "ArrayList lista".
      lista.add(person);
    }

    // Cerramos el cursor, esto no afecta en nada al codigo pero es una
    // buena practica para guardar recursos.
    cursor.close();

    // El metodo MostrarPersonas cumple 2 funciones:
    // 1. Mostrar los datos de las personas en el "Spinner",
    // ejemplo: Persona 1, Persona 2
    // 2. Llenaria las filas de texto que declaramos arriba, serian:
    // nombres, apellidos, correo.
    MostrarPersonas();
  }

  private void MostrarPersonas() {
    Arreglo = new ArrayList<String>();

    // El bucle for recorrera cada elemento del arreglo "lista"
    // que almacena la informacion de las personas que tenemos.
    for (int i = 0; i < lista.size(); i++) {
      // Dentro del bucle asignamos cada elemento de "Personas" al
      // "Arreglo".
      Arreglo.add(lista.get(i).getId() + " " +
              lista.get(i).getNombres() + " " +
              lista.get(i).getApellidos());
    }

    // Creamos un nuevo "ArrayAdapter", esto para poder mostrarlo con el
    // simple_spinner_item, y aqui poder mostrar el nombre de las
    // personas.
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arreglo);

    // Ahora aqui establecemos el "setDropDownViewResource", que hara posible
    // la vista desplegable.
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // Asignamos el "ArrayAdapter<String> adapter" al "combopersonas", esto
    // para mostrar los datos como el nombre.
    combopersonas.setAdapter(adapter);

    // Con el "combopersonas" le decimos que cuando seleccionemos un item
    // con el metodo ".setOnItemSelectedListener()" haga algo.
    combopersonas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Declaramos una variable para saber la posicion en la que tiene que poner
        // los datos.
        Personas personaSeleccionada = lista.get(position);

        // Establecemos los datos en los campos de texto correspondientes.
        nombres.setText(personaSeleccionada.getNombres());
        apellidos.setText(personaSeleccionada.getApellidos());
        correo.setText(personaSeleccionada.getCorreo());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Si no hay nada seleccionado mostrar algo aqui.
      }
    });
  }
}