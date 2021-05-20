package com.example.getphoto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    ImageView imgviewgallery;
    Button btnBrowse,btnCam, btnSaveData, btnShowData;
    TextView txtvmainName;
    ConvImage ImageConverter;
    DBclass mydb;
    TextView txtHeading;
    byte[] byteimage;

    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        getSupportActionBar ().hide ();
        setContentView ( R.layout.activity_main );

        imgviewgallery=findViewById ( R.id.imageViewGallery );
        btnBrowse=findViewById ( R.id.btnBrowseGallery );
        btnCam=(Button)findViewById ( R.id.btnCamera );
        txtvmainName=(TextView)findViewById ( R.id.editTextName );
        btnSaveData=(Button)findViewById ( R.id.btnSave );
        btnShowData=(Button)findViewById ( R.id.btnShow );
        txtHeading=(TextView)findViewById ( R.id.txtMainHeading );

        btnBrowse.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v)
            {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission ( Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                    {
                        //permission not granted, Request it.
                        String [] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions ( permissions,PERMISSION_CODE );
                    }
                    else
                    {
                        //permission already granted
                        PickImageFromGallery();
                    }



                }else
                    {
                        //system OS is less then the Marshmallow

                        PickImageFromGallery();

                    }

            }
        } );


        btnCam.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                if (CheckAndRequestPermissioin ())
                {
                    takePictureFromCamera();
                }

            }
        } );

        btnSaveData.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                try {


                    // ImageConverter=new ConvImage ();

                    Toast.makeText ( MainActivity.this, "Method Initiated", Toast.LENGTH_SHORT ).show ();
                    ImageConverter=new ConvImage ();

                    byte[] bytesimg = ImageConverter.Conv ( imgviewgallery );
                    Toast.makeText ( MainActivity.this, "bytes[] received", Toast.LENGTH_SHORT ).show ();
                    String Name = txtvmainName.getText ().toString ();
                    mydb = new DBclass ( MainActivity.this );
                    Toast.makeText ( MainActivity.this, "Sent to DB", Toast.LENGTH_SHORT ).show ();
                    if (mydb.save ( Name, bytesimg )) {
                        Toast.makeText ( MainActivity.this, "SuccessFully Saved", Toast.LENGTH_SHORT ).show ();
                        Resetall();
                    } else {
                        Toast.makeText ( MainActivity.this, "Not Saved", Toast.LENGTH_SHORT ).show ();
                    }

                } catch (Exception e)
                {
                    e.printStackTrace ();

                }
            }
        } );

        btnShowData.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                ShowData();
                Bitmap bitmap=ConvertByteArrayToImage(byteimage);
                imgviewgallery.setImageBitmap ( bitmap );
            }
        } );

    }

    //TAKE PICTURE FROM CAMERA METHOD
    private void takePictureFromCamera()
    {
        Intent takepicture=new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (takepicture.resolveActivity ( getPackageManager () )!=null)
        {
            startActivityForResult ( takepicture,2 );
        }

    }

    //PERMISSIONS FOR CAMERA USE AT RUN TIME
    private boolean CheckAndRequestPermissioin()
    {
        if (Build.VERSION.SDK_INT>=23)
        {
            int camerapermission= ActivityCompat.checkSelfPermission ( MainActivity.this,Manifest.permission.CAMERA );
            if (camerapermission==PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions ( MainActivity.this,new String[]{Manifest.permission.CAMERA},20 );
                return false;
            }

        }return true;




    }




    private void PickImageFromGallery()
    {
        Intent intent=new Intent (Intent.ACTION_PICK);
        intent.setType ( "image/*" );
        startActivityForResult ( intent, IMAGE_PICK_CODE );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_CODE:
                {
                    if (grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    {
                        //permission was granted
                        PickImageFromGallery ();
                    }else
                        {
                            //permission was denied
                            Toast.makeText ( this,"Permission Denied", Toast.LENGTH_SHORT ).show ();
                        }
                }
        }

        //ONREQUESTPERMISSION FOR CAMERA
        if (requestCode==20 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            takePictureFromCamera();
        }else
        {
            Toast.makeText ( MainActivity.this,"Permission Not Granted",Toast.LENGTH_SHORT ).show ();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        super.onActivityResult ( requestCode, resultCode, data );

        if (resultCode== Activity.RESULT_OK)
            switch (requestCode)
            {
                case IMAGE_PICK_CODE:
                    Uri SelectedImage=data.getData ();

                        imgviewgallery.setImageURI ( SelectedImage );

                    break;
                    //case added for camera
                case 2:
                    if (resultCode==RESULT_OK) {
                        Bundle bundle = data.getExtras ();
                        Bitmap bitmapImage=(Bitmap)bundle.get ( "data" );
                        imgviewgallery.setImageBitmap ( bitmapImage );
                        break;
                    }
            }

    }

    private void ShowData()
    {

mydb=new DBclass(this);

        Cursor cur=mydb.getalldata ();
        if (cur.getCount ()==0)
        {
            Toast.makeText ( MainActivity.this,"Error, No Data Found",Toast.LENGTH_SHORT ).show ();
            return;
        }
       // StringBuffer buffer=new StringBuffer ();
        while (cur.moveToNext ())
        {
          /*  buffer.append ( "ID"+cur.getString ( 0)+"\n" ) ;
            buffer.append ( "Name"+cur.getString ( 1 )+"\n" );
            buffer.append ( "Image"+cur.getString ( 2 )+"\n" ); */
            txtHeading.setText ( cur.getString ( 0 )+" "+ cur.getString ( 1 ) );
            byteimage=cur.getBlob ( 2 );
            Toast.makeText ( MainActivity.this,"running showdata",Toast.LENGTH_SHORT ).show ();



        }

    }

    private Bitmap ConvertByteArrayToImage(byte[] bytes)
    {
        return BitmapFactory.decodeByteArray ( bytes,0,bytes.length );

    }

    private void Resetall()
    {
        txtvmainName.setText(null);
        imgviewgallery.setImageResource(android.R.color.transparent);
    }


}