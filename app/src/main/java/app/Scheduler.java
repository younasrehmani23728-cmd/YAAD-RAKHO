package com.yaadrakho.app;

import android.app.*;
import android.content.*;
import android.os.Build;
import android.provider.Settings;
import org.json.*;

public class Scheduler {
 static void schedule(Context c,JSONObject o){
  long at=o.optLong("at");int id=o.optInt("id");if(at<=System.currentTimeMillis())return;
  AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
  Intent intent=new Intent(c,AlarmReceiver.class).putExtra("id",id);
  PendingIntent pi=PendingIntent.getBroadcast(c,id,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  if(Build.VERSION.SDK_INT>=31 && !am.canScheduleExactAlarms())return;
  am.setAlarmClock(new AlarmManager.AlarmClockInfo(at,PendingIntent.getActivity(c,id,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE)),pi);
 }
 static void cancel(Context c,int id){AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);PendingIntent pi=PendingIntent.getBroadcast(c,id,new Intent(c,AlarmReceiver.class),PendingIntent.FLAG_NO_CREATE|PendingIntent.FLAG_IMMUTABLE);if(pi!=null){am.cancel(pi);pi.cancel();}}
 static void reschedule(Context c){JSONArray a=ReminderStore.all(c);for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null) schedule(c,o);}}
 static void next(Context c,JSONObject o){try{
  String repeat=o.optString("repeat","Once");if(repeat.equals("Once")){o.put("done",true);ReminderStore.upsert(c,o);return;}
  java.util.Calendar cal=java.util.Calendar.getInstance();cal.setTimeInMillis(o.optLong("at"));
  do { if(repeat.equals("Daily"))cal.add(java.util.Calendar.DAY_OF_YEAR,1);else if(repeat.equals("Weekly"))cal.add(java.util.Calendar.WEEK_OF_YEAR,1);else if(repeat.equals("Monthly"))cal.add(java.util.Calendar.MONTH,1);else if(repeat.equals("Yearly"))cal.add(java.util.Calendar.YEAR,1);else return; }while(cal.getTimeInMillis()<=System.currentTimeMillis());
  o.put("at",cal.getTimeInMillis());ReminderStore.upsert(c,o);schedule(c,o);
 }catch(Exception ignored){}}
}
