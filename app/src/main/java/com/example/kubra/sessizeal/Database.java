package com.example.kubra.sessizeal;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="placesDb.db";
    private static final String TABLE_NAME="placeTable";
    private static final String PRIMARY_KEY="id";
    private  static final String PLACE_ID="place_id";

    public Database(Context context) {super(context, DATABASE_NAME, null,DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_create_table="CREATE TABLE "+TABLE_NAME+"("+PRIMARY_KEY+" INTEGER PRIMARY KEY AUTOINCREMENT ," +
                ""+PLACE_ID+" TEXT"+")";
        db.execSQL(sql_create_table);
    }

    public void lokasyonIdEkle(String placeId){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(PLACE_ID,placeId);
        db.insert(TABLE_NAME,"",values);
        db.close();
    }

    public Cursor getPlaceData(){
        SQLiteDatabase db=this.getReadableDatabase();
        String sorgu="SELECT * FROM "+TABLE_NAME;
        Cursor data=db.rawQuery(sorgu,null);
        return data;

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
