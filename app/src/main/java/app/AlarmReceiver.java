package com.yaadrakho.app;
import android.content.*;
import android.app.*;
import android.os.*;
import org.json.*;
public class AlarmReceiver extends android.content.BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){int id=i.getIntExtra("id",-1);JSONObject o=ReminderStore.get(c,id);if(o==null)return;
  String title=o.optString("title","Reminder");
  NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel("alarms","Reminder alarms",NotificationManager.IMPORTANCE_HIGH));
  Intent open=new Intent(c,AlarmActivity.class).putExtra("id",id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
  PendingIntent full=PendingIntent.getActivity(c,id,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Notification n=new Notification.Builder(c,"alarms").setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("YAAD RAKHO 🔔").setContentText(title).setCategory(Notification.CATEGORY_ALARM).setPriority(Notification.PRIORITY_MAX).setFullScreenIntent(full,true).setContentIntent(full).setAutoCancel(true).build();
  nm.notify(id,n);
  // When allowed, launch the alarm screen; otherwise the high-priority alarm notification remains available.
  try{c.startActivity(open);}catch(Exception ignored){}
 }
}
