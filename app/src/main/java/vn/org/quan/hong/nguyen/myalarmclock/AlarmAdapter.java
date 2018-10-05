package vn.org.quan.hong.nguyen.myalarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

// feature: many alarm and seperate
// feature : on off button 1 adapter will not affect other not finish
//  to do: make multi alarm not affect other

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private static final String TAG = "AlarmAdapter";
    public static final String FROM_ALARM_ADAPTER_KEY = "from , AlarmAdapter";
    public static final String STOP_KEY = "stop ,";
    public static final String SET_ALARM_KEY = "set , alarm";
    public static final String ADAPTER_ID_KEY = "adapter , position";
    public static ArrayList<Integer> requestCodeArrayList = new ArrayList<>();

    List<Alarm> alarmList;
    private Context mContext;





    public AlarmAdapter(List<Alarm> alarmList, Context mContext) {
        this.alarmList = alarmList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View alarmView = layoutInflater.inflate(R.layout.alarm_detail , parent , false);
        ViewHolder viewHolder = new ViewHolder(alarmView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Alarm alarm = alarmList.get(position);

        holder.alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        holder.txtHourMinute.setText(alarm.getsHour() + ":" + alarm.getsMinute());
        holder.txtAmPm.setText(alarm.getAmOrPm());

        holder.btnOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    holder.alarmCalendar = Calendar.getInstance();

                    if (holder.alarmCalendar.get(Calendar.HOUR_OF_DAY)< alarm.getHour()){

                        holder.alarmCalendar.set(Calendar.HOUR_OF_DAY , alarm.getHour());
                        holder.alarmCalendar.set(Calendar.MINUTE , alarm.getMinute());
                        holder.alarmCalendar.set(Calendar.SECOND , 0);
                        holder.setAlarmIntent = new Intent(mContext , AlarmReceiver.class );
                        holder.setAlarmIntent.putExtra(FROM_ALARM_ADAPTER_KEY , SET_ALARM_KEY);
                        holder.setAlarmIntent.putExtra(ADAPTER_ID_KEY , holder.getAdapterPosition());
                        holder.setAlarmPendingIntent = PendingIntent.getBroadcast(mContext , holder.getAdapterPosition() ,
                                holder.setAlarmIntent , PendingIntent.FLAG_UPDATE_CURRENT);
                        requestCodeArrayList.add(holder.getAdapterPosition());
                        holder.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP ,
                                holder.alarmCalendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY ,holder.setAlarmPendingIntent);

                    } else if (holder.alarmCalendar.get(Calendar.HOUR_OF_DAY) == alarm.getHour()
                            && holder.alarmCalendar.get(Calendar.MINUTE) < alarm.getMinute()){

                        holder.alarmCalendar.set(Calendar.HOUR_OF_DAY , alarm.getHour());
                        holder.alarmCalendar.set(Calendar.MINUTE , alarm.getMinute());
                        holder.alarmCalendar.set(Calendar.SECOND , 0);
                        holder.setAlarmIntent = new Intent(mContext , AlarmReceiver.class );
                        holder.setAlarmIntent.putExtra(FROM_ALARM_ADAPTER_KEY , SET_ALARM_KEY);
                        holder.setAlarmIntent.putExtra(ADAPTER_ID_KEY , holder.getAdapterPosition());
                        holder.setAlarmPendingIntent = PendingIntent.getBroadcast(mContext , holder.getAdapterPosition() ,
                                holder.setAlarmIntent , PendingIntent.FLAG_UPDATE_CURRENT);
                        requestCodeArrayList.add(holder.getAdapterPosition());
                        holder.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP ,
                                holder.alarmCalendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY ,holder.setAlarmPendingIntent);

                    } else if (holder.alarmCalendar.get(Calendar.HOUR_OF_DAY) > alarm.getHour()){

                        holder.alarmCalendar.add(Calendar.DATE , 1);
                        holder.alarmCalendar.set(Calendar.HOUR_OF_DAY , alarm.getHour());
                        holder.alarmCalendar.set(Calendar.MINUTE , alarm.getMinute());
                        holder.alarmCalendar.set(Calendar.SECOND , 0);
                        holder.setAlarmIntent = new Intent(mContext , AlarmReceiver.class );
                        holder.setAlarmIntent.putExtra(FROM_ALARM_ADAPTER_KEY , SET_ALARM_KEY);
                        holder.setAlarmIntent.putExtra(ADAPTER_ID_KEY , holder.getAdapterPosition());
                        holder.setAlarmPendingIntent = PendingIntent.getBroadcast(mContext , holder.getAdapterPosition() ,
                                holder.setAlarmIntent , PendingIntent.FLAG_UPDATE_CURRENT);
                        requestCodeArrayList.add(holder.getAdapterPosition());
                        holder.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP ,
                                holder.alarmCalendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY ,holder.setAlarmPendingIntent);

                    } else if (holder.alarmCalendar.get(Calendar.HOUR_OF_DAY) == alarm.getHour()
                            && holder.alarmCalendar.get(Calendar.MINUTE) >= alarm.getMinute()){

                        holder.alarmCalendar.add(Calendar.DATE , 1);

                        holder.alarmCalendar.set(Calendar.HOUR_OF_DAY , alarm.getHour());
                        holder.alarmCalendar.set(Calendar.MINUTE , alarm.getMinute());
                        holder.alarmCalendar.set(Calendar.SECOND , 0);
                        holder.setAlarmIntent = new Intent(mContext , AlarmReceiver.class );
                        holder.setAlarmIntent.putExtra(ADAPTER_ID_KEY , holder.getAdapterPosition());
                        holder.setAlarmIntent.putExtra(FROM_ALARM_ADAPTER_KEY , SET_ALARM_KEY);

                        holder.setAlarmPendingIntent = PendingIntent.getBroadcast(mContext , holder.getAdapterPosition() ,
                                holder.setAlarmIntent , PendingIntent.FLAG_UPDATE_CURRENT);
                        requestCodeArrayList.add(holder.getAdapterPosition());
                        holder.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP ,
                                holder.alarmCalendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY ,holder.setAlarmPendingIntent);

                    }

                } else {
                    holder.alarmManager.cancel(holder.setAlarmPendingIntent);
                    holder.setAlarmIntent.putExtra(FROM_ALARM_ADAPTER_KEY , STOP_KEY);
                    holder.setAlarmIntent.putExtra(ADAPTER_ID_KEY , holder.getAdapterPosition());
                    mContext.sendBroadcast(holder.setAlarmIntent);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener , MenuItem.OnMenuItemClickListener {
        public static final int EDIT_REQUEST_CODE = 10020;
        public static final int DELETE_REQUEST_CODE = 10080;
        public static final String OLD_HH_MM_STRING_KEY = "old , HH:MM , key";
        public static final String AM_PM_KEY = "am , or , pm";


        public TextView txtHourMinute;
        public TextView txtAmPm;
        public TextView txtAlarmTitle;
        public ToggleButton btnOnOff;
        public View mView;
        public Intent setAlarmIntent;
        public PendingIntent setAlarmPendingIntent;
        public AlarmManager alarmManager;
        Calendar alarmCalendar;
        int test = 0;



        public ViewHolder(View itemView) {
            super(itemView);

            txtHourMinute = itemView.findViewById(R.id.txtTimeDisplay);
            txtAmPm = itemView.findViewById(R.id.txtAmPm);
            txtAlarmTitle = itemView.findViewById(R.id.txtAlarmTitle);
            btnOnOff = itemView.findViewById(R.id.btnOnOff);

            mView = itemView;

            mView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuInflater menuInflater = new MenuInflater(v.getContext());
            menuInflater.inflate(R.menu.contextual_menu, menu);

            MenuItem editAlarm = menu.findItem(R.id.editAlarm);
            MenuItem deleteAlarm = menu.findItem(R.id.deleteAlarm);

            editAlarm.setOnMenuItemClickListener(this);
            deleteAlarm.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()){

                case R.id.editAlarm:
                    Intent editIntent = new Intent(mView.getContext() , TimePicker.class);
                    editIntent.putExtra(OLD_HH_MM_STRING_KEY , txtHourMinute.getText());
                    editIntent.putExtra(AM_PM_KEY , txtAmPm.getText());
                    editIntent.setAction(Intent.ACTION_EDIT);
                    ((Activity) mView.getContext()).startActivityForResult(editIntent , EDIT_REQUEST_CODE);
                    break;

                case R.id.deleteAlarm:
                    Intent deleteIntent = new Intent(mView.getContext() , TimePicker.class);
                    deleteIntent.putExtra(OLD_HH_MM_STRING_KEY , txtHourMinute.getText());
                    deleteIntent.putExtra(AM_PM_KEY , txtAmPm.getText());
                    deleteIntent.setAction(Intent.ACTION_DELETE);
                    ((Activity) mView.getContext()).startActivityForResult(deleteIntent ,DELETE_REQUEST_CODE);
                    break;

            }
            return true;
        }

    }

}
