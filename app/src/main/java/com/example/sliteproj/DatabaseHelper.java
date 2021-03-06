package com.example.sliteproj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Constraints;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table
        db.execSQL(Constants.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ Constants.TABLE_NAME);
        onCreate(db);
    }

    // insert information
    public long insertInfo(String image, String name, String age, String phone, String des, String addTimeStamp, String updateTimeStamp) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_AGE, age);
        values.put(Constants.C_PHONE, phone);
        values.put(Constants.C_DES, des);
        values.put(Constants.C_IMAGE, image);
        values.put(Constants.C_Add_TIMESTAMP, addTimeStamp);
        values.put(Constants.C_UPDATED_TIMESTAMP, updateTimeStamp);

        long id = db.insert(Constants.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // update information
    public void updateInfo(String id, String image, String name, String age, String phone, String des, String addTimeStamp, String updateTimeStamp) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_AGE, age);
        values.put(Constants.C_PHONE, phone);
        values.put(Constants.C_DES, des);
        values.put(Constants.C_IMAGE, image);
        values.put(Constants.C_Add_TIMESTAMP, addTimeStamp);
        values.put(Constants.C_UPDATED_TIMESTAMP, updateTimeStamp);

        db.update(Constants.TABLE_NAME, values, Constants.C_ID + " = ?", new String[]{id});
        db.close();
    }

    // delete information
    public void deleteInfo(String id) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.C_ID + " = ?", new String[]{id});
        db.close();
    }

    public ArrayList<Model> getAllData(String orderBy) {

        ArrayList<Model> arrayList = new ArrayList<>();
        // query for select info
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToNext()) {
            do {
                Model model = new Model(
                        ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_AGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_PHONE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_DES)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_Add_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP)));

                arrayList.add(model);
            }while (cursor.moveToNext());
        }

        db.close();
        return arrayList;
    }

    /*public int getCounts() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }*/
}
