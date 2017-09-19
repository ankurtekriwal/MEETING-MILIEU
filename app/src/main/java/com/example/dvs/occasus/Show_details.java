package com.example.dvs.occasus;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


import android.widget.TextView;
import android.widget.Toast;


public class Show_details extends ActionBarActivity {

    String clicked_date_time;
    String st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);


        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        clicked_date_time=getIntent().getStringExtra("clicked_date_time");
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c=db.getEventDetail(clicked_date_time);
        if(c.moveToFirst())
        {
            TextView name= (TextView)findViewById(R.id.name_text);
            name.setText(c.getString(c.getColumnIndex("event_name")));


            TextView desc= (TextView)findViewById(R.id.description_text);
            desc.setText(c.getString(c.getColumnIndex("description")));

            TextView date= (TextView)findViewById(R.id.date_text);
            date.setText(c.getString(c.getColumnIndex("event_date")));

            TextView stime= (TextView)findViewById(R.id.stime_text);
            stime.setText(c.getString(c.getColumnIndex("start_time")));

            TextView etime= (TextView)findViewById(R.id.etime_text);

            String str;
             st=c.getString(c.getColumnIndex("end_time"));
            if(c.getInt(c.getColumnIndex("next_day"))==1) {
                str=st.concat("    +1");
                etime.setText(str);
            }
            else
            {
                etime.setText(st);
            }

            TextView bluetooth= (TextView)findViewById(R.id.bluetooth_text);
            bluetooth.setText(c.getString(c.getColumnIndex("bluetooth")));



            TextView wifi= (TextView)findViewById(R.id.wifi_text);
            wifi.setText(c.getString(c.getColumnIndex("wifi")));

            TextView profile= (TextView)findViewById(R.id.profile_text);
            profile.setText(c.getString(c.getColumnIndex("profile")));

           TextView mobile_data= (TextView)findViewById(R.id.mobile_data_text);
            mobile_data.setText(c.getString(c.getColumnIndex("mobile_data")));

            String repeat="";
            int comma=0;
            TextView repeat_text= (TextView) findViewById(R.id.repeat_text);
            if(c.getInt(c.getColumnIndex("monday"))==1) {
                repeat = repeat.concat("Monday");
                comma = 1;
            }

            if(c.getInt(c.getColumnIndex("tuesday"))==1) {
                if(comma==1)
                {
                    repeat=repeat.concat(", ");
                }
                repeat = repeat.concat("Tuesday");
                comma=1;
            }

            if(c.getInt(c.getColumnIndex("wednesday"))==1) {
                if(comma==1)
                {
                    repeat=repeat.concat(", ");
                }
                repeat = repeat.concat("Wednesday");
                comma = 1;
            }

            if(c.getInt(c.getColumnIndex("thursday"))==1) {
                if(comma==1)
                {
                    repeat=repeat.concat(", ");
                }
                repeat = repeat.concat("Thursday");
                comma = 1;
            }

            if(c.getInt(c.getColumnIndex("friday"))==1) {
                if(comma==1)
                {
                    repeat=repeat.concat(", ");
                }
                repeat = repeat.concat("Friday");
                comma = 1;
            }

            if(c.getInt(c.getColumnIndex("saturday"))==1) {
                if(comma==1)
                {
                    repeat=repeat.concat(", ");
                }
                repeat = repeat.concat("Saturday");
                comma = 1;
            }

            if(c.getInt(c.getColumnIndex("sunday"))==1) {
                if(comma==1)
                {
                    repeat=repeat.concat(", ");
                }

                repeat = repeat.concat("Sunday");
            }

                repeat_text.setText(repeat);


        }
        db.close();


    }

    //back button override......sends the app back to mainactivity screen
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Show_details.this,MainActivity.class);

        startActivity(intent);

    }


}
