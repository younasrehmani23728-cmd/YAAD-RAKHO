package com.yaadrakho.app;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.media.*;import android.speech.tts.*;import android.view.*;import android.widget.*;import org.json.*;import java.util.*;
public class AlarmActivity extends Activity {
 MediaPlayer player;TextToSpeech tts;Handler handler=new Handler(Looper.getMainLooper());int id;JSONObject reminder;boolean finished=false;int cycle=0;
 @Override public void onCreate(Bundle b){super.onCreate(b);setShowWhenLocked(true);setTurnScreenOn(true);getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);id=getIntent().getIntExtra("id",-1);reminder=ReminderStore.get(this,id);if(reminder==null){finish();return;}
 LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setGravity(Gravity.CENTER);l.setPadding(24,24,24,24);l.setBackgroundColor(Color.rgb(14,42,91));setContentView(l);
 TextView h=new TextView(this);h.setText("🔔 YAAD RAKHO");h.setTextColor(-1);h.setTextSize(30);h.setGravity(Gravity.CENTER);l.addView(h);
 TextView task=new TextView(this);task.setText(reminder.optString("title"));task.setTextColor(-1);task.setTextSize(26);task.setGravity(Gravity.CENTER);task.setPadding(0,60,0,60);l.addView(task);
 Button snooze=new Button(this);snooze.setText("😴 Snooze 5 min");l.addView(snooze);snooze.setOnClickListener(v->{stop();try{reminder.put("at",System.currentTimeMillis()+300000);ReminderStore.upsert(this,reminder);Scheduler.schedule(this,reminder);}catch(Exception ignored){}finish();});
 Button done=new Button(this);done.setText("✅ Done");l.addView(done);done.setOnClickListener(v->{stop();Scheduler.next(this,reminder);finish();});
 tts=new TextToSpeech(this,status->{if(status==TextToSpeech.SUCCESS){int result=tts.setLanguage(new Locale("ur","PK"));if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED)tts.setLanguage(Locale.getDefault());}});
 ring();}
 void ring(){if(finished)return;try{player=MediaPlayer.create(this,android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI);if(player==null)player=MediaPlayer.create(this,android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);if(player!=null){player.setLooping(false);player.start();}}catch(Exception ignored){}
 ((android.os.Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createWaveform(new long[]{0,400,250,400},-1));
 handler.postDelayed(()->{releasePlayer();if(!finished&&tts!=null)tts.speak(reminder.optString("title"),TextToSpeech.QUEUE_FLUSH,null,"reminder");},3500);
 handler.postDelayed(()->{if(!finished){cycle++;ring();}},10500);
 }
 void releasePlayer(){if(player!=null){try{player.stop();}catch(Exception ignored){}player.release();player=null;}}
 void stop(){finished=true;handler.removeCallbacksAndMessages(null);releasePlayer();if(tts!=null){tts.stop();tts.shutdown();tts=null;}((android.os.Vibrator)getSystemService(VIBRATOR_SERVICE)).cancel();((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(id);}
 @Override protected void onDestroy(){stop();super.onDestroy();}
 @Override public void onBackPressed(){new AlertDialog.Builder(this).setMessage("Done ya Snooze select karein").setPositiveButton("OK",null).show();}
}
