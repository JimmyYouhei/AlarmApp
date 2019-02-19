package vn.org.quan.hong.nguyen.myalarmclock.event_for_event_bus;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import vn.org.quan.hong.nguyen.myalarmclock.Alarm;

// wrap gift that have a List and a context to tranfer between background thread and main thread for EventBus
// The EventBus only accept a single object so the 2 object have to wrap inside a gift like that so it can be tranfer between Thread
public class ListAndContextObject {

    private List<Alarm> alarmList = new ArrayList<>();
    private Context context ;

    public ListAndContextObject(List<Alarm> alarmList, Context context) {
        this.alarmList = alarmList;
        this.context = context;
    }

    public List<Alarm> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
