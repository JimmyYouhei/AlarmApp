package vn.org.quan.hong.nguyen.myalarmclock.MethodInterfaceEnum;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class Command {

    // avoid Init
    private Command() {}

    public static void setupRecyclerView(List<Alarm> alarmList , Context context) {
        AlarmAdapter alarmAdapter = new AlarmAdapter(alarmList , context);
        RecyclerView recyclerView = ((Activity)context).findViewById(R.id.vRecyclerView);
        recyclerView.setAdapter(alarmAdapter);
    }

    public static void resetAllPedingIntentAndService(Context context){

        Intent oldSetAlarmIntent = new Intent(context.getApplicationContext() , AlarmReceiver.class );
        oldSetAlarmIntent.putExtra(AlarmAdapter.FROM_ALARM_ADAPTER_KEY , AlarmAdapter.SET_ALARM_KEY);
        AlarmManager toCancel = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        for (int i = 0 ; i < AlarmAdapter.requestCodeArrayList.size(); i++ ){
            PendingIntent oldSetAlarmPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext()
                    , AlarmAdapter.requestCodeArrayList.get(i) ,
                    oldSetAlarmIntent , PendingIntent.FLAG_UPDATE_CURRENT);
            toCancel.cancel(oldSetAlarmPendingIntent);
        }
        AlarmAdapter.requestCodeArrayList.clear();
        oldSetAlarmIntent.putExtra(AlarmAdapter.FROM_ALARM_ADAPTER_KEY,AlarmAdapter.STOP_KEY);
        oldSetAlarmIntent.putExtra(AlarmAdapter.ADAPTER_ID_KEY , CANCEL_ALL_KEY);
        context.getApplicationContext().sendBroadcast(oldSetAlarmIntent);

    }

    public static Alarm getOldAlarm (Intent data) {
        int oldHour = -1;

        String oldAlarmString = data.getStringExtra(AlarmAdapter.ViewHolder.OLD_HH_MM_STRING_KEY);
        String oldAmOrPm = data.getStringExtra(AlarmAdapter.ViewHolder.AM_PM_KEY);

        if (oldAmOrPm.equals("Am")){
            oldHour = Integer.valueOf(oldAlarmString.substring(0,2));
        } else if (oldAmOrPm.equals("Pm")){
            oldHour = Integer.valueOf(oldAlarmString.substring(0,2))+12;
        }
        int oldMinute = Integer.valueOf(oldAlarmString.substring(3,5));

        return new Alarm(oldHour , oldMinute);
    }

    public static void setupAlarm
            (Context context, AlarmAdapter.ViewHolder holder, Alarm alarm,
             ArrayList<Integer> requestCodeArrayList , boolean setAlarmForTomorrow){

        if (setAlarmForTomorrow){
            holder.alarmCalendar.add(Calendar.DATE , 1);
        }

        holder.alarmCalendar.set(Calendar.HOUR_OF_DAY , alarm.getHour());
        holder.alarmCalendar.set(Calendar.MINUTE , alarm.getMinute());
        holder.alarmCalendar.set(Calendar.SECOND , 0);
        holder.setAlarmIntent = new Intent(context , AlarmReceiver .class );
        holder.setAlarmIntent.putExtra(FROM_ALARM_ADAPTER_KEY , SET_ALARM_KEY);
        holder.setAlarmIntent.putExtra(ADAPTER_ID_KEY , holder.getAdapterPosition());
        holder.setAlarmPendingIntent = PendingIntent.getBroadcast(context , holder.getAdapterPosition() ,
                holder.setAlarmIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        requestCodeArrayList.add(holder.getAdapterPosition());
        holder.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP ,
                holder.alarmCalendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY ,holder.setAlarmPendingIntent);
    }

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

    public static Intent extractIntentFromTimePicker (android.widget.TimePicker timePicker){
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 23){
        intent.putExtra(HOUR_KEY , timePicker.getHour());
        intent.putExtra(MINUTE_KEY , timePicker.getMinute());
    } else {
        intent.putExtra(HOUR_KEY, timePicker.getCurrentHour());
        intent.putExtra(MINUTE_KEY , timePicker.getCurrentMinute());
        }
        return intent;
    }

    public static void putExtraInformationAboutOldAlarm (Intent intentFromMainActivity , Intent intentFromTimePicker){
        intentFromTimePicker.putExtra(OLD_HH_MM_STRING_KEY ,
                intentFromMainActivity.getStringExtra(OLD_HH_MM_STRING_KEY));
        intentFromTimePicker.putExtra(AM_PM_KEY,
                intentFromMainActivity.getStringExtra(AM_PM_KEY));
    }

    public static AlarmAdapter.ViewHolder inflateNewViewForAdapter (Context context, ViewGroup parent)  {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View alarmView = layoutInflater.inflate(R.layout.alarm_detail , parent , false);
        return new AlarmAdapter.ViewHolder(alarmView);
    }
}
