package com.example.dvs.occasus;




import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter{


    public static final String DATABASE_NAME = "Events";
    public static final String DATABASE_TABLE = "event_list";
    public static final int DATABASE_VERSION = 1;
    public static final String value= "event_start_date_time";
    private DbHelper Helper;
    private static Context Context;
    private static SQLiteDatabase db;


    static final String DATABASE_CREATE =
            "create table event_list (_id integer primary key autoincrement, event_name text not null,description text, " +
                    "event_date text not null, event_start_date_time text not null,start_time text not null, end_time text not null,next_day integer not null," +
                    "bluetooth text not null,wifi text not null,profile text not null, mobile_data text not null,monday integer not null," +
                    "tuesday integer not null,wednesday integer not null,thursday integer not null,friday integer not null,saturday integer not null," +
                    "sunday integer not null);";



    public DBAdapter(Context ctx) {
        Context = ctx;
        Helper = new DbHelper(Context);
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                throw e;
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DbAdapter", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }





    public DBAdapter open() throws SQLException {

        db = Helper.getWritableDatabase();
        return this;
    }

    public void close() {

        Helper.close();
    }

    public long insertevent(String name,String desc, String date, String event_start_date_time,String stime, String etime, Integer next,String bluetooth2,
                            String wifi2, String profile,String mobile_data,Integer mon,Integer tue,Integer wed,Integer thu,
                            Integer fri,Integer sat,Integer sun)
    {

        ContentValues initialValues = new ContentValues();
        initialValues.put("event_name", name);
        initialValues.put("description",desc);
        initialValues.put("event_date", date);
        initialValues.put("event_start_date_time",event_start_date_time);
        initialValues.put("start_time",stime);
        initialValues.put("end_time",etime);
        initialValues.put("next_day",next);
        initialValues.put("bluetooth",bluetooth2);
        initialValues.put("wifi",wifi2);
        initialValues.put("profile",profile);
        initialValues.put("mobile_data",mobile_data);
        initialValues.put("monday",mon);
        initialValues.put("tuesday",tue);
        initialValues.put("wednesday",wed);
        initialValues.put("thursday",thu);
        initialValues.put("friday",fri);
        initialValues.put("saturday",sat);
        initialValues.put("sunday",sun);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }



    //---deletes a particular event---
    public void deleteEvent(String date_time)
    {
        open();
        db.delete(DATABASE_TABLE,value + "= ?", new String[]{date_time});
        close();
    }


    //---retrieves all the contacts---
    public Cursor getAllEventsDetails()
    {
        return db.rawQuery("SELECT * from event_list",null);
    }


    // retrieves contacts having name=req_name
    public Cursor getEventDetail(String date_time)
    {
        String edit= "select * from event_list where event_start_date_time =?";
        return db.rawQuery(edit,new String[]{date_time});
    }

    //retrieves contacts having _id=id
   /* public Cursor getEventsDetail1(int id)
    {
        String edit= "select * from event_list where _id =?";
        return db.rawQuery(edit,new String[] {Integer.toString(id)});
    }*/

}
