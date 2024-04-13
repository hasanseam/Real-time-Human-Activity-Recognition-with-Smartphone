package com.hasanur.realtimehar.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class SensorDbHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "Sensor.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_SENSOR = "sensor";
    private static final String COLUMN_ID_SENSOR = "_id";
    private static final String COLUMN_SENSOR_NAME_SENSOR = "checked_sensor_name";

    public SensorDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+TABLE_NAME_SENSOR+
                "("+COLUMN_ID_SENSOR + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_SENSOR_NAME_SENSOR+" TEXT );";
        db.execSQL(query);
    }

    public void addSensorName(String sensorName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SENSOR_NAME_SENSOR,sensorName);
        long result = db.insert(TABLE_NAME_SENSOR,null,cv);
        if(result==-1){
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Added Succesfully!",Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSensorName(String sensorName){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME_SENSOR,
                COLUMN_SENSOR_NAME_SENSOR+"=?", new String[]{sensorName});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME_SENSOR;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SENSOR);
        onCreate(db);
    }

}
