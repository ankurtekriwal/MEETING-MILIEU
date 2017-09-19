package com.example.dvs.occasus;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class SetToggles extends ActionBarActivity
{

    String bluetooth="no",wifi1="no",mobile_data="no";
    int l;
    String profile="ring";
    int check;
    CharSequence[] items = { " Silent", "Ring", "Vibrate"};
    AudioManager am;
    int flag;
    String profile_status;
    String stime;
    String date;
    String name;
    Integer day;
    Integer month;
    Integer year;
    Integer shour;
    Integer ehour;
    Integer sminute;
    Integer eminute;
    Integer add;
    Integer[] days={0,0,0,0,0,0,0};
    int start_time_pass=0;
    Button b1;
    String unique_key;
    int pending_key;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_toggles);

        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//to hide backbuttton in action bar


        Intent intent = getIntent();
        //initializing audio manager




        b1=(Button)findViewById(R.id.profile_button);
        b1.setText("Ring");



        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);//getting audio manager




        //flag received from create event
        //flag=0 means that new event is getting created
        flag=intent.getIntExtra("flag",0);
        //flag=1 means that existing event is being edited
        if(flag==1)
        {

            ToggleButton b;
            b=(ToggleButton) findViewById(R.id.bluetooth_button);

            bluetooth=intent.getStringExtra("bluetooth");
            //status equals yes means bluetooth was supposed to be on so toggle button is turned on by b.setchecked
            if(bluetooth.equals("yes"))
            {
                b.setChecked(true);
            }
            //status equals no means bluetooth was supposed to be off so toggle button is turned off by b.setchecked
            else
            {
                b.setChecked(false);
            }
            b = (ToggleButton) findViewById(R.id.wifi_button);



            //getting earlier wifi toggle button state

            wifi1= intent.getStringExtra("wifi");
            //status equals yes means wifi was supposed to be on so toggle button is turned on by b.setchecked
            if(wifi1.equals("yes"))
            {
                b.setChecked(true);
            }
            //status equals no means wifi was supposed to be off so toggle button is turned off by b.setchecked
            else
            {
                b.setChecked(false);
            }
            b = (ToggleButton) findViewById(R.id.mobiledata_button);



            mobile_data= intent.getStringExtra("mobile_data");
            if(mobile_data.equals("yes"))
            {
                b.setChecked(true);
            }
            else
            {
                b.setChecked(false);
            }




            profile_status= intent.getStringExtra("profile");
            //profile and profile_status contains the radiobutton that was checked in profile daialog box when event was created earlier
            b1.setText(profile_status);
            profile= profile_status;


        }
    }





    //on click listener for bluetooth toggle switch
    public void bluetooth_settings(View view)
    {

        boolean on = ((ToggleButton) view).isChecked();
        //on==true means that bluetooth toggle button is set to on
        if (on)
        {
            //bluetooth=yes means that bluetooth toggle switch is set to yes
            bluetooth="yes";
         }
        else
        {
            //bluetooth=no means that bluetooth toggle switch is set to no
            bluetooth="no";
        }
    }



    //onclick listener for profile button
    public void silent_settings(View view)
    {
       //shows the dialog box containing profile options
        showDialog(0);
    }





    //onclick listener for mobiledata toggle button
    public void mobiledata_settings(View view){

        boolean on = ((ToggleButton) view).isChecked();

        if(on)
        {
            mobile_data= "yes";
        }
        else
        {
            mobile_data="no";
        }
    }





    //called by showdialog automatically
        @Override
        protected Dialog onCreateDialog(int id) {

            check=-1;

            //if existing event is getting edited then what was the radiobutton which was checked previous time in profile dailog box
            if(flag==1)
            {
                if(profile_status.equals("silent"))
                    check=0;
                else if(profile_status.equals("vibrate"))
                    check=2;
                else
                    check=1;

            }



            switch (id) {
                case 0:
                    return new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_launcher)
                            .setTitle("Choose a profile")

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    }
                            )


                                .setSingleChoiceItems(items, check,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //stores which radiobutton was clicked in a variable l
                                                setvalue(which);//stores the profile which was chosen in a seperate variable
                                                //setvallue function is defined below in this class
                                            }
                                        }
                                )

                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            //if ok is clicked then if l=0 then silent radiobutton was clicked
                                            //if l=1 then ring radiobutton was clicked
                                            //if l=2 then vibrate radiobutton was clicked
                                            if (l == 0)
                                            {
                                                profile = "silent";
                                                check=0;
                                                b1.setText("Silent");
                                            }
                                            else if (l == 1)
                                            {
                                                profile = "ring";
                                                //may be there is no need to set check to some value
                                                check=1;
                                                b1.setText("Ring");
                                            }
                                            else if (l == 2)
                                            {
                                                profile = "vibrate";
                                               check=2;
                                                b1.setText("Vibrate");
                                            }
                                        }
                                    }
                            ).create();
            }
            return null;
    }


    //sets l= the radiobutton which was clicked
