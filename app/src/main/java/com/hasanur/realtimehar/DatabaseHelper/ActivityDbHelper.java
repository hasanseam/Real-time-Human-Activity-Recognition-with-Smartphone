package com.hasanur.realtimehar.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.hasanur.realtimehar.R;

public class ActivityDbHelper extends SQLiteOpenHelper {
     private Context context;
     private static final String DATABASE_NAME = "Activity.db";
     private static final int DATABASE_VERSION = 1;

     private static final String TABLE_NAME_ACTIVITY = "activities";
     private static final String COLUMN_ID_ACTIVITY = "_id";
     private static final String COLUMN_ACTIVITY_ACTIVITY = "activity_name";

     private static final String TABLE_NAME_ACTIVE_ACTIVITY = "active_activity";
     private static final String COLUMN_POSITION_ACTIVE_ACTIVITY = "activity_position";

     public ActivityDbHelper(Context context){
          super(context,DATABASE_NAME,null,DATABASE_VERSION);
          this.context = context;
     }

     @Override
     public void onCreate(SQLiteDatabase db) {
          String query = "CREATE TABLE "+TABLE_NAME_ACTIVITY+
                  "("+COLUMN_ID_ACTIVITY + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COLUMN_ACTIVITY_ACTIVITY+" TEXT );";
          String query2 = "CREATE TABLE "+TABLE_NAME_ACTIVE_ACTIVITY+
                  " ( "+ COLUMN_POSITION_ACTIVE_ACTIVITY+" TEXT)";
          db.execSQL(query2);
          db.execSQL(query);
     }

     public long addActivity(String activity){
          SQLiteDatabase db = this.getWritableDatabase();
          ContentValues cv = new ContentValues();

          cv.put(COLUMN_ACTIVITY_ACTIVITY,activity);
          long result = db.insert(TABLE_NAME_ACTIVITY,null,cv);
          if(result==-1){
               Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
          }else{
               Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
          }
          return result;
     }

     public void setPositionInActiveActivity(String position){
          //delete previous data first as only one value will be used
          deleteDataFromActiveActivity();
          //insert operation
          SQLiteDatabase db = this.getWritableDatabase();
          ContentValues cv = new ContentValues();
          cv.put(COLUMN_POSITION_ACTIVE_ACTIVITY,position);
          long result = db.insert(TABLE_NAME_ACTIVE_ACTIVITY,null,cv);
     }

     public Cursor readAllDataFromActiveActivity(){
          return readAllData(TABLE_NAME_ACTIVE_ACTIVITY);
     }

     public Cursor readAllDataFromActivity(){
          return readAllData(TABLE_NAME_ACTIVITY);
     }

     private Cursor readAllData(String TableName){
          String query = "SELECT * FROM " + TableName;
          SQLiteDatabase db = this.getReadableDatabase();

          Cursor cursor = null;
          if(db != null){
               cursor = db.rawQuery(query, null);
          }
          return cursor;
     }

     public void deleteOneRowFromActivity(String row_id){
          SQLiteDatabase db = this.getWritableDatabase();
          long result = db.delete(TABLE_NAME_ACTIVITY, "_id=?", new String[]{row_id});
          if(result == -1){
               Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
          }else{
               Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
          }
     }

     public void deleteDataFromActiveActivity(){
           deleteAllData(TABLE_NAME_ACTIVE_ACTIVITY);
     }

     void deleteAllData(String tableName){
          SQLiteDatabase db = this.getWritableDatabase();
          db.execSQL("DELETE FROM " + tableName);
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          // Upgrade policy if the database version changes
     }
}
