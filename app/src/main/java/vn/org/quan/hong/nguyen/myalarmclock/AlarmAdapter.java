package vn.org.quan.hong.nguyen.myalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import vn.org.quan.hong.nguyen.myalarmclock.method_interface_enum.Command;

// feature: many alarm and seperate
// feature : on off button 1 adapter will not affect other not finish
//  to do: make multi alarm not affect other OK!


// Adapter class for RecyclerView
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private static final String TAG = "AlarmAdapter";
    public static final String FROM_ALARM_ADAPTER_KEY = "from , AlarmAdapter";
    public static final String STOP_KEY = "stop ,";
    public static final String SET_ALARM_KEY = "set , alarm";
    public static final String ADAPTER_ID_KEY = "adapter , position";

    // array list to know whether there is any pending intent and remeber their position in the Adapter;
    // the List will be the ArrayList of pending intent's position in the adapter
    public static ArrayList<Integer> requestCodeArrayList = new ArrayList<>();

    // List and Context for RecyclerView;
    List<Alarm> alarmList;
    private Context mContext;

    // Constructor
    public AlarmAdapter(List<Alarm> alarmList, Context mContext) {
        this.alarmList = alarmList;
        this.mContext = mContext;
    }


    // Create RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return Command.inflateNewViewForAdapter(mContext , parent);
    }

    //
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //make alarm object based on the ViewHolder
        final Alarm alarm = alarmList.get(position);

        // make initilaize alarm manager for each View
        holder.alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        // make text to display time and morning/afternoon
        holder.txtHourMinute.setText(alarm.getSHour() + ":" + alarm.getSMinute());
        holder.txtAmPm.setText(alarm.getAmOrPm());

        // onclick the toogle button
        holder.btnOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // when click to On
                if (isChecked){
                    // make calendar object and get current time
                    holder.alarmCalendar = Calendar.getInstance();

                    // case alarm can be set today
                    if (holder.alarmCalendar.get(Calendar.HOUR_OF_DAY)< alarm.getHour()){
                        Command.setupAlarm(mContext, holder , alarm , requestCodeArrayList , false);
                    // same as above
                    } else if (holder.alarmCalendar.get(Calendar.HOUR_OF_DAY) == alarm.getHour()
                            && holder.alarmCalendar.get(Calendar.MINUTE) < alarm.getMinute()){

                        Command.setupAlarm(mContext, holder ,alarm , requestCodeArrayList , false);

                        // case cannot set alarm for today will set alarm for tomorrow
                    } else if (holder.alarmCalendar.get(Calendar.HOUR_OF_DAY) > alarm.getHour()){

                        Command.setupAlarm(mContext, holder , alarm ,requestCodeArrayList , true);
                        // case same as above
                    } else if (holder.alarmCalendar.get(Calendar.HOUR_OF_DAY) == alarm.getHour()
                            && holder.alarmCalendar.get(Calendar.MINUTE) >= alarm.getMinute()){

                        Command.setupAlarm(mContext, holder , alarm ,requestCodeArrayList , true);

                    }

                    // when click to off
                } else {
                    // cancel current pending intent and send broadcast if the service for this is
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

    // internal class as required by RecyclerView
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
        public Calendar alarmCalendar;
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

        // create Context menu for each View of RecyclerView
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuInflater menuInflater = new MenuInflater(v.getContext());
            menuInflater.inflate(R.menu.contextual_menu, menu);

            MenuItem editAlarm = menu.findItem(R.id.editAlarm);
            MenuItem deleteAlarm = menu.findItem(R.id.deleteAlarm);

            editAlarm.setOnMenuItemClickListener(this);
            deleteAlarm.setOnMenuItemClickListener(this);

        }

        // make code for when Context Menu Item is clicked
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()){
                // when click edit menu item
                case R.id.editAlarm:
                    Command.sendIntentFromContextMenu(mView , txtHourMinute,txtAmPm, EDIT_REQUEST_CODE);
                    break;

                    // when click delete Menu item
                case R.id.deleteAlarm:
                    Command.sendIntentFromContextMenu(mView,txtHourMinute,txtAmPm,DELETE_REQUEST_CODE);
                    break;

            }
            return true;
        }
    }
}
