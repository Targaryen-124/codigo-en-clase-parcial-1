package com.example.codigo_en_clase;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityPhoto extends AppCompatActivity {
  static final int peticionAccesoCamera = 101;
  static final int peticionCapturaImagen = 102;
  String currentPhotoPath, image64;
  ImageView ObjetoImagen;
  Button btnCaptura;
  String pathImagen;

  ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
    @Override
    public void onActivityResult(ActivityResult result) {
      if (result.getResultCode() == Activity.RESULT_OK) {
        // There are no request codes
        Intent data = result.getData();
      }
    }
  });

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
      // tomarFoto();
      // Usamos:
      dispatchTakePictureIntent();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == peticionAccesoCamera) {
      if (grantResults.length > 0 &&
              grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // tomarFoto();
        // Usamos:
        dispatchTakePictureIntent();
      } else {
        Toast.makeText(getApplicationContext(),
                "Acceso Denegado!", Toast.LENGTH_LONG).show();
      }
    }
  }

/*
  private void tomarFoto() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    if (intent.resolveActivity(getPackageManager()) != null) {
      someActivityResultLauncher.launch(intent);
    }
  }
*/

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Añadimos resultCode == RESULT_OK
    if (requestCode == peticionCapturaImagen && resultCode == RESULT_OK) {
      setPic();
      galleryAddPic();

      Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

      if (bitmap != null) {
        image64 = convertImageTo64(bitmap);
        Log.i("Imagen ", image64);
      } else {
        Log.e("Error", "Bitmap is null");
      }
    }
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    String imageFileName = "JPEG_" + timeStamp + "_";

    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

    File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
    );

    // Save a file: path for use with ACTION_VIEW intents
    currentPhotoPath = image.getAbsolutePath();
    return image;
  }

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      // Create the File where the photo should go
      File photoFile = null;

      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        // Error occurred while creating the File
      }

      // Continue only if the File was successfully created
      if (photoFile != null) {
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.codigo_en_clase.fileprovider",
                photoFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        startActivityForResult(takePictureIntent, peticionCapturaImagen);
      }
    }
  }

  private void galleryAddPic() {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File f = new File(currentPhotoPath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    this.sendBroadcast(mediaScanIntent);
  }

  private void setPic() {
    // Get the dimensions of the View
    int targetW = ObjetoImagen.getWidth();
    int targetH = ObjetoImagen.getHeight();

    // Get the dimensions of the bitmap
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    bmOptions.inJustDecodeBounds = true;

    BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

    int photoW = bmOptions.outWidth;
    int photoH = bmOptions.outHeight;

    // Determine how much to scale down the image
    int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

    // Decode the image file into a Bitmap sized to fill the View
    bmOptions.inJustDecodeBounds = false;
    bmOptions.inSampleSize = scaleFactor;
    bmOptions.inPurgeable = true;

    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
    ObjetoImagen.setImageBitmap(bitmap);
  }

  private String convertImageTo64(Bitmap bitmap) {
    ByteArrayOutputStream byteImage = new ByteArrayOutputStream();

    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteImage);

    byte[] byteArray = byteImage.toByteArray();

    return Base64.encodeToString(byteArray, Base64.DEFAULT);
  }
}