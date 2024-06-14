package com.example.codigo_en_clase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivityPhoto extends AppCompatActivity {
  static final int peticionAccesoCamera = 101;
  static final int peticionCapturaImagen = 102;
  ImageView ObjetoImagen;
  Button btnCaptura;
  String pathImagen;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_photo);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    ObjetoImagen = (ImageView) findViewById(R.id.imageView);
    btnCaptura = (Button) findViewById(R.id.btntakefoto);

    btnCaptura.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        permisos();
      }
    });
  }

  private void permisos() {
    if (ContextCompat.checkSelfPermission(
            getApplicationContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.CAMERA},
              peticionAccesoCamera);
    } else {
      tomarFoto();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == peticionAccesoCamera) {
      if (grantResults.length > 0 &&
              grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        tomarFoto();
      } else {
        Toast.makeText(getApplicationContext(),
                "Acceso Denegado!", Toast.LENGTH_LONG).show();
      }
    }
  }

  private void tomarFoto() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    if (intent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(intent, peticionCapturaImagen);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == peticionCapturaImagen) {
      Bundle extras = data.getExtras();

      Bitmap imagen = (Bitmap) extras.get("data");

      ObjetoImagen.setImageBitmap(imagen);
    }
  }
}