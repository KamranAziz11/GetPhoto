package com.example.getphoto;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class ConvImage
{
    public byte[] Conv(ImageView img)
    {



            Bitmap bitmap = ((BitmapDrawable) img.getDrawable ()).getBitmap ();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ();
            bitmap.compress ( Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream );
            return byteArrayOutputStream.toByteArray ();



    }

}
