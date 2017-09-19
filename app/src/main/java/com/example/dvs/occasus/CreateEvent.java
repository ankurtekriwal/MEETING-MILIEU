package com.example.dvs.occasus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.app.Dialog;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class CreateEvent extends ActionBarActivity {

    int shour, sminute,ehour,eminute;
    int yr, month, day;
    static final int DATE_DIALOG_ID = 1;
    static final int TIME_DIALOG_ID = 0;
    String event_name;
    String desc;
    EditText eve_name1;
    EditText descrip1;
    String start_strtime="00:00";
    int start_time_set=0;
    String end_strtime="24:00";//so that when user enters stime, stime is smaller than etime....so "+1" message doesn't appears
    int end_time_set=0;
    //we can't check if end time was entered by user by intial value since initial value is 24:00
    // if user also enters 24:00 then problem occurs, so end_time_set used

    String eve_date="yo";
    String eve_date1;
    String eve_date2;
    int overlap;
    Switch s;
    int flag;
    int add=0;

    Integer[] ret_days={0,0,0,0,0,0,0};
    int database_rep;
    int time_iden1=0;
    int time_iden2=0;
    int next_day=0;
    int id;
    TextView next;
    Button b;
    CharSequence[] items = { "Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday","Sunday" };
    boolean[] itemsChecked = new boolean [items.length];

    String[] day_array={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    String profile="ring";
    String bluetooth="no",wifi1="no",mobile_data="no";
    String profile_status;
    EditText editText_name;
    EditText editText_desc;
    EditText editText_date;
    EditText editText_stime;
    EditText editText_etime;
    EditText editText_repeat;
    String edit_date_time;
    String[] repeat_days={ "Mo   ","Tu   ","We   ","Th   ","Fr   ","Sa   ","Su  "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//to hide back button on action bar


        //ImageView image = (ImageView) findViewById(R.drawable.occasus1);


        Intent intent = getIntent();
        editText_name=(EditText) findViewById(R.id.eve_name);
        editText_desc=(EditText) findViewById(R.id.descrip);
        editText_date=(EditText) findViewById(R.id.date);
        editText_stime=(EditText) findViewById(R.id.stime);
        editText_etime=(EditText) findViewById(R.id.etime);
        editText_repeat=(EditText) findViewById(R.id.repeat);

        flag=intent.getIntExtra("flag",0);//flag passed from main activity class to create event class
        // flag=0 if new event is being created
        //flag=1 if event is being edited



        Calendar today = Calendar.getInstance();//today contains current date and time when event is being created
        yr = today.get(Calendar.YEAR);  //yr initialized
        month = today.get(Calendar.MONTH);  //month initialized
        day = today.get(Calendar.DAY_OF_MONTH);  //date initialized



        descrip1 = (EditText)findViewById(R.id.descrip);// descrip1 -> reference object to description field in xml
        TextView textView =(TextView)findViewById(R.id.textView2);// textview -> reference object to title
        next= (TextView)findViewById(R.id.next);  //next -> reference object for "+1" thing in xml




        next_day=0;//next_day initialized.....next_day=1 => event goes on to next day




        if(flag==0)//if new event is being created
        {
            start_time_set=0;
            end_time_set=0;//end_time_set=0 => user hasn't entered any time....used in checking if  etime>stime

            //to initialize item_checked array.......item_checked used for storing checked values in repetition dialog box days
            for (int i = 0; i < 7; i++)
            {
                itemsChecked[i] = false;//no day is checked in repeat dialog box
            }
        }





        if(flag==1)//if event is being edited
        {
            textView.setText("Edit Event");//title set to "edit event"
            start_time_set=1;
            end_time_set=1;//user entered some end time(when event was created)

            edit_date_time= intent.getStringExtra("clicked_date_time");
            //clicked_id is id of the profile to be edited
            //clicked_id is sent to createvent from mainactivity if user wants to edit the event




            DBAdapter db1 = new DBAdapter(CreateEvent.this);
            db1.open();//database open containing event details
            Cursor c;//to get details of all events with name=req_name
            c=db1.getEventDetail(edit_date_time);
            c.moveToFirst();
            id=c.getInt(c.getColumnIndex("_id"));//get the id of the event to be edited from database
            event_name= c.getString(c.getColumnIndex("event_name"));//event_name= previous name of event from database
            desc= c.getString(c.getColumnIndex("description"));//desc= previous description of event from database
            eve_date= c.getString(c.getColumnIndex("event_date"));//eve_date= previous date of event from database
            start_strtime=c.getString(c.getColumnIndex("start_time"));//start_strtime= previous stime from database
            end_strtime= c.getString(c.getColumnIndex("end_time"));//end_strtime = previous etime from database
            String status;
            status = c.getString(c.getColumnIndex("bluetooth"));//to check the previously "entered" bluetooth state
            bluetooth = status;
            status = c.getString(c.getColumnIndex("wifi"));//getting earlier wifi toggle button state
            wifi1 = status;
            status = c.getString(c.getColumnIndex("mobile_data"));//getting earlier mobile data toggle button state
            mobile_data = status;
            profile_status = c.getString(c.getColumnIndex("profile"));//getting profile selected earlier
            profile = profile_status;






            String rep="";
            //to check the days which were checked for repetion earlier
            if(c.getInt(c.getColumnIndex("monday"))==1)//if event used to repeat on monday(got from database)
            {
                itemsChecked[0] = true;

                rep=rep.concat("Mo   ");

            }
            else//event didn't use to repeat on monday(got from database)
            {
                 itemsChecked[0] = false;


            }

            if(c.getInt(c.getColumnIndex("tuesday"))==1)//event used to repeat on tuesday(got from database)
            {
                itemsChecked[1] = true;

                rep=rep.concat("Tu   ");

            }
            else//event didn't use to repeat on tuesday(from database)
            {
                itemsChecked[1] = false;


            }

            if(c.getInt(c.getColumnIndex("wednesday"))==1)//event used to repeat on wednesday(from database)
            {
                itemsChecked[2] = true;

                rep=rep.concat("We   ");

            }
            else//didn't use to repeat on wednesday(from database)
            {
                itemsChecked[2] = false;


            }

            if(c.getInt(c.getColumnIndex("thursday"))==1)//used to repeat on thursday(from database)
            {
                itemsChecked[3] = true;

                rep=rep.concat("Th   ");
            }
            else//didn't use to repeat on thursday(from database)
            {
                itemsChecked[3] = false;

            }

            if(c.getInt(c.getColumnIndex("friday"))==1)//used to repeat on friday(from database)
            {
                itemsChecked[4] = true;

                rep=rep.concat("Fr   ");
            }
            else//didn't use to repeat on friday(from database)
            {
                itemsChecked[4] = false;

            }

            if(c.getInt(c.getColumnIndex("saturday"))==1)//used to repeat on saturday(from database)
            {
                itemsChecked[5] = true;

                rep=rep.concat("Sa   ");
            }
            else//didn't use to repeat from database(from database)
            {
                itemsChecked[5] = false;

            }

            if(c.getInt(c.getColumnIndex("sunday"))==1)//used to repeat on sunday(from database)
            {
                itemsChecked[6] = true;

                rep=rep.concat("Su   ");
            }
            else//didn't use to repeat on sunday(from database)
            {
                itemsChecked[6] = false;
            }





            if(c.getInt(c.getColumnIndex("next_day"))==1)//if event extends to next day(from database)
            {
                next.setText("+1");
                next_day=1;
            }
            else {
                next.setText(" ");
                next_day=0;
            }






            //to set the fields to the values entered earlier
            editText_name.setText(event_name);//eve_name1(textfield)= name of the event
            editText_desc.setText(desc);//descrip1(textfield)= description of the event
            editText_date.setText(eve_date);
            editText_stime.setText(start_strtime);
            editText_etime.setText(end_strtime);
            editText_repeat.setText(rep);




            //in this way we are able to show all the previously entered information so that user doesn't have to rewrite everything





            shour=start_strtime.charAt(1)-'0'+(start_strtime.charAt(0)-'0')*10;//shour = start hour in integer
            sminute=start_strtime.charAt(4)-'0'+(start_strtime.charAt(3)-'0')*10;//sminute= start min in int
            ehour=end_strtime.charAt(1)-'0'+(end_strtime.charAt(0)-'0')*10;//ehour= end hour in int
            eminute=end_strtime.charAt(4)-'0'+(end_strtime.charAt(3)-'0')*10;//eminute= end min in int

            yr=(eve_date.charAt(6)-'0')*1000+(eve_date.charAt(7)-'0')*100+(eve_date.charAt(8)-'0')*10+(eve_date.charAt(9)-'0');
            //yr= year in int
            month=(eve_date.charAt(3)-'0')*10+(eve_date.charAt(4)-'0');//month= month in int
            month--;//month is 1 less by default in android
            day=(eve_date.charAt(0)-'0')*10+(eve_date.charAt(1)-'0');//day= date in int





            db1.close();//closing the database

        }





    }




    //back button override......sends the app back to mainactivity screen
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(CreateEvent.this,MainActivity.class);
        startActivity(intent);
    }







    //calls SetToggles activity
    //on click listener for set toggles button
    //sends the data of create event to set toggles
    public void set_toggles(View view)
    {

        Intent intent = new Intent(CreateEvent.this, SetToggles.class);//intent for settoggles activity is created


        overlap=0;//overlap=0 means event is valid


        event_name = editText_name.getText().toString();//event_name contains the name of event (which is getting created) or (edited name)
        desc = editText_desc.getText().toString();//desc contains the desc of event (which is to be created) or (edited desc)



        for(int i=0;i<=6;i++)
        {
            if(itemsChecked[i])
            {
                add=1;
                break;
            }
        }
        //add=0 means that there is no repeat or no day is checked in repeat dialog
        //add=1 means that event is to be repeated





        Date date1 = new Date(yr, month, day-1, 0, 0);//date1 contains the current entered date of the event
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String goal = outFormat.format(date1);//for finding day at current date





        DBAdapter db = new DBAdapter(this);
        String ret_name="-1";//initizing ret_name
        db.open();//open database containing event details
        Cursor c1 = db.getAllEventsDetails();//retrieves all the existing events details from the database






        if (c1.moveToFirst())
        {

            do//checks for overlapping
                {
                    if((flag==0)||((!edit_date_time.equals(c1.getInt(c1.getColumnIndex("event_start_date_time"))))))
                    //to ensure that we don't compare the current version and the older version(in database) of same event
                    //coz both versions will never remain together while editing
                    {

                        //RETREIVING OTHER EVENT'S DETAILS
                        String ret_endtime = c1.getString(c1.getColumnIndex("end_time"));
                        //ret_endtime contains the endtime of a event already present in database
                        String ret_starttime = c1.getString(c1.getColumnIndex("start_time"));
                        //ret_starttime contains the starttime of a event already present in database

                        String ret_date = c1.getString(c1.getColumnIndex("event_date"));
                        //ret_date contains the date of a event already present in database
                        int database_Date = (ret_date.charAt(0) - 48) * 10 + ret_date.charAt(1) - 48;
                        //database_date = date of event from database in int
                        int database_Month = (ret_date.charAt(3) - 48) * 10 + ret_date.charAt(4) - 48;
                        //database_month = month of event from database in int
                        int database_Year = (ret_date.charAt(6) - 48) * 1000 + (ret_date.charAt(7) - 48) * 100 + (ret_date.charAt(8) - 48) * 10 + (ret_date.charAt(9) - 48);
                        //database_year = month of event from database in int
                        database_Date--;//there was some issue with calculating day at a date....
                        //"sometimes" date has to be 1 less than actual to get correct day
                        database_Month--;//month is 1 less by default in android studio

                        ret_name = c1.getString(c1.getColumnIndex("event_name"));
                        //ret_endtime contains the endtime of a event already present in database
                        ret_days[0] = c1.getInt(c1.getColumnIndex("monday"));
                        ret_days[1] = c1.getInt(c1.getColumnIndex("tuesday"));
                        ret_days[2] = c1.getInt(c1.getColumnIndex("wednesday"));
                        ret_days[3] = c1.getInt(c1.getColumnIndex("thursday"));
                        ret_days[4] = c1.getInt(c1.getColumnIndex("friday"));
                        ret_days[5] = c1.getInt(c1.getColumnIndex("saturday"));
                        ret_days[6] = c1.getInt(c1.getColumnIndex("sunday"));
                        //ret_days[i]=1 means the event in database repeats on ith day
                        database_rep = 0;//initializing database_rep......database_rep=0 =>event from database doesn't repeats
                        for (int i = 0; i < 7; i++)
                        {
                            if (ret_days[i] == 1)//if the event from database repeats for any day
                            {
                                database_rep = 1;//means event from database repeats
                                break;
                            }
                        }
                        int ret_next_day = c1.getInt(c1.getColumnIndex("next_day"));
                        //ret_next_day tells if the event from database repeats





                        //if current event extends upto next day
                        if (next_day == 1)
                        {
                            if (ret_next_day == 1)//if event from database extends upto next day
                            {

                                if (add == 1)//for new events with repetition
                                 {

                                    if (database_rep == 1)//if retrieved event repeats
                                     {

                                        for (int i = 0; i <= 6; i++)
                                        {
                                            //if on any day both existing event and event to be created occur
                                            if ((itemsChecked[i]) && (ret_days[i] == 1))
                                            {
                                                overlap = 1;//overlap=1 -> clash
                                                break;
                                            }
                                            else if ((itemsChecked[i]) && (ret_days[(i + 1)%7] == 1))
                                            {
                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                //takes two strings and compare them using polynomial hashing
                                                if (time_iden2 == 1)//if(ret_starttime<end_strtime)
                                                {
                                                    overlap = 1;//overlap=1 -> clash
                                                    break;
                                                }
                                            }
                                            else if ((itemsChecked[i]) && (ret_days[(i +6)%7] == 1))
                                            {
                                                time_iden1 = time_identifier1(start_strtime, ret_endtime);                                           //takes two strings and compare them using polynomial hashing
                                                //takes two strings and compare them using polynomial hashing
                                                if (time_iden1 == 1)//if(start_strtime<ret_endtime)
                                                {
                                                    overlap = 1;//overlap=1 -> clash
                                                    break;
                                                }
                                            }
                                        }
                                        if (overlap == 1)//if event overlapped
                                            break;//stop fetching further events from database
                                    }


                                    else //if retrieved event does not repeat
                                    {
                                        Date date2 = new Date(database_Year, database_Month, database_Date, 0, 0);
                                        //date2 contains the date at which the event in database is supposed to start
                                        SimpleDateFormat outFormat1 = new SimpleDateFormat("EEEE");
                                        String goal1 = outFormat1.format(date2);//for finding day at date2


                                        for (int i = 0; i <= 6; i++)
                                        {

                                            if ((goal1.equals(day_array[i])) && (itemsChecked[i]))
                                            //if current event repeats on the day on which event from database occurs
                                            {
                                                int y1 = date_identifier1(ret_date, eve_date);
                                                //takes two strings and compare them using polynomial hashing
                                                if (y1 == 2)//if event from database occurs after current event(date wise)
                                                {
                                                    overlap = 1;//clashing
                                                    break;
                                                }
                                            }
                                            else if ((goal1.equals(day_array[(i + 1)%7])) && (itemsChecked[i]))
                                            {
                                                int y1 = date_identifier1(ret_date, eve_date);
                                                if (y1 == 2) //if date wise clash
                                                {
                                                    time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                    if (time_iden2 == 1) //if time wise clash
                                                    {
                                                            overlap = 1;//clash
                                                            break;
                                                    }
                                                }
                                            }
                                            else if ((goal1.equals(day_array[(6+i)%7])) && (itemsChecked[i]))
                                            {
                                                int y1 = date_identifier1(ret_date, eve_date1);
                                                if (y1 == 2)//if date wise clash
                                                {
                                                    time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                    if (time_iden1 == 1)//if time wise clash
                                                    {
                                                        overlap = 1;//clash
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (overlap == 1)//if event overlaps
                                            break;//don't look for other events in database
                                    }
                                }

                                else//for new events without repeat
                                {

                                    if (database_rep == 1)   //if retrieved event repeats
                                    {
                                        for (int i = 0; i <= 6; i++)
                                        {
                                            //goal contains the day of the event to be created
                                            //if goal equals ith day and at that day existing event is also repeated
                                            if ((goal.equals(day_array[i])) && (ret_days[i] == 1)) //day clash
                                            {
                                                int date_clash = date_identifier1(eve_date, ret_date);
                                                //if event to be created occurs on a date after the starting date of an existing event
                                                if (date_clash == 2)//date clash
                                                {
                                                    overlap=1;//clash
                                                    break;
                                                }

                                            }
                                            else if ((goal.equals(day_array[i])) && (ret_days[(i+1)%7] == 1)) {
                                                int date_clash = date_identifier1(eve_date2, ret_date);
                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                if (date_clash == 2) //if date clash
                                                {
                                                    if (time_iden2 == 1)//if time overlap
                                                    {
                                                        overlap = 1;//overlap
                                                        break;
                                                    }
                                                }
                                            }
                                            else if ((goal.equals(day_array[i])) && (ret_days[(i+6)%7] == 1))
                                            {
                                                int date_clash = date_identifier1(eve_date, ret_date);//date clash
                                                time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                if (date_clash == 2)//date clash
                                                {
                                                    if (time_iden1 == 1) //time clash
                                                    {
                                                        overlap = 1;//clash
                                                        break;
                                                    }
                                                }
                                            }

                                        }
                                        if (overlap == 1)//event overlapss
                                            break;//stop looking for other events in database
                                    }

                                    else//if retrieved event does not repeat
                                    {

                                        int new_date;
                                        int new_month;
                                        int new_year;
                                        int ex_date;
                                        int ex_month;
                                        int ex_year;
                                        new_date = (eve_date.charAt(0) - '0') * 10 + (eve_date.charAt(1) - '0');
                                        //date of current event in int
                                        new_month = (eve_date.charAt(3) - '0') * 10 + (eve_date.charAt(4) - '0');
                                        //month of current event in int
                                        new_year = (eve_date.charAt(6) - '0') * 1000 + (eve_date.charAt(7) - '0') * 100 + (eve_date.charAt(8) - '0') * 10 + (eve_date.charAt(9) - '0');
                                        //year of current event in int
                                        ex_date = (ret_date.charAt(0) - '0') * 10 + (ret_date.charAt(1) - '0');
                                        //date of event from database in int
                                        ex_month = (ret_date.charAt(3) - '0') * 10 + (ret_date.charAt(4) - '0');
                                        //month of event from database in int
                                        ex_year = (ret_date.charAt(6) - '0') * 1000 + (ret_date.charAt(7) - '0') * 100 + (ret_date.charAt(8) - '0') * 10 + (ret_date.charAt(9) - '0');
                                        //year of event from database in int




                                        if ((new_month == ex_month) && (new_year == ex_year))//if both events have same month and year
                                        {
                                            if (new_date - ex_date == 0)//event also have same date
                                            {
                                                overlap = 1;
                                                //clash (since both event extend upto same day.....definitely they'll clash
                                                break;//to need to look further in database
                                            }
                                            else if (new_date - ex_date == 1) //if current event occurs on day after old event
                                            {
                                                time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                //takes 2 time strings and compare by polynomial hashing

                                                if (time_iden1 == 1)//if current event starts before event in database ends
                                                {
                                                    overlap = 1;//clash
                                                    break;//no need to look further in database
                                                }

                                            }
                                            else if (new_date - ex_date == -1)
                                            //if current event starts day before event from database
                                            {
                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                //takes 2 time strings and compare by polynomial hashing

                                                if (time_iden2 == 1)
                                                //if current event ends after the event from database starts
                                                {
                                                    overlap = 1;//clash
                                                    break;//no need to look further in database
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else //event from database doesn't extend upto next day
                            {

                                if (add == 1) //for new events with repetition
                                 {
                                    if (database_rep == 1) //if retrieved event repeats
                                    {

                                        for (int i = 0; i <= 6; i++)
                                        {
                                            if ((itemsChecked[i]) && (ret_days[i] == 1))
                                            //if on any day both existing event and event to be created occur
                                            {
                                                time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                //takes 2 time strings and compare by polynomial hashing
                                                if (time_iden1 == 1)
                                                {
                                                    overlap = 1;
                                                    break;
                                                }
                                            } else if ((itemsChecked[i]) && (ret_days[(i + 1)%7] == 1)) {
                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                if (time_iden2 == 1) {
                                                    overlap = 1;
                                                    break;
                                                }
                                            }

                                        }
                                        if (overlap == 1)
                                            break;
                                    }

                                    else //if retrieved event does not repeat
                                    {
                                        Date date2 = new Date(database_Year, database_Month, database_Date, 0, 0);
                                        //date2 contains the date at which the event in database is supposed to start

                                        SimpleDateFormat outFormat1 = new SimpleDateFormat("EEEE");
                                        //for finding day at date2
                                        String goal1 = outFormat1.format(date2);


                                        for (int i = 0; i <= 6; i++) {
                                            if ((goal1.equals(day_array[i])) && (itemsChecked[i])) {

                                                int y1 = date_identifier1(ret_date, eve_date);
                                                if (y1 == 2) {

                                                    overlap = 1;
                                                    break;

                                                }
                                            } else if ((goal1.equals(day_array[(i + 1)%7])) && (itemsChecked[i])) {
                                                int y1 = date_identifier1(ret_date, eve_date);
                                                if (y1 == 2) {

                                                    time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                    //both occur on same time
                                                    if (time_iden2 == 1) {
                                                        overlap = 1;
                                                        break;
                                                    }
                                                }
                                            }

                                        }
                                        if (overlap == 1)
                                            break;

                                    }
                                }
                                //for new events without repeat
                                else {
                                    //if retrieved event repeats
                                    if (database_rep == 1) {
                                        for (int i = 0; i <= 6; i++) {
                                            //goal contains the day of the event to be created
                                            //if goal equals ith day and at that day existing event is also repeated
                                            if ((goal.equals(day_array[i])) && (ret_days[i] == 1)) {
                                                int date_clash = date_identifier1(eve_date, ret_date);
                                                //if event to be created occurs on a date after the starting date of an existing event
                                                if (date_clash == 2) {
                                                    time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                    time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                    //both occur on same time
                                                    if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                                        overlap = 1;
                                                        break;
                                                    }
                                                }

                                            } else if ((goal.equals(day_array[i])) && (ret_days[(i + 1)%7] == 1)) {
                                                int date_clash = date_identifier1(eve_date2, ret_date);
                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                if (date_clash == 2) {
                                                    if (time_iden2 == 1) {
                                                        overlap = 1;
                                                        break;
                                                    }
                                                }
                                            }


                                        }
                                        if (overlap == 1)
                                            break;
                                    }
                                    //if retrieved event does not repeat
                                    else {
                                        int new_date;
                                        int new_month;
                                        int new_year;
                                        int ex_date;
                                        int ex_month;
                                        int ex_year;
                                        new_date = (eve_date.charAt(0) - '0') * 10 + (eve_date.charAt(1) - '0');
                                        new_month = (eve_date.charAt(3) - '0') * 10 + (eve_date.charAt(4) - '0');
                                        new_year = (eve_date.charAt(6) - '0') * 1000 + (eve_date.charAt(7) - '0') * 100 + (eve_date.charAt(8) - '0') * 10 + (eve_date.charAt(9) - '0');
                                        ex_date = (ret_date.charAt(0) - '0') * 10 + (ret_date.charAt(1) - '0');
                                        ex_month = (ret_date.charAt(3) - '0') * 10 + (ret_date.charAt(4) - '0');
                                        ex_year = (ret_date.charAt(6) - '0') * 1000 + (ret_date.charAt(7) - '0') * 100 + (ret_date.charAt(8) - '0') * 10 + (ret_date.charAt(9) - '0');
                                        if ((new_month == ex_month) && (new_year == ex_year)) {
                                            if (new_date - ex_date == 0) {
                                                overlap = 1;
                                                break;
                                            } else if (new_date - ex_date == -1) {
                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                //both occur on same time
                                                if (time_iden2 == 1) {
                                                    overlap = 1;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }


                        //if next_day=0 for current event
                        else {

                            if (ret_next_day == 1) {
                                //for new events with repetition
                                if (add == 1) {
                                    //if retrieved event repeats
                                    if (database_rep == 1) {

                                        for (int i = 0; i <= 6; i++) {
                                            //if on any day both existing event and event to be created occur
                                            if ((itemsChecked[i]) && (ret_days[i] == 1)) {

                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                if (time_iden2 == 1) {
                                                    overlap = 1;
                                                    break;
                                                }

                                            } else if ((itemsChecked[i]) && (ret_days[(i+6)%7] == 1)) {
                                                time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                if (time_iden1 == 1) {
                                                    overlap = 1;
                                                    break;
                                                }
                                            }
                                        }
                                        if (overlap == 1)
                                            break;
                                    }
                                    //if retrieved event does not repeat
                                    else {
                                        Date date2 = new Date(database_Year, database_Month, database_Date, 0, 0);
                                        //date2 contains the date at which the event in database is supposed to start

                                        SimpleDateFormat outFormat1 = new SimpleDateFormat("EEEE");
                                        //for finding day at date2
                                        String goal1 = outFormat1.format(date2);


                                        for (int i = 0; i <= 6; i++) {
                                            if ((goal1.equals(day_array[i])) && (itemsChecked[i])) {

                                                int y1 = date_identifier1(ret_date, eve_date);
                                                if (y1 == 2) {

                                                    time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                    //both occur on same time
                                                    if ( time_iden2 == 1) {
                                                        overlap = 1;
                                                        break;
                                                    }

                                                }
                                            } else {
                                                if(i!=0) {
                                                    if ((goal1.equals(day_array[i - 1])) && (itemsChecked[i])) {

                                                        int y1 = date_identifier1(ret_date, eve_date1);
                                                        if (y1 == 2) {
                                                            time_iden1 = time_identifier1(start_strtime, ret_endtime);

                                                            //both occur on same time
                                                            if (time_iden1 == 1) {
                                                                overlap = 1;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    if ((goal1.equals(day_array[6])) && (itemsChecked[i])) {

                                                        int y1 = date_identifier1(ret_date, eve_date1);
                                                        if (y1 == 2) {
                                                            time_iden1 = time_identifier1(start_strtime, ret_endtime);

                                                            //both occur on same time
                                                            if (time_iden1 == 1) {
                                                                overlap = 1;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (overlap == 1)
                                            break;

                                    }
                                }
                                //for new events without repeat
                                else {
                                    //if retrieved event repeats
                                    if (database_rep == 1) {
                                        for (int i = 0; i <= 6; i++) {
                                            //goal contains the day of the event to be created
                                            //if goal equals ith day and at that day existing event is also repeated
                                            if ((goal.equals(day_array[i])) && (ret_days[i] == 1)) {
                                                int date_clash = date_identifier1(eve_date, ret_date);
                                                //if event to be created occurs on a date after the starting date of an existing event
                                                if (date_clash == 2) {
                                                   // time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                    time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                    //both occur on same time
                                                    if ( time_iden2 == 1) {
                                                        overlap = 1;
                                                        break;
                                                    }
                                                }

                                            } else if ((goal.equals(day_array[i])) && (ret_days[(i+6)%7] == 1)) {
                                                int date_clash = date_identifier1(eve_date, ret_date);
                                                time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                if (date_clash == 2) {
                                                    if (time_iden1 == 1) {
                                                        overlap = 1;
                                                        break;
                                                    }
                                                }
                                            }

                                        }
                                        if (overlap == 1)
                                            break;
                                    }
                                    //if retrieved event does not repeat
                                    else {
                                        int new_date;
                                        int new_month;
                                        int new_year;
                                        int ex_date;
                                        int ex_month;
                                        int ex_year;
                                        new_date = (eve_date.charAt(0) - '0') * 10 + (eve_date.charAt(1) - '0');
                                        new_month = (eve_date.charAt(3) - '0') * 10 + (eve_date.charAt(4) - '0');
                                        new_year = (eve_date.charAt(6) - '0') * 1000 + (eve_date.charAt(7) - '0') * 100 + (eve_date.charAt(8) - '0') * 10 + (eve_date.charAt(9) - '0');
                                        ex_date = (ret_date.charAt(0) - '0') * 10 + (ret_date.charAt(1) - '0');
                                        ex_month = (ret_date.charAt(3) - '0') * 10 + (ret_date.charAt(4) - '0');
                                        ex_year = (ret_date.charAt(6) - '0') * 1000 + (ret_date.charAt(7) - '0') * 100 + (ret_date.charAt(8) - '0') * 10 + (ret_date.charAt(9) - '0');
                                        if ((new_month == ex_month) && (new_year == ex_year)) {
                                            if (new_date - ex_date == 0) {
                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                //both occur on same time
                                                if (time_iden2 == 1) {
                                                    overlap = 1;
                                                    break;
                                                }

                                            } else if (new_date - ex_date == 1) {
                                                time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                //both occur on same time
                                                if (time_iden1 == 1) {
                                                    overlap = 1;
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                }
                            }


                            //if nextday=0 for existing event
                            else {

                                //for new events with repetition
                                if (add == 1) {
                                    //if retrieved event repeats
                                    if (database_rep == 1) {
                                        for (int i = 0; i <= 6; i++) {
                                            //if on any day both existing event and event to be created occur
                                            if ((itemsChecked[i]) && (ret_days[i] == 1))
                                            {
                                                time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                //if(both clash acc to time)
                                                if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                                    overlap = 1;
                                                    break;
                                                }
                                            }
                                        }
                                        if (overlap == 1)
                                            break;
                                    }
                                    //if retrieved event does not repeat
                                    else {
                                        Date date2 = new Date(database_Year, database_Month, database_Date, 0, 0);
                                        //date2 contains the date at which the event in database is supposed to start

                                        SimpleDateFormat outFormat1 = new SimpleDateFormat("EEEE");
                                        //for finding day at date2
                                        String goal1 = outFormat1.format(date2);

                                        for (int i = 0; i <= 6; i++) {
                                            if ((goal1.equals(day_array[i])) && (itemsChecked[i])) {

                                                int y1 = date_identifier1(ret_date, eve_date);
                                                if (y1 == 2) {
                                                    time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                    time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                    //both occur on same time
                                                    if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                                        overlap = 1;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (overlap == 1)
                                            break;
                                    }
                                }
                                //for new events without repeat
                                else {
                                    //if retrieved event repeats
                                    if (database_rep == 1) {
                                        for (int i = 0; i <= 6; i++) {
                                            //goal contains the day of the event to be created
                                            //if goal equals ith day and at that day existing event is also repeated
                                            if ((goal.equals(day_array[i])) && (ret_days[i] == 1)) {
                                                int date_clash = date_identifier1(eve_date, ret_date);
                                                //if event to be created occurs on a date after the starting date of an existing event
                                                if (date_clash == 2) {
                                                    time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                                    time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                                    //both occur on same time
                                                    if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                                        overlap = 1;
                                                        break;
                                                    }
                                                }

                                            }
                                        }
                                        if (overlap == 1)
                                            break;
                                    }
                                    //if retrieved event does not repeat
                                    else {
                                        if (eve_date.equals(ret_date)) {
                                            time_iden1 = time_identifier1(start_strtime, ret_endtime);
                                            time_iden2 = time_identifier1(ret_starttime, end_strtime);
                                            //both occur on same time
                                            if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                                overlap = 1;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } while (c1.moveToNext());
            }





            //overlap=1 time clashing
            if (overlap == 1) {
                Toast.makeText(getBaseContext(), "event clashing with existing event '" + ret_name +"'", Toast.LENGTH_SHORT).show();
            }

            //overlap=3 means starttime>end time of the event to be created
            else if (overlap == 3) {
                Toast.makeText(getBaseContext(), "start time is greater than end time", Toast.LENGTH_SHORT).show();
            }


            //no conflict .....event can be created
            else
            {

                db.close();//database was opened somewhere above

                if((event_name.equals(null))||(event_name.equals(""))) //if event name hasn't been entered
                {
                    Toast.makeText(getBaseContext(),"please enter the name of the event",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(eve_date.equals("yo"))//if date hasn't been entered.....eve_date was initaialized to "yo"
                    {
                        Toast.makeText(getBaseContext(),"Please enter a date",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(start_time_set==0)//if stime hasn't been entered
                        {
                            Toast.makeText(getBaseContext(),"Please enter the starting time",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(end_time_set==0)//if end time wasn't entered by user
                            {
                                Toast.makeText(getBaseContext(),"Please enter the ending time",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //name description date(in string) starttime(in string) endtime(in string) day(intrger) month(integer) year(integer) start hour(integer)
                                //start minute(integer) end minute(integer) end hour(integer)
                                intent.putExtra("Name", event_name);
                                intent.putExtra("Description", desc);
                                intent.putExtra("Date", eve_date);
                                intent.putExtra("STime", start_strtime);//all details sent to settoggles
                                intent.putExtra("ETime", end_strtime);

                                intent.putExtra("int_day", day);
                                intent.putExtra("int_month", month);
                                intent.putExtra("int_year", yr);
                                intent.putExtra("int_shour", shour);
                                intent.putExtra("int_ehour", ehour);
                                intent.putExtra("int_sminute", sminute);
                                intent.putExtra("int_eminute", eminute);
                                intent.putExtra("next_day", next_day);
                                intent.putExtra("edit_date_time", edit_date_time);



                                if (flag == 1)//if event was edited then send the other details for fields on toggles page too
                                {
                                    intent.putExtra("bluetooth", bluetooth);
                                    intent.putExtra("wifi", wifi1);//these values have been obtained from database
                                    intent.putExtra("mobile_data", mobile_data);
                                    intent.putExtra("profile", profile_status);
                                }



                                //to send if any day was checked in repeat dialog box
                                //previous_items checked[i]=1 means that day no i was checked
                                //previous items is sent to set toggles
                                if (itemsChecked[0])
                                    intent.putExtra("int_mon", 1);
                                else
                                    intent.putExtra("int_mon", 0);
                                if (itemsChecked[1])
                                    intent.putExtra("int_tue", 1);
                                else
                                    intent.putExtra("int_tue", 0);
                                if (itemsChecked[2])
                                    intent.putExtra("int_wed", 1);
                                else
                                    intent.putExtra("int_wed", 0);
                                if (itemsChecked[3])
                                    intent.putExtra("int_thu", 1);
                                else
                                    intent.putExtra("int_thu", 0);
                                if (itemsChecked[4])
                                    intent.putExtra("int_fri", 1);
                                else
                                    intent.putExtra("int_fri", 0);
                                if (itemsChecked[5])
                                    intent.putExtra("int_sat", 1);
                                else
                                    intent.putExtra("int_sat", 0);
                                if (itemsChecked[6])
                                    intent.putExtra("int_sun", 1);
                                else
                                    intent.putExtra("int_sun", 0);


                                //add is sent to settoggles
                                intent.putExtra("add", add);
                                //flag=1 means that edit event is to be performed
                                //flag=0 means that new event is to be created
                                if (flag == 1)
                                    intent.putExtra("flag", 1);
                                else
                                    intent.putExtra("flag", 0);





                                Calendar calNow = Calendar.getInstance();
                                Calendar calSet = (Calendar) calNow.clone();
                                //calset contains the calender instance of the time when event should start
                                calSet.set(Calendar.YEAR, yr);
                                calSet.set(Calendar.MONTH, month);
                                calSet.set(Calendar.DAY_OF_MONTH, day);
                                calSet.set(Calendar.HOUR_OF_DAY, shour);
                                calSet.set(Calendar.MINUTE, sminute);
                                calSet.set(Calendar.SECOND, 0);
                                calSet.set(Calendar.MILLISECOND, 0);




                                    //if the event start time has not yet passed

                                    //if current year is smaller than start time year
                                    if (calNow.get(Calendar.YEAR) < calSet.get(Calendar.YEAR))
                                    {
                                        //settoggles intent is called
                                        startActivity(intent);
                                    }
                                    //if current year is same as start time year
                                    else if (calNow.get(Calendar.YEAR) == calSet.get(Calendar.YEAR))
                                    {
                                        if (calNow.get(Calendar.MONTH) < calSet.get(Calendar.MONTH))
                                        {
                                            //settoggles intent is called
                                            startActivity(intent);
                                        }
                                        else if (calNow.get(Calendar.MONTH) == calSet.get(Calendar.MONTH))
                                        {
                                            if (calNow.get(Calendar.DAY_OF_MONTH) < calSet.get(Calendar.DAY_OF_MONTH))
                                            {
                                                //settoggles intent is called
                                                startActivity(intent);
                                            }
                                            else if (calNow.get(Calendar.DAY_OF_MONTH) == calSet.get(Calendar.DAY_OF_MONTH))
                                            {
                                                if (calNow.get(Calendar.HOUR_OF_DAY) < calSet.get(Calendar.HOUR_OF_DAY))
                                                {
                                                    //settoggles intent is called
                                                    startActivity(intent);
                                                }
                                                else if (calNow.get(Calendar.HOUR_OF_DAY) == calSet.get(Calendar.HOUR_OF_DAY))
                                                {
                                                    if (calNow.get(Calendar.MINUTE) < calSet.get(Calendar.MINUTE))
                                                    {
                                                        //settoggles intent is called
                                                        startActivity(intent);
                                                    }
                                                    else if (calNow.get(Calendar.MINUTE) == calSet.get(Calendar.MINUTE))
                                                    {
                                                        //settoggles intent is called
                                                        startActivity(intent);
                                                    }
                                                    else
                                                        Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                    Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();



                            }
                        }
                    }
                }
            }
    }













    //returns 1 if time1<time2
    //returns 2 if time1>time2
    public int time_identifier1(String time1,String time2) {

        int asci1 = 0, asci2 = 0, as;
        int i;
        for (i = 0; i <= 4; i++)
        {
            //ch gets the char at position i in string time1
            char ch = time1.charAt(i);
            if(ch!=':')
            {
                as = (int) ch;
                as = as - 48;
                int j;
                j = (int) Math.pow(10, 4 - i);
                as = as * j;
                asci1 = asci1 + as;
            }
        }
        for (i = 0; i <= 4; i++)
        {

            char ch = time2.charAt(i);
            if(ch!=':')
            {
                as = (int) ch;
                as = as - 48;
                int j;
                j = (int) Math.pow(10, 4 - i);
                as = as * j;
                asci2 = asci2 + as;
            }
        }
        //asci1 contains the polynomial hashing value of time1 string
        //asci2 contains the polynomial hashing value of time2 string
        if(asci1<asci2)
            return 1;
        else
            return 2;



    }





    //returns 1 if date1<date2
    //returns2 if date1>date2
    public int date_identifier1(String date1,String date2) {

        int asci1 = 0, asci2 = 0, as;
        int i;
        for (i = 6; i <= 9; i++)
        {

            char ch = date1.charAt(i);

            as = (int) ch;
            as = as - 48;
            int j;
            j = (int) Math.pow(10, 14 - i);
            as = as * j;
            asci1 = asci1 + as;

        }


        char ch = date1.charAt(3);

        as = (int) ch;
        as = as - 48;
        int j;
        j = (int) Math.pow(10, 3);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(4);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 2);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(0);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 1);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(1);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 0);
        as = as * j;
        asci1 = asci1 + as;

        for (i = 6; i <= 9; i++)
        {

            ch = date2.charAt(i);

            as = (int) ch;
            as = as - 48;

            j = (int) Math.pow(10, 14 - i);
            as = as * j;
            asci2 = asci2 + as;

        }


        ch = date2.charAt(3);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 3);
        as = as * j;
        asci2 = asci2 + as;

        ch = date2.charAt(4);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 2);
        as = as * j;
        asci2 = asci2+ as;

        ch = date2.charAt(0);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 1);
        as = as * j;
        asci2 = asci2 + as;

        ch = date2.charAt(1);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 0);
        as = as * j;
        asci2 = asci2 + as;

        if(asci1<asci2)
            return 1;
        else
            return 2;

    }





    //opens the dialog box for setting date of the event
    public void setdate(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_date.getWindowToken(), 0);
        showDialog(DATE_DIALOG_ID);

    }

    //opens the dialog box for setting start time of the event
    public void setstart_time(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_stime.getWindowToken(), 0);
        showDialog(TIME_DIALOG_ID);
    }

    //opens the dialog box for setting end time of the event
    public void setend_time(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_etime.getWindowToken(), 0);
        showDialog(2);
    }

    public void set_repeat(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_repeat.getWindowToken(), 0);
        showDialog(9);
    }


    //called when dialog box is created
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id) {
            //shows dialog box for setting start time
            case 0:
                return new TimePickerDialog(
                        this, mTimeSetListener, shour, sminute, false);
            //shows dialog box for date
            case 1:
                return new DatePickerDialog(
                        this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        yr= year;
                        month= monthOfYear;
                        day = dayOfMonth;
                        //way of formatting date doesn't create any problem
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = new Date(yr-1900,month,day,0,0);
                        Date date2=new Date(yr-1900,month,day-1,0,0);
                        Date date3= new Date(yr-1900,month,day+1,0,0);
                        //eve date1 and eve_date2 is for checking overlapping
                        eve_date1=dateFormat.format(date2);
                        eve_date = dateFormat.format(date);
                        eve_date2=dateFormat.format(date3);
                        editText_date.setText(eve_date);


                    }
                }
                            ,yr,month,day);

            //shows dialog box for end time
            case 2: return  new TimePickerDialog(
                    this, m1TimeSetListener, ehour, eminute , false);


            case 3: //dialog box for next_day warning
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle("Warning")
                        .setMessage("End Time is smaller than starting time....Do you want the event to end on next day??")

                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        next_day=0;
                                        next.setText(" ");

                                        end_strtime="24:00";


                                    }
                                }
                        )

                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        next.setText("+1");
                                        next_day=1;

                                    }
                                }
                        ).create();

            //shows dialog box for repeat
            case 9: return new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("Select Days")

                   /* .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {


                                }
                            }
                    )*/


                    .setMultiChoiceItems(items, itemsChecked,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which, boolean isChecked) {
                                    //if a  day is selected then set itemchecked true for that day
                                    if(isChecked) {
                                        itemsChecked[which] = true;
                                    }
                                    else
                                    {
                                        itemsChecked[which]=false;
                                    }
                                    //shows the day which is checked or unchecked

                                }
                            }
                    )


                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    //when ok button is clicked item checked is copied into previous itemchecked
                                    //so previous item checked contains the final selected items

                                    String repeat_str="";
                                    for(int i=0;i<items.length;i++)
                                    {
                                        if(itemsChecked[i]==true)
                                        {
                                            repeat_str=repeat_str.concat(repeat_days[i]);
                                        }
                                    }

                                    editText_repeat.setText(repeat_str);

                                }
                            }
                    ).create();
        }
        return null;
    }




    //onclick listener for start time setting dialog window
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener()
            {
                public void onTimeSet(
                        TimePicker view, int hourOfDay, int minuteOfHour)
                {
                    //shour contains hour at which event is to be started
                    shour = hourOfDay;
                    //sminute contains minute at which event should start
                    sminute = minuteOfHour;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date(0,0,0, shour, sminute);
                    //start_time contains start time in string format
                     start_strtime = timeFormat.format(date);
                    start_time_set=1;

                    int y;
                    //checks if starttime is greater than end time of the event we are creating
                    y=time_identifier1(start_strtime,end_strtime);

                    if(y==2)
                    {
                        showDialog(3);//showing warning message that etime<stime
                    }
                    else
                    {
                        next.setText(" ");//"+1" doesn't appears
                        next_day=0;//event doesn't goes to next day
                    }

                    editText_stime.setText(start_strtime);

                }
            };



    //on click for setting end time
    //works in same way as onclick for start time
    private TimePickerDialog.OnTimeSetListener m1TimeSetListener =
            new TimePickerDialog.OnTimeSetListener()
            {
                public void onTimeSet(
                        TimePicker view, int hourOfDay, int minuteOfHour)
                {
                    ehour = hourOfDay;
                    eminute = minuteOfHour;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date(0,0,0, ehour, eminute);
                    end_strtime = timeFormat.format(date);
                    end_time_set=1;
                    int y;
                    //checks if starttime is greater than end time of the event we are creating
                    y=time_identifier1(start_strtime,end_strtime);

                    if(y==2)
                    {
                        showDialog(3);//warning message showing etime<stime
                    }
                    else
                    {
                        next.setText(" ");//"+1" doesn't appears
                        next_day=0;//event doesn't goes to next day

                    }
                    editText_etime.setText(end_strtime);

                }
            };

}
