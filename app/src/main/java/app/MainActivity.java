package com.yaadrakho.app;

import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.speech.RecognizerIntent;import android.view.*;import android.widget.*;import org.json.*;import java.text.*;import java.util.*;

public class MainActivity extends Activity {
 LinearLayout root,list; EditText title; TextView when; long selectedAt; String repeat="Once"; int voiceCode=7;
 int blue=Color.rgb(24,92,185);
 TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(Color.rgb(25,35,52));t.setPadding(10,12,10,12);return t;}
 Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);return b;}
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(blue);screen();if(Build.VERSION.SDK_INT>=33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=getPackageManager().PERMISSION_GRANTED)requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},1);}
 void screen(){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(1);root.setPadding(22,20,22,20);root.setBackgroundColor(Color.rgb(244,248,255));sc.addView(root);setContentView(sc);
 TextView heading=text("🔔 YAAD RAKHO",28);heading.setTextColor(blue);root.addView(heading);root.addView(text("Apna kaam kabhi na bhoolo • Offline reminders",14));
 title=new EditText(this);title.setSingleLine(false);title.setHint("Kya yaad dilana hai? / کیا یاد دلانا ہے؟");root.addView(title);
 Button mic=button("🎙️ Bol kar likhein");root.addView(mic);mic.setOnClickListener(v->{try{Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ur-PK");startActivityForResult(i,voiceCode);}catch(Exception e){Toast.makeText(this,"Voice recognition unavailable",Toast.LENGTH_LONG).show();}});
 selectedAt=System.currentTimeMillis()+3600000;
 when=text("",18);root.addView(when);refreshTime();Button choose=button("📅 Date aur time select karein");root.addView(choose);choose.setOnClickListener(v->pickDate());
 Spinner spin=new Spinner(this);String[] repeats={"Once","Daily","Weekly","Monthly","Yearly"};ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,repeats);spin.setAdapter(adapter);root.addView(text("Repeat:",15));root.addView(spin);
 Button save=button("➕ Reminder save karein");root.addView(save);save.setOnClickListener(v->{String s=title.getText().toString().trim();if(s.isEmpty()){title.setError("Kaam likhein");return;}if(selectedAt<=System.currentTimeMillis()){Toast.makeText(this,"Future time select karein",Toast.LENGTH_LONG).show();return;}
 AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);if(Build.VERSION.SDK_INT>=31 && !am.canScheduleExactAlarms()){new AlertDialog.Builder(this).setMessage("Exact alarm ki permission dein, phir reminder save karein.").setPositiveButton("Settings",(d,w)->startActivity(new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))).setNegativeButton("Cancel",null).show();return;}
 try{JSONObject o=new JSONObject();int id=(int)(System.currentTimeMillis()%2000000000);o.put("id",id);o.put("title",s);o.put("at",selectedAt);o.put("repeat",spin.getSelectedItem().toString());o.put("done",false);ReminderStore.upsert(this,o);Scheduler.schedule(this,o);title.setText("");Toast.makeText(this,"Reminder saved 🔔",Toast.LENGTH_SHORT).show();renderList();}catch(Exception e){Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();}});
 root.addView(text("📋 Saved reminders",22));list=new LinearLayout(this);list.setOrientation(1);root.addView(list);renderList();
 root.addView(text("Tip: Settings mein notifications aur alarms allow karein. Phone off ho to alarm nahi baj sakta.",13));
 }
 void refreshTime(){when.setText("⏰ "+new SimpleDateFormat("EEE, dd MMM yyyy • hh:mm a",Locale.getDefault()).format(new Date(selectedAt)));}
 void pickDate(){Calendar c=Calendar.getInstance();c.setTimeInMillis(selectedAt);new DatePickerDialog(this,(v,y,m,d)->{Calendar x=Calendar.getInstance();x.set(y,m,d);new TimePickerDialog(this,(tv,h,min)->{x.set(Calendar.HOUR_OF_DAY,h);x.set(Calendar.MINUTE,min);x.set(Calendar.SECOND,0);selectedAt=x.getTimeInMillis();refreshTime();},c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false).show();},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();}
 void renderList(){list.removeAllViews();JSONArray a=ReminderStore.all(this);ArrayList<JSONObject> items=new ArrayList<>();for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)items.add(o);}Collections.sort(items,(x,y)->Long.compare(x.optLong("at"),y.optLong("at")));
 if(items.isEmpty())list.addView(text("Abhi koi reminder nahi.",15));for(JSONObject o:items){int id=o.optInt("id");String info=o.optString("title")+"\n"+new SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.getDefault()).format(new Date(o.optLong("at")))+" • "+o.optString("repeat")+(o.optBoolean("done")?" • Done":"");TextView t=text(info,16);t.setBackgroundColor(Color.WHITE);list.addView(t);Button del=button("🗑 Delete");list.addView(del);del.setOnClickListener(v->new AlertDialog.Builder(this).setMessage("Delete this reminder?").setPositiveButton("Delete",(d,w)->{Scheduler.cancel(this,id);ReminderStore.delete(this,id);renderList();}).setNegativeButton("Cancel",null).show());}}
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==voiceCode && result==RESULT_OK && data!=null){ArrayList<String> words=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);if(words!=null&&!words.isEmpty())title.setText(words.get(0));}}
 @Override protected void onResume(){super.onResume();if(list!=null)renderList();}
}
