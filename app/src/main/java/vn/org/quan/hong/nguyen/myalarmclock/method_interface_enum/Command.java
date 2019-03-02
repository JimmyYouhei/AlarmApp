/*
 * MIT License
 *
 * Copyright (c) 2019.  Jimmy Youhei(Quan Nguyen)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package vn.org.quan.hong.nguyen.myalarmclock.method_interface_enum;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import vn.org.quan.hong.nguyen.myalarmclock.Alarm;
import vn.org.quan.hong.nguyen.myalarmclock.AlarmAdapter;
import vn.org.quan.hong.nguyen.myalarmclock.AlarmReceiver;
import vn.org.quan.hong.nguyen.myalarmclock.AlarmTimePicker;
import vn.org.quan.hong.nguyen.myalarmclock.R;

import static vn.org.quan.hong.nguyen.myalarmclock.AlarmAdapter.ADAPTER_ID_KEY;
import static vn.org.quan.hong.nguyen.myalarmclock.AlarmAdapter.FROM_ALARM_ADAPTER_KEY;
import static vn.org.quan.hong.nguyen.myalarmclock.AlarmAdapter.SET_ALARM_KEY;
import static vn.org.quan.hong.nguyen.myalarmclock.AlarmAdapter.ViewHolder.AM_PM_KEY;
import static vn.org.quan.hong.nguyen.myalarmclock.AlarmAdapter.ViewHolder.OLD_HH_MM_STRING_KEY;
import static vn.org.quan.hong.nguyen.myalarmclock.AlarmTimePicker.HOUR_KEY;
import static vn.org.quan.hong.nguyen.myalarmclock.AlarmTimePicker.MINUTE_KEY;
import static vn.org.quan.hong.nguyen.myalarmclock.MainActivity.CANCEL_ALL_KEY;


// A central class for methods to easily read , maintain and reuse those methods
public abstract class Command {

    // cancel all pending intent and stop all playing alarm sound
    public static void resetAllPedingIntentAndService(Context context){
        // get intent to cancel
        Intent oldSetAlarmIntent = new Intent(context.getApplicationContext() , AlarmReceiver.class );
        oldSetAlarmIntent.putExtra(AlarmAdapter.FROM_ALARM_ADAPTER_KEY , AlarmAdapter.SET_ALARM_KEY);
        // alarm manager to cancel pending intent
        AlarmManager toCancel = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        // make pending intent based on the position that is included in the requestCodeArrayList to cancel all pending intent
        for (int i = 0 ; i < AlarmAdapter.requestCodeArrayList.size(); i++ ){
            PendingIntent oldSetAlarmPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext()
                    , AlarmAdapter.requestCodeArrayList.get(i) ,
                    oldSetAlarmIntent , PendingIntent.FLAG_UPDATE_CURRENT);
            toCancel.cancel(oldSetAlarmPendingIntent);
        }
        // clear the requestCodeArrayList as finish their role and no more pending intent
        AlarmAdapter.requestCodeArrayList.clear();
        // send broadcast to stop all playing service (if any) CANCEL_ALL_KEY will try to stop everything
        oldSetAlarmIntent.putExtra(AlarmAdapter.FROM_ALARM_ADAPTER_KEY,AlarmAdapter.STOP_KEY);
        oldSetAlarmIntent.putExtra(AlarmAdapter.ADAPTER_ID_KEY , CANCEL_ALL_KEY);
        context.getApplicationContext().sendBroadcast(oldSetAlarmIntent);
    }

    // make alarm object based on intent received
    public static Alarm getOldAlarm (Intent data) {
        int oldHour = -1;

        String oldAlarmString = data.getStringExtra(AlarmAdapter.ViewHolder.OLD_HH_MM_STRING_KEY);
        String oldAmOrPm = data.getStringExtra(AlarmAdapter.ViewHolder.AM_PM_KEY);

        // parse Hour (based on 24 hour clock) and minute based on String od intent
        if (oldAmOrPm.equals("Am")){
            oldHour = Integer.valueOf(oldAlarmString.substring(0,2));
        } else if (oldAmOrPm.equals("Pm")){
            oldHour = Integer.valueOf(oldAlarmString.substring(0,2))+12;
        }
        int oldMinute = Integer.valueOf(oldAlarmString.substring(3,5));

        return new Alarm(oldHour , oldMinute);
    }

    // setup Alarm when toggle button on
    public static void setupAlarm
            (Context context, AlarmAdapter.ViewHolder holder, Alarm alarm,
             ArrayList<Integer> requestCodeArrayList , boolean setAlarmForTomorrow){

        // if cannot set alarm today
        if (setAlarmForTomorrow){
            // add 1 to Date for tommorrow
            holder.alarmCalendar.add(Calendar.DATE , 1);
        }

        // set Hour and Minute according to the alarm
        holder.alarmCalendar.set(Calendar.HOUR_OF_DAY , alarm.getHour());
        holder.alarmCalendar.set(Calendar.MINUTE , alarm.getMinute());
        // set Second to 0 for uniform calendar
        holder.alarmCalendar.set(Calendar.SECOND , 0);
        // make pending intent and put extra for alarm service
        holder.setAlarmIntent = new Intent(context , AlarmReceiver.class );
        holder.setAlarmIntent.putExtra(FROM_ALARM_ADAPTER_KEY , SET_ALARM_KEY);
        holder.setAlarmIntent.putExtra(ADAPTER_ID_KEY , holder.getAdapterPosition());
        // request code as position in the Adapter
        holder.setAlarmPendingIntent = PendingIntent.getBroadcast(context , holder.getAdapterPosition() ,
                holder.setAlarmIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        // add position to the arraylist for alarm service management
        requestCodeArrayList.add(holder.getAdapterPosition());
        // set Repeat alarm
        holder.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP ,
                holder.alarmCalendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY ,holder.setAlarmPendingIntent);
    }

    // make intent when click on the Context menu and send Request Code according to what was click
    public static void sendIntentFromContextMenu
            (View alarmAdapterView , TextView txtHourMinute, TextView txtAmPm, int REQUEST_CODE) {
        Intent intent = new Intent(alarmAdapterView.getContext() , AlarmTimePicker.class);
        intent.putExtra(OLD_HH_MM_STRING_KEY , txtHourMinute.getText());
        intent.putExtra(AM_PM_KEY , txtAmPm.getText());
        if (REQUEST_CODE == AlarmAdapter.ViewHolder.EDIT_REQUEST_CODE){
            intent.setAction(Intent.ACTION_EDIT);
        } else if (REQUEST_CODE == AlarmAdapter.ViewHolder.DELETE_REQUEST_CODE){
            intent.setAction((Intent.ACTION_DELETE));
        }

        ((Activity) alarmAdapterView.getContext()).startActivityForResult(intent , REQUEST_CODE);
    }

    // make result intent that carry TimePicker information
    public static Intent extractIntentFromTimePicker (android.widget.TimePicker timePicker){
        Intent intent = new Intent();
        // code depend on SDK
        if (Build.VERSION.SDK_INT >= 23){
        intent.putExtra(HOUR_KEY , timePicker.getHour());
        intent.putExtra(MINUTE_KEY , timePicker.getMinute());
    } else {
        intent.putExtra(HOUR_KEY, timePicker.getCurrentHour());
        intent.putExtra(MINUTE_KEY , timePicker.getCurrentMinute());
        }
        return intent;
    }

    // put old alarm information for delete or edit
    public static void putExtraInformationAboutOldAlarm (Intent intentFromMainActivity , Intent intentFromTimePicker){
        intentFromTimePicker.putExtra(OLD_HH_MM_STRING_KEY ,
                intentFromMainActivity.getStringExtra(OLD_HH_MM_STRING_KEY));
        intentFromTimePicker.putExtra(AM_PM_KEY,
                intentFromMainActivity.getStringExtra(AM_PM_KEY));
    }

    //as the name said
    public static AlarmAdapter.ViewHolder inflateNewViewForAdapter (Context context, ViewGroup parent)  {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View alarmView = layoutInflater.inflate(R.layout.alarm_detail , parent , false);
        return new AlarmAdapter.ViewHolder(alarmView);
    }
}
