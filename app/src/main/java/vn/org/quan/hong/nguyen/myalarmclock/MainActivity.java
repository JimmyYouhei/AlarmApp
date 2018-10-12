package vn.org.quan.hong.nguyen.myalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vn.org.quan.hong.nguyen.myalarmclock.MethodInterfaceEnum.Command;
import vn.org.quan.hong.nguyen.myalarmclock.RoomDatabase.RoomAlarmDatabase;


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
    List<Alarm> malarmList = new ArrayList<>();

    RoomAlarmDatabase alarmDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize or acquired Database by Room
        alarmDatabase = RoomAlarmDatabase.getInstance(this);

        // if there is already an Database , convert Database to an ArrayList and setupRecyclerView
        // if no Data base exist will just let RecyclerView empty
        if (alarmDatabase.alarmDao().countAlarm() != 0){
            malarmList = alarmDatabase.alarmDao().getDatabse();
            Command.setupRecyclerView(malarmList , this);
        }

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
                malarmList.add(resultAlarm);
                alarmDatabase.alarmDao().insertAlarm(resultAlarm);
            }


            //if there is no pending intent then will setup RecyclerView
            if (AlarmAdapter.requestCodeArrayList.size() == 0){

                Command.setupRecyclerView(malarmList , this);

                // if there is pending intent then will reset all then setupRecyclerView
            } else {

                Command.resetAllPedingIntentAndService(this);

                Command.setupRecyclerView(malarmList , this);

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

            // reset all database
            alarmDatabase.alarmDao().nukeDatabase();
            // make new database base on the current List
            alarmDatabase.alarmDao().insertAll(malarmList);

            // if there is no pending intent then will setup RecyclerView according to the modified List
            if (AlarmAdapter.requestCodeArrayList.size() == 0){

                Command.setupRecyclerView(malarmList, this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

                // there are pending intents will reset all pending intent and then do as above
            } else {

                Command.resetAllPedingIntentAndService(this);

                Command.setupRecyclerView(malarmList , this);
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
            alarmDatabase.alarmDao().nukeDatabase();
            alarmDatabase.alarmDao().insertAll(malarmList);


            //again like above check for if there is any pending itent and will work just like above for pending intent and RecyclerView
            if (AlarmAdapter.requestCodeArrayList.size() == 0){

                Command.setupRecyclerView(malarmList , this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

            } else {

                Command.resetAllPedingIntentAndService(this);

                Command.setupRecyclerView(malarmList , this);
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
        super.onDestroy();
    }
}
