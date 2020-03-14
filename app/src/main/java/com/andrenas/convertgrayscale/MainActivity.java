package com.andrenas.convertgrayscale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();

    Uri imageUri;

    Bitmap imageBitmap, grayBitmap;

    private static final int MY_REQUEST_CODE_PERMISSIONS = 100;
    private static final int MY_REQUEST_GALLERY_CODE = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyPermission();

        OpenCVLoader.initDebug();

        this.mViewHolder.imageView = this.findViewById(R.id.image_container);
        this.mViewHolder.buttonGallery = this.findViewById(R.id.btn_gallery);
        this.mViewHolder.buttonConvert = this.findViewById(R.id.btn_convert);

        //TODO: CRIAR A FUNCIONALIDADE DE SALVAR OS ARQUIVOS CONVERTIDOS NO CELULAR!!!

        this.mViewHolder.buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, MY_REQUEST_GALLERY_CODE);
            }
        });

        this.mViewHolder.buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Mat matRgba = new Mat();
                Mat matGray = new Mat();

                BitmapFactory.Options optBitmap = new BitmapFactory.Options();
                optBitmap.inDither = false;
                optBitmap.inSampleSize = 4;

                int width = imageBitmap.getWidth();
                int height = imageBitmap.getHeight();

                grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

                //bITMAP to Map

                Utils.bitmapToMat(imageBitmap, matRgba);
                Imgproc.cvtColor(matRgba, matGray, Imgproc.COLOR_RGB2GRAY);

                Utils.matToBitmap(matGray, grayBitmap);

                mViewHolder.imageView.setImageBitmap(grayBitmap);

            }
        });

    }

    public void verifyPermission(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //When Granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("GRANTED");
                    builder.setMessage("READ STORAGE");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE},MY_REQUEST_CODE_PERMISSIONS);
                        }//onClick
                    });
                    builder.setNegativeButton("CANCEL", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }//REQUEST PERMISSION
                else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, MY_REQUEST_CODE_PERMISSIONS);
                }

            }//CHECK PERMISSION
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if((grantResults.length > 0) && (grantResults[0])== PackageManager.PERMISSION_GRANTED){
            //PERMISSAO GARANTIDA!!! YAI!!!
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == MY_REQUEST_GALLERY_CODE) &&(data!= null)){

            imageUri = data.getData();

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.mViewHolder.imageView.setImageURI(imageUri);
        }

    }

    private static class ViewHolder{
        ImageView imageView;

        Button buttonGallery;
        Button buttonConvert;

    }

}
