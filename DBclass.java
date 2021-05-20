package com.example.getphoto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBclass extends SQLiteOpenHelper
{
    private static final int DBversion=1;
    private static final String DBName="ImageCollection.db";
    private static final String TableName="myimages";


    public DBclass(@Nullable Context context)
    {
        super ( context, DBName, null, DBversion );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL ( "CREATE TABLE IF NOT EXISTS myimages(id Integer primary key autoincrement, name text, image BLOB)" );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean save( String Name, byte[] img)
    {
        try{
            ContentValues cv=new ContentValues ();
          //  cv.put ( "id", id );
           cv.put ( "Name",Name );
            cv.put ( "Image",img );

            //Opening connection to database
            SQLiteDatabase db=this.getWritableDatabase ();
            db.insert ( "myimages",null,cv );
            return true;

        }catch (Exception e)
        {
            e.printStackTrace ();
            return false;
        }
    }

    //METHOD TO RETRIVE DATA FROM SQLite
    public Cursor getalldata()
    {
        SQLiteDatabase db=this.getWritableDatabase ();
        Cursor cur= db.rawQuery ("SELECT * FROM "+TableName,null);
        return cur;
    }
}
