package com.example.codigo_en_clase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ActivityVideo extends AppCompatActivity {
  private static final int solicitarVideo = 101;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_video);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    Button comenzarGrabacion = findViewById(R.id.buttonGrabar);

    comenzarGrabacion.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
          startActivityForResult(videoIntent, solicitarVideo);
        } else {
          Toast.makeText(ActivityVideo.this, "No hay camara.", Toast.LENGTH_SHORT).show();
        }

      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == solicitarVideo && resultCode == RESULT_OK) {
      Uri videoUri = data.getData();
      Toast.makeText(this, "Guardado en" + videoUri, Toast.LENGTH_LONG).show();

      try {
        // Convert video to Base64
        String base64Video = convertVideoTo64(this, videoUri);
        Log.i("Video", base64Video);
      } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(this, "Error al convertir", Toast.LENGTH_SHORT).show();
      }
    } else {
      Toast.makeText(this, "Error al grabar", Toast.LENGTH_SHORT).show();
    }
  }

  private String convertVideoTo64(Context context, Uri videoUri) throws IOException {
    InputStream inputStream = context.getContentResolver().openInputStream(videoUri);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      byteArrayOutputStream.write(buffer, 0, bytesRead);
    }

    byte[] videoBytes = byteArrayOutputStream.toByteArray();
    inputStream.close();
    byteArrayOutputStream.close();

    return Base64.encodeToString(videoBytes, Base64.DEFAULT);
  }
}