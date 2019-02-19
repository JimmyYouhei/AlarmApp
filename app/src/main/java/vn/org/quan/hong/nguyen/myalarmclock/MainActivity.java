package vn.org.quan.hong.nguyen.myalarmclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import vn.org.quan.hong.nguyen.myalarmclock.event_for_event_bus.ListAndContextObject;
import vn.org.quan.hong.nguyen.myalarmclock.method_interface_enum.Command;
import vn.org.quan.hong.nguyen.myalarmclock.room_library.RoomAlarmDatabase;


//Main Activity
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private TextView title;
    private ImageButton btnAdd;
    public static final int ADD_REQUEST_CODE = 1000;
    public static final int EDIT_REQUEST_CODE = 1001;
    public static final String REQUEST_TYPE_KEY = "Request , Type";
    public static final int CANCEL_ALL_KEY = 1489;
    // List for RecyclerView
    List<Alarm> malarmList = new ArrayList<>();

    // Room Library to manipulate SQLite
    RoomAlarmDatabase alarmDatabase;

    // exceutor for background Thread
    private Executor excecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize or acquired Database by Room
        alarmDatabase = RoomAlarmDatabase.getInstance(this);

        // if there is already an Database , convert Database to an ArrayList and setupRecyclerView
        // if no Data base exist will just let RecyclerView empty
        // Add code to make it run in background and then tranfer to main thread to setup RecyclerView

        excecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (alarmDatabase.alarmDao().countAlarm() != 0){
                    malarmList = alarmDatabase.alarmDao().getDatabse();
                    setForTranferBetweenThreadAndMakeRecyclerView(malarmList, MainActivity.this);
                }
            }
        });

        // tie the View to its place
        toolbar = findViewById(R.id.mainToolbar);
        title = findViewById(R.id.txtTitle);
        btnAdd = findViewById(R.id.btnAddIcon);

        // set Title for Toolbar
        title.setText("Alarm");

        // when click on add icon will go to AlarmTimePicker make new alarm
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this , AlarmTimePicker.class);
                addIntent.setAction(Intent.ACTION_INSERT);
                startActivityForResult(addIntent , ADD_REQUEST_CODE);
            }
        });

        // Register the setForTranferBetweenThreadAndMakeRecyclerView for EventBus to run on MainThread
        EventBus.getDefault().register(this);
    }

    // for when received result back
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // received the add alarm result and OK result
        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK){

            //make new Alarm object based on the result send back
            Alarm resultAlarm = new Alarm(data.getIntExtra(AlarmTimePicker.HOUR_KEY , -1) ,
                    data.getIntExtra(AlarmTimePicker.MINUTE_KEY , -1) );

            // check if the alarm object already exist in the database if not add to database
            if (!duplicateExist(malarmList , resultAlarm)){

                // add code to run on the background thread
                excecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        malarmList.add(resultAlarm);
                        alarmDatabase.alarmDao().insertAlarm(resultAlarm);
                    }
                });

            }


            //if there is no pending intent then will setup RecyclerView
            if (AlarmAdapter.requestCodeArrayList.size() == 0){

                setForTranferBetweenThreadAndMakeRecyclerView(malarmList, MainActivity.this);

                // if there is pending intent then will reset all then setupRecyclerView
            } else {

                Command.resetAllPedingIntentAndService(this);

                setForTranferBetweenThreadAndMakeRecyclerView(malarmList, MainActivity.this);

            }
        }

        // received the edit alarm result and Ok result
        if (requestCode == AlarmAdapter.ViewHolder.EDIT_REQUEST_CODE && resultCode == RESULT_OK){

            // make alarm object based on the result received
            Alarm oldAlarm = Command.getOldAlarm(data);

            // edit the old alarm in the database to the new alarm
            for (Alarm anAlarm : malarmList){
                if (anAlarm.getHour() == oldAlarm.getHour() && anAlarm.getMinute() == oldAlarm.getMinute()){
                    anAlarm.setHour(data.getIntExtra(AlarmTimePicker.HOUR_KEY, -1));
                    anAlarm.setMinute(data.getIntExtra(AlarmTimePicker.MINUTE_KEY , -1));
                }
            }


            excecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // reset all database
                    alarmDatabase.alarmDao().nukeDatabase();
                    // make new database base on the current List
                    alarmDatabase.alarmDao().insertAll(malarmList);
                }
            });


            // if there is no pending intent then will setup RecyclerView according to the modified List
            if (AlarmAdapter.requestCodeArrayList.size() == 0){

                setForTranferBetweenThreadAndMakeRecyclerView(malarmList, MainActivity.this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

                // there are pending intents will reset all pending intent and then do as above
            } else {

                Command.resetAllPedingIntentAndService(this);

                setForTranferBetweenThreadAndMakeRecyclerView(malarmList, MainActivity.this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

            }


        }

        // if received delete result and OK result
        if (requestCode == AlarmAdapter.ViewHolder.DELETE_REQUEST_CODE && resultCode == RESULT_OK){

            // make alarm object based on result received
            final Alarm oldAlarm = Command.getOldAlarm(data);

            // according to the SDK version will use lambda expression or iterator to remove from the List
                // Some how the normal ArrayList.remove() always make error so have to do like that
            if(Build.VERSION.SDK_INT >= 24){
                malarmList.removeIf(alarm -> alarm.getHour() == oldAlarm.getHour() && alarm.getMinute() == oldAlarm.getMinute() );
            } else {
                for (Iterator<Alarm> iterator = malarmList.iterator(); iterator.hasNext();){
                    Alarm anAlarrm = iterator.next();
                    if (anAlarrm.getHour() == oldAlarm.getHour() && anAlarrm.getMinute() == oldAlarm.getMinute()){
                     iterator.remove();
                    }
                }
            }

            // reset all database and make new database as the new List

            excecutor.execute(new Runnable() {
                @Override
                public void run() {
                    alarmDatabase.alarmDao().nukeDatabase();
                    alarmDatabase.alarmDao().insertAll(malarmList);
                }
            });



            //again like above check for if there is any pending itent and will work just like above for pending intent and RecyclerView
            if (AlarmAdapter.requestCodeArrayList.size() == 0){

                setForTranferBetweenThreadAndMakeRecyclerView(malarmList, MainActivity.this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

            } else {

                Command.resetAllPedingIntentAndService(this);

                setForTranferBetweenThreadAndMakeRecyclerView(malarmList, MainActivity.this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // method to check whether the object alarm is already exist in the database
    public boolean duplicateExist (List<Alarm> compareArrayList , Alarm compareAlarm){
        boolean result = false;

        for (Alarm anAlarm : compareArrayList){
            if (anAlarm.getHour() == compareAlarm.getHour() && anAlarm.getMinute() == compareAlarm.getMinute()){
             result = true;
            }
        }

        return result;
    }

    // avoid database leak
    @Override
    protected void onDestroy() {
        RoomAlarmDatabase.destroyInstance();
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    // tranfer from background thread and setup RecyclerView on MainThread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setupRecyclerView(ListAndContextObject object) {
        List<Alarm> alarmList = object.getAlarmList();
        Context context = object.getContext();
        AlarmAdapter alarmAdapter = new AlarmAdapter(alarmList , context);
        RecyclerView recyclerView = ((Activity)context).findViewById(R.id.vRecyclerView);
        recyclerView.setAdapter(alarmAdapter);
    }

    // combination of both wrap the 2 List and Context in 1 pack and setupRecyclerView method above for quick calling
    private void setForTranferBetweenThreadAndMakeRecyclerView(List<Alarm> alarmList , Context context){
        ListAndContextObject object = new ListAndContextObject(alarmList , context);
        EventBus.getDefault().post(object);
    }


}
