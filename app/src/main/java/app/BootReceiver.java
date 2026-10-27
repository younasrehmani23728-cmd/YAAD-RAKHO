package com.yaadrakho.app;
import android.content.*;
public class BootReceiver extends BroadcastReceiver {public void onReceive(Context c,Intent i){Scheduler.reschedule(c);}}