void setvalue(int y)
{
    l=y;
}



    //onclick listener for wifi toggle switch
    public void wifi_settings(View view)
    {
        boolean on = ((ToggleButton) view).isChecked();
        //on==true means wifi toggle button is set on
        if(on)
        {
            wifi1="yes";
        }
        else
        {
            wifi1="no";
        }
    }




    //onclick for save button
    public void save_event(View view)
    {
        //name description date(string) start time(string) end time(sting) add(int) retrieved from create event class
        //these values are inserted into database later in save_event method
         name = getIntent().getStringExtra("Name");
        String desc = getIntent().getStringExtra("Description");
         date = getIntent().getStringExtra("Date");
         stime = getIntent().getStringExtra("STime");
        String etime = getIntent().getStringExtra("ETime");

        add=getIntent().getIntExtra("add",1);
        //add=1 means that event repeats


        //day(int) month(int)year(int) shour(int) ehour(int) sminute(int) eminute(int) retrieved from create_event class
         day= getIntent().getIntExtra("int_day",1);
         month= getIntent().getIntExtra("int_month",1);
         year= getIntent().getIntExtra("int_year",1);
         shour= getIntent().getIntExtra("int_shour",1);
         ehour= getIntent().getIntExtra("int_ehour",1);
         sminute= getIntent().getIntExtra("int_sminute",1);
         eminute= getIntent().getIntExtra("int_eminute",1);
         int next_day=getIntent().getIntExtra("next_day", 0);
         //String req_name=getIntent().getStringExtra("req_name");


        //days[i]=1 if ith day was checked in repeat dialog box 0n previous screen
        days[0] = getIntent().getIntExtra("int_mon", 1);
        days[1] = getIntent().getIntExtra("int_tue", 1);
        days[2] = getIntent().getIntExtra("int_wed", 1);
        days[3] = getIntent().getIntExtra("int_thu", 1);
        days[4] = getIntent().getIntExtra("int_fri", 1);
        days[5] = getIntent().getIntExtra("int_sat", 1);
        days[6] = getIntent().getIntExtra("int_sun", 1);


        unique_key=date.concat(stime);




        DBAdapter db=new DBAdapter(this);
        db.open();
        if(flag==1)//if event is getting edit
        {
            String edit_date_time= getIntent().getStringExtra("edit_date_time");
            db.deleteEvent(edit_date_time);//delete the older version
           db.open();
           db.insertevent(name, desc, date,unique_key, stime, etime, next_day, bluetooth, wifi1, profile, mobile_data, days[0], days[1], days[2], days[3], days[4], days[5], days[6]);
           //inserts the new version
           Toast.makeText(getBaseContext(), "event successfully edited", Toast.LENGTH_SHORT).show();
        }
        else
        {
                    //event details inserted into database
            try
            {
                db.insertevent(name, desc, date,unique_key, stime, etime, next_day, bluetooth, wifi1, profile, mobile_data, days[0], days[1], days[2], days[3], days[4], days[5], days[6]);
                Toast.makeText(getBaseContext(), "event successfully created", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                String ec = e.getMessage();
                Toast.makeText(getBaseContext(), ec, Toast.LENGTH_SHORT).show();
            }
        }




        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        //calset contains the calender instance of the time when event should start
        calSet.set(Calendar.YEAR, year);
        calSet.set(Calendar.MONTH, month);
        calSet.set(Calendar.DAY_OF_MONTH, day);
        calSet.set(Calendar.HOUR_OF_DAY, shour);
        calSet.set(Calendar.MINUTE, sminute);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);




        Calendar calSet1 = (Calendar) calNow.clone();

        //calset1 contains the calender instance of the time when event should end
        calSet1.set(Calendar.YEAR, year);
        calSet1.set(Calendar.MONTH, month);
        if(next_day==1)//if event goes upto next day
            calSet1.set(Calendar.DAY_OF_MONTH, day+1);//create ending pending intent for one day after the enter date
        else//if event doesn't goes upto next day
            calSet1.set(Calendar.DAY_OF_MONTH, day);

        calSet1.set(Calendar.HOUR_OF_DAY, ehour);
        calSet1.set(Calendar.MINUTE, eminute);
        calSet1.set(Calendar.SECOND, 0);
        calSet1.set(Calendar.MILLISECOND, 0);



        setAlarm(calSet, add);//creates pending intent for start of event
        endeve(calSet1, add);//crates pending intent for end of event


        db.close();


        //screen restored to mainscreen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


    }







    //suppose event is made on saturday (25 april 2015) at 6 pm and is supposed to start at 5pm and repeat on saturdays
    //now while creating pending intent for start of event, pending intent is created with date 25 april 2015 since it is the latest saturday from 25 april 2015
    //the pending intent is supposed to work on 25 april 5 pm but it won't work coz it's 6 pm already
    //due to this the event won't even start after 7 days
    //to overcome this we check if the current time(6 pm) is greater than start time(5 pm) if the event is supposed to start today and it repeats
    //this problem never occurs if event doesn't repeats coz there we already tell the user that starting time has passed
  /*  Calendar time_passed(Calendar calSet,int rep)
    {

        Calendar calNow = Calendar.getInstance();

        //if the event start time has not yet passed
        //if current year is smaller than start time year
        if (calNow.get(Calendar.YEAR) < calSet.get(Calendar.YEAR))
        {
            return calSet;
        }
        //if current year is same as start time year
        else if (calNow.get(Calendar.YEAR) == calSet.get(Calendar.YEAR))
        {
            if (calNow.get(Calendar.MONTH) < calSet.get(Calendar.MONTH))
            {
                return calSet;
            }
            else if (calNow.get(Calendar.MONTH) == calSet.get(Calendar.MONTH))
            {
                if (calNow.get(Calendar.DAY_OF_MONTH) < calSet.get(Calendar.DAY_OF_MONTH))
                {
                    return calSet;
                }
                else if (calNow.get(Calendar.DAY_OF_MONTH) == calSet.get(Calendar.DAY_OF_MONTH))
                {
                    if (calNow.get(Calendar.HOUR_OF_DAY) < calSet.get(Calendar.HOUR_OF_DAY))
                    {
                        return calSet;
                    }
                    else if (calNow.get(Calendar.HOUR_OF_DAY) == calSet.get(Calendar.HOUR_OF_DAY))
                    {
                        if (calNow.get(Calendar.MINUTE) < calSet.get(Calendar.MINUTE))
                        {
                            return calSet;
                        }
                        else if (calNow.get(Calendar.MINUTE) == calSet.get(Calendar.MINUTE))
                        {
                            return calSet;
                        }
                        else
                        {
                            calSet.add(Calendar.DATE,7);
                            start_time_pass=1;
                        }
                    }
                    else
                    {
                        calSet.add(Calendar.DATE, 7);
                        start_time_pass=1;
                    }
                }
                else
                {
                    calSet.add(Calendar.DATE, 7);
                    start_time_pass=1;
                }
            }
            else
            {
                calSet.add(Calendar.DATE, 7);
                start_time_pass=1;
            }
        }
        else
        {
            calSet.add(Calendar.DATE, 7);
            start_time_pass=1;
        }

        return calSet;
    }

*/




    //creates pending intent for start time
    private void setAlarm(Calendar targetCal,Integer rep)
    {

        Intent intent = new Intent(getBaseContext(), Activate_event.class);


        intent.putExtra("unique_key",unique_key);
        //ret_id used as a unique key

        DBAdapter db= new DBAdapter(SetToggles.this);
        db.open();
        Cursor c=db.getEventDetail(unique_key);
        if(c.moveToFirst())
        {
            pending_key=c.getInt(c.getColumnIndex("_id"));
        }
        intent.putExtra("pending_key",pending_key);





        //if new event is repeated
        if(rep==1)
        {
            intent.putExtra("rep",rep);
            if (days[0] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                date.add(Calendar.DATE, 1);//get the date of the latest monday from date entered


                //Calendar pending_calSet = (Calendar)time_passed(targetCal,rep).clone();//"time_passed" function called

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
            }

            if (days[1] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY)
                    date.add(Calendar.DATE, 1);//get the date of the latest tuesday from date entered


               // Calendar pending_calSet = (Calendar)time_passed(targetCal,rep).clone();//"time_passed" function called

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
            }

            if (days[2] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY)
                    date.add(Calendar.DATE, 1);//get the date of the latest wednesday from date entered


                //Calendar pending_calSet = (Calendar)time_passed(targetCal,rep).clone();//"time passed" function called

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
            }

            if (days[3] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
                    date.add(Calendar.DATE, 1);//get the date of the latest thursday from date entered


               // Calendar pending_calSet = (Calendar)time_passed(targetCal,rep).clone();//"time passed" function called

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
            }

            if (days[4] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
                    date.add(Calendar.DATE, 1);//get the date of the latest friday from date entered


                //Calendar pending_calSet = (Calendar)time_passed(targetCal,rep).clone();//"time passed" function called

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
            }

            if (days[5] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                    date.add(Calendar.DATE, 1);//get the date of the latest saturday from date entered


               // Calendar pending_calSet = (Calendar)time_passed(targetCal,rep).clone();//"time passed" function called

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

            if (days[6] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                    date.add(Calendar.DATE, 1);//get the date of the latest sunday from date entered


               // Calendar pending_calSet = (Calendar)time_passed(targetCal,rep).clone();//"time passed" function called

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }
        }


        //if new event is not getting repeated
        if(rep==0)
        {
            intent.putExtra("rep",0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }
    }





    //creates pending intent for end time
    private void endeve(Calendar targetCal,int rep)
    {

        Intent intent = new Intent(getBaseContext(), deactivate_event.class);

        DBAdapter db= new DBAdapter(SetToggles.this);
        db.open();
        Cursor c=db.getEventDetail(unique_key);
        if(c.moveToFirst())
        {
            pending_key=c.getInt(c.getColumnIndex("_id"));
        }
        intent.putExtra("pending_key",pending_key);
        intent.putExtra("unique_key",unique_key);


        //if new event is repeated
        if(rep==1)
        {
            intent.putExtra("rep",rep);
            if (days[0] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                    date.add(Calendar.DATE, 1);//get the date of the latest monday from date entered


                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
            }

            if (days[1] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY)
                    date.add(Calendar.DATE, 1);//get the date of the latest tuesday from date entered



                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

            if (days[2] == 1)
            {
                GregorianCalendar date = new GregorianCalendar(year, month, day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY)
                    date.add(Calendar.DATE, 1);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }


            if (days[3] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
                    date.add(Calendar.DATE, 1);



                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }


            if (days[4] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
                    date.add(Calendar.DATE, 1);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);



            }


            if (days[5] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                    date.add(Calendar.DATE, 1);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);


            }


            if (days[6] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                    date.add(Calendar.DATE, 1);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }
        }
        if(rep==0) //if event doesn't repeat, simply create pending intent for end
        {

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pending_key, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }
    }




}



