package vn.org.quan.hong.nguyen.myalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// BroadcastReceiver class to receive broadcast
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static final String FROM_ALARM_RECEIVER_KEY = "from , AlarmReceiver";
    public static final String FROM_ALARM_RECEIVER_ADAPTER_KEY = "from , AlarmReciver , adapterPosition";

    @Override
    public void onReceive(Context context, Intent intent) {

        // pass forward the intent
        Intent alarm = new Intent(context , AlarmService.class);
        alarm.putExtra(FROM_ALARM_RECEIVER_KEY , intent.getStringExtra(AlarmAdapter.FROM_ALARM_ADAPTER_KEY));
        alarm.putExtra(AlarmAdapter.ADAPTER_ID_KEY , intent.getIntExtra(AlarmAdapter.ADAPTER_ID_KEY , -10));
        context.startService(alarm);

    }
}
