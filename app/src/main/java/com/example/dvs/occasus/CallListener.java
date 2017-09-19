package com.example.dvs.occasus;



import android.database.Cursor;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;



public class CallListener extends BroadcastReceiver {

    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub

        if(context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("event_running",0)==1)//0 is the default value here
            //if in shared preferences, field-> event_running = 1 ( event is running....i.e. it wasn't deleted while it was running)
        {

            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//getting audio manager

            //Creating an object of DBAdapterException Class
            DBAdapterException db = new DBAdapterException(context);

            //Creating an object of DBAdapterSms Class
            DBAdapterSms db1 = new DBAdapterSms(context);

            // get the phone number
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String incomingNumber1;


            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                // This code will execute when the phone has an incoming call
                db.open();
                //j: flag variable to prevent sending message two times
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("j", "0").commit();
                //i: Flag variable to enter RINGING_STATE once
                String i = PreferenceManager.getDefaultSharedPreferences(context).getString("i", "0");
                if (i.equals("0"))
                {
                    switch (am.getRingerMode())
                    {
                        case AudioManager.RINGER_MODE_SILENT:
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("p", "1").commit(); //p: variable to store phone state
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("p", "2").commit();
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("p", "3").commit();
                            break;
                    }
                }

                //Taking the last 10 digits of the incoming number
                incomingNumber1 = incomingNumber.substring(3);


                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("incoming number actual", incomingNumber).commit();

                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("incoming no", incomingNumber1).commit();

                Cursor c = db.getAllContacts();
                if (c.moveToFirst())
                {
                    do
                    {

                        if (c.getString(c.getColumnIndex("PhoneNo")).substring(c.getString(c.getColumnIndex("PhoneNo")).length() - 10).equals(incomingNumber1))
                        {
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("i", "1").commit();
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            int streamMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_RING); //Setting max volume
                            am.setStreamVolume(AudioManager.STREAM_RING, streamMaxVolume, AudioManager.FLAG_ALLOW_RINGER_MODES | AudioManager.FLAG_PLAY_SOUND);

                            break;
                        }
                    } while (c.moveToNext());
                }
                db.close();



            }
            else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_IDLE)
                    || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                // This code will execute when the call is disconnected

                db1.open();

                String p = PreferenceManager.getDefaultSharedPreferences(context).getString("p", "0");
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("i", "0").commit();

                if (p.equals("1"))
                {
                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
                else if (p.equals("2"))
                {
                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
                else if (p.equals("3"))
                {
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }

                String j = PreferenceManager.getDefaultSharedPreferences(context).getString("j", "0");
                String number = PreferenceManager.getDefaultSharedPreferences(context).getString("incoming no", "");
                String actualNo = PreferenceManager.getDefaultSharedPreferences(context).getString("incoming number actual", "");

                Cursor c1 = db1.getAllContacts();
                if (c1.moveToFirst())
                {
                    do
                    {
                        if (c1.getString(c1.getColumnIndex("PhoneNo")).substring(c1.getString(c1.getColumnIndex("PhoneNo")).length() - 10).equals(number) && j.equals("0"))
                        {
                            String s = PreferenceManager.getDefaultSharedPreferences(context).getString("message_text", "I am busy, call me later.");
                            Toast.makeText(context, "Message Sent to "+actualNo, Toast.LENGTH_SHORT).show();

                            try
                            {
                                //sending text message
                                SmsManager.getDefault().sendTextMessage(actualNo, null, s, null, null);
                            }
                            catch (Exception e)
                            {
                                String error = e.getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("j", "1").commit();
                        }
                    } while (c1.moveToNext());
                }

                db1.close();
            }

        }






    }
}


