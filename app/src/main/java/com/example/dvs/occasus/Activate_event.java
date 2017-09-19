package com.example.dvs.occasus;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;



public class Activate_event extends BroadcastReceiver {

    Context context1;

    Calendar calSet1;
    int notificationID = 1;
    String database_name;
    String unique_key;
    int pending_key;

    public static final String MyPREFERENCES = "MyPrefs";

    public void onReceive(final Context context, Intent intent)
    {

        //database for event details opened
        DBAdapter db = new DBAdapter(context);
        db.open();

        Cursor c;

        //ret_name has the name of the event whose pending intent is being executed....it is passed from settoggles class to this class
        unique_key=intent.getStringExtra("unique_key");
        pending_key=intent.getIntExtra("pending_key",0);



        try
        {

            //we check if the event still exists in database......i.e. it hasn't been deleted from database
            c = db.getEventDetail(unique_key);
            c.moveToFirst();
            //if event has been deleted then nothing happens(due to try catch) otherwise the following code is executed


            //if c=null => no event exist with same id in database or it has been deleted....so if statement is not executed
            if (c != null)
            {

                database_name = c.getString(c.getColumnIndex("event_name"));
                //cursor c stores the details of the event whose pending intent is being executed
                //database name stores the name of the event whose pending intent is being executed

                context1 = context;
                //context1 stores the context

                displayNotification();//calls displayNotification function which displays a notification indicating start of event



                //shared preferences declared
                SharedPreferences sharedpreferences;
                //shared preferences editor declared
                SharedPreferences.Editor editor;
                //initializing shared preferences
                sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();




                //getting bluetooth adapter
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter.isEnabled())//if bluetooth was enabled just before the start of event
                {
                    editor.putInt("bluetooth_state", 1);
                    //in shared preferences, field -> bluetooth state = 1....it means bluetooth was on just before the event started
                    editor.commit();//changes committed in shared preferences
                }
                else//if bluetooth was not enabled just before event started
                {
                    editor.putInt("bluetooth_state", 0);
                    //in shared preferences, field -> bluetooth state = 0....it means bluetooth was off just before the event started
                    editor.commit();//changes committed in shared preferences
                }




                //getting wifi manager
                WifiManager wifi;
                wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled())//if wifi was enabled just before event started
                {
                    editor.putInt("wifi_state", 1);
                    //in shared preferences, field -> wifi_state = 1....it means wifi was on just before start of the event
                    editor.commit();//changes committed in shared preferences
                }
                else//if wifi wasn't enabled just before start of the event
                {
                    editor.putInt("wifi_state", 0);
                    //in shared preferences field, wifi_state = 1.....it means wifi was on just before start of event
                    editor.commit();//changes committed in shared preferences

                }





                //checking if mobile data was on or off before event starts
                boolean mobileDataEnabled = false; // Assume disabled
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                try
                {
                    Class cmClass = Class.forName(cm.getClass().getName());
                    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                    method.setAccessible(true); // Make the method callable
                    // get the setting for "mobile data"
                    mobileDataEnabled = (Boolean) method.invoke(cm);
                }
                catch (Exception e)
                {
                    // Some problem accessible private API
                    // TODO do whatever error handling you want here
                }
                if (mobileDataEnabled)//if mobile data was on before event started
                {
                    editor.putInt("mobiledata_state", 1);
                    //in shared preferences, field -> mobiledata_state = 1.....means mobile data was on before start of event
                    editor.commit();//changes committed in shared preferences
                }
                else
                {
                    editor.putInt("mobiledata_state", 0);
                    //in shared preferences, field -> mobiledata_state = 0.....means mobile data was off before start of event
                    editor.commit();//changes committed in shared preferences
                }






                //getting audio manager
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                switch (am.getRingerMode())//getRingerMode returns the ringing mode currently(just before start of event)
                {

                    case AudioManager.RINGER_MODE_SILENT://if phone is on silent just before event starts
                        editor.putInt("profile_state", 1);
                        //in shared preferences, field -> profile_state = 1....means phone was on silent just before event started
                        editor.commit();//changes committed in shared preferences
                        break;

                    case AudioManager.RINGER_MODE_VIBRATE://if phone is on vibrate just before event starts
                        editor.putInt("profile_state", 2);
                        //in shared preferences, field -> profile_state = 2.....means phone was on vibrate just before event started
                        editor.commit();//changes committed in shared preferences
                        break;

                    case AudioManager.RINGER_MODE_NORMAL://if phones is on normal mode just before event starts
                        editor.putInt("profile_state", 3);
                        //in shared preferences, field -> profile_state = 3.....means phone was on normal mode just before event started
                        editor.commit();//changes committed in shared preferences
                        break;
                }






                //getting mediaplayer
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);
                //mp set to play notification.mp3(stored in res/raw)
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()//called when mp3 ends
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        // TODO Auto-generated method stub
                        mp.reset();             //reset release and setting null necessary....otherwise some problems might occur
                        mp.release();
                        mp = null;
                    }
                });
                mp.start();//mp3 starts playing indicating the start of the event







                //to set the bluetooth state when event starts
                if (c.getString(c.getColumnIndex("bluetooth")).equals("yes"))//if bluetooth is "yes" in database for the running event
                {
                    mBluetoothAdapter.enable();//turn bluetooth on
                }
                else//if bluetooth is "no" in database for the running event
                {
                    mBluetoothAdapter.disable();//turn bluetooth off
                }






                //to set the wifi state when event starts
                if (c.getString(c.getColumnIndex("wifi")).equals("yes"))//if wifi is "yes" in database for the running event
                {
                    wifi.setWifiEnabled(true);//turn wifi on
                }
                else//if wifi is "no" in database for the running event
                {
                    wifi.setWifiEnabled(false);//turn wifi off
                }






                //to set the mobile data when event starts
                final ConnectivityManager conman =
                    (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                if (c.getString(c.getColumnIndex("mobile_data")).equals("yes"))
                //if mobile data is "yes" in database for the running event
                {
                    try
                    {

                        final Class conmanClass = Class.forName(conman.getClass().getName());
                        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                        iConnectivityManagerField.setAccessible(true);
                        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                        final Class iConnectivityManagerClass =
                            Class.forName(iConnectivityManager.getClass().getName());
                        final Method setMobileDataEnabledMethod =
                            iConnectivityManagerClass
                                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                        setMobileDataEnabledMethod.setAccessible(true);
                        setMobileDataEnabledMethod.invoke(iConnectivityManager, true);//turn mobile data on

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else//if mobile data is "no" in database for the running event
                {
                    try
                    {

                        final Class conmanClass = Class.forName(conman.getClass().getName());
                        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                        iConnectivityManagerField.setAccessible(true);
                        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                        final Class iConnectivityManagerClass =
                            Class.forName(iConnectivityManager.getClass().getName());
                        final Method setMobileDataEnabledMethod =
                            iConnectivityManagerClass
                                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                        setMobileDataEnabledMethod.setAccessible(true);
                        setMobileDataEnabledMethod.invoke(iConnectivityManager, false);//turn mobile data off

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }







                AudioManager MyAudioManager;
                MyAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//getting audio manager
                if (c.getString(c.getColumnIndex("profile")).equals("silent"))//if profile is "silent" for running event in database
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent mode
                }
                else if (c.getString(c.getColumnIndex("profile")).equals("ring"))//if profile is "ring" for running event in database
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//set phone to ring mode
                }
                else//if profile is "vibrate" for running event in database
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);//set phone to vibrate mode
                }






                editor.putInt("event_running", 1);
                //in shared preferences, field -> event_running = 1....means some event is currently running
                editor.commit();//commit the changes in shared preferences


                editor.putString("running_date_time", unique_key);//in shared preferences, field -> id = ret_id
                //ret_id contains the id of the event whose pending intent is getting executed
                //ret_id is passed from settoggles class to this class
                editor.commit();//changes committed in shared preferences



                calSet1 = Calendar.getInstance();//calSet1 contains the current date and time

                int rep = intent.getIntExtra("rep", 0);//rep passed from settoggles class to this class
                if (rep == 1)//if event repeats on some day
                {
                    calSet1.add(Calendar.DAY_OF_MONTH, 7);//calSet1 set to next week to create starting pending event for next week
                    setAlarm(calSet1);//setAlarm called.....creates starting pending intent for next week
                }

            }

        }
        catch(Exception e)
        {

        }

    }



    //displays notification indicating start of the event
    public void displayNotification()
    {
        Intent i = new Intent(context1, NotificationView.class);//moves control to notificationView class
        i.putExtra("notificationID",notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);

        Notification notif = new Notification(
            R.drawable.ic_launcher,"Event "+database_name+" starts",System.currentTimeMillis()
        );

        CharSequence from = "SystemAlarm";
        CharSequence message ="Event " +database_name+" starts";//message appearing at notification

        notif.setLatestEventInfo(context1,from,message,pendingIntent);
        nm.notify(notificationID,notif);
    }




    //creates pending intent for start time
    private void setAlarm(Calendar targetCal)
    {
        Intent intent = new Intent(context1, Activate_event.class);

        intent.putExtra("unique_key",unique_key);
        intent.putExtra("pending_key",pending_key);

        //name sent to activate_event class....there it is used to retrieve all the details of the event
        intent.putExtra("rep",1);//repetion=true passed to start pending intent for next week

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, pending_key, intent, 0);//ret_id is key here
        //pending intent behaves differently for different keys.....though here key doesn't plays any role

        AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        //housekeeping stuff for the starting pending intent for next week
    }


}
