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
import android.widget.Toast;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;


public class deactivate_event extends BroadcastReceiver {

    public static final String MyPREFERENCES = "MyPrefs";
    int bluetooth_state;
    int wifi_state;
    int mobiledata_state;
    int profile_state;
    Calendar calSet1;
    Context context1;

    String database_name;

    int notificationID = 1;

    int pending_key;
    String unique_key;
    public void onReceive(final Context context, Intent intent)
    {


        context1=context;


        pending_key=intent.getIntExtra("pending_key", 0);//id of event whose pending intent is being called



        DBAdapter db= new DBAdapter(context);
        db.open();
        Cursor c=null;


        Calendar calNow = Calendar.getInstance();
        calSet1 = (Calendar) calNow.clone();



        int run= context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("event_running", 6);
        //checks if the event is still running
        //i.e. it wasn't deleted while it was running
        unique_key=intent.getStringExtra("unique_key");

        try
        {
            c = db.getEventDetail(unique_key);//check if the event still is present in the database
            // i.e. it wasn't deleted before even the event started


            if((c.moveToFirst())&&(c!=null)&&(run==1))//if event is in database and it is running
            {
                database_name=c.getString(c.getColumnIndex("event_name"));


                displayNotification();//display notification that event has ended


                final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);//getting media player
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        // TODO Auto-generated method stub
                        mp.reset();
                        mp.release();
                        mp = null;
                    }
                });
                mp.start();



                //getting the system state that was just before the event started from shared preferences
                bluetooth_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("bluetooth_state", 6);
                wifi_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("wifi_state", 6);
                mobiledata_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("mobiledata_state", 6);
                profile_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);
                //6 is the default value






                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetooth_state == 1) //if bluetooth was on before start of event
                {
                    mBluetoothAdapter.enable();//turn on bluetooth
                }
                else //if bluetooth was on before start of event
                {
                    mBluetoothAdapter.disable(); //turn off bluetooth
                }





                WifiManager wifi;
                wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifi_state == 1) //if wifi was on before start of event
                {
                    wifi.setWifiEnabled(true);//turn on wifi
                }
                else //if wifi was off before start of event
                {
                    wifi.setWifiEnabled(false);//turn off wifi
                }





                final ConnectivityManager conman =
                        (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                if (mobiledata_state == 1) //if mobile data was on before start of event
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
                else//if mobile data was off before start of event
                {
                    try {
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
                MyAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//get audio manager
                if (profile_state == 1) //if profile was silent before event started
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent
                }
                else if (profile_state == 3) //if profile was ring before event started
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//set phone to ring
                }
                else//if profile was vibrate before event started
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);//set phone to vibrate
                }





                SharedPreferences.Editor editor;
                editor = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).edit();
                //editor declared for shared preferences
                editor.putInt("event_running", 0);//set event_running field to 0 in shared prefernces
                //coz event is not running now
                editor.commit();



                int rep = intent.getIntExtra("rep", 0);//rep obtained from settoggles class
                //indicates where event repeats or not
                if (rep == 1) //if repeats
                {
                    calSet1.add(Calendar.DAY_OF_MONTH, 7);
                    endeve(calSet1);//set ending pending intent for next week
                }
                else//if doesn't repeats
                {
                    db.deleteEvent(unique_key);//delete the event from database
                }
            }
        }
        catch (Exception e)
        {

        }
    }





    //to display a notification
    public void displayNotification()
    {
        Intent i = new Intent(context1, NotificationView.class);
        i.putExtra("notificationID",notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);

        Notification notif = new Notification(
                R.drawable.ic_launcher,"Event "+database_name+" ends",System.currentTimeMillis()
        );

        CharSequence from = "SystemAlarm";
        CharSequence message = "Event "+database_name+" ends";

        notif.setLatestEventInfo(context1,from,message,pendingIntent);
        nm.notify(notificationID,notif);
    }



    //creates pending intent for end time for next week
    private void endeve(Calendar targetCal){



        Intent intent = new Intent(context1, deactivate_event.class);
        intent.putExtra("pending_key",pending_key);
        intent.putExtra("unique_key",unique_key);
        intent.putExtra("rep",1);




        PendingIntent pendingIntent = PendingIntent.getBroadcast(context1,pending_key , intent, 0);
        AlarmManager alarmManager = (AlarmManager)context1.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

    }


}
