package com.yaadrakho.app;

import android.content.Context;
import org.json.*;
import java.util.*;

public class ReminderStore {
 static JSONArray all(Context c) { try { return new JSONArray(c.getSharedPreferences("reminders",0).getString("items","[]")); } catch(Exception e){return new JSONArray();} }
 static void save(Context c,JSONArray a){c.getSharedPreferences("reminders",0).edit().putString("items",a.toString()).apply();}
 static JSONObject get(Context c,int id){JSONArray a=all(c);for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null && o.optInt("id")==id)return o;}return null;}
 static void upsert(Context c,JSONObject item){JSONArray a=all(c),b=new JSONArray();int id=item.optInt("id");for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null && o.optInt("id")!=id)b.put(o);}b.put(item);save(c,b);}
 static void delete(Context c,int id){JSONArray a=all(c),b=new JSONArray();for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null && o.optInt("id")!=id)b.put(o);}save(c,b);}
}
