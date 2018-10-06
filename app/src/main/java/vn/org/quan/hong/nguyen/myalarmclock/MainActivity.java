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

// feature: avoid duplicate

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

        alarmDatabase = RoomAlarmDatabase.getInstance(this);

        if (alarmDatabase.alarmDao().countAlarm() != 0){
            malarmList = alarmDatabase.alarmDao().getDatabse();
            Command.setupRecyclerView(malarmList , this);
        }

        toolbar = findViewById(R.id.mainToolbar);
        title = findViewById(R.id.txtTitle);
        btnAdd = findViewById(R.id.btnAddIcon);

        title.setText("Alarm");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this , AlarmTimePicker.class);
                addIntent.setAction(Intent.ACTION_INSERT);
                startActivityForResult(addIntent , ADD_REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK){

            Alarm resultAlarm = new Alarm(data.getIntExtra(AlarmTimePicker.HOUR_KEY , -1) ,
                    data.getIntExtra(AlarmTimePicker.MINUTE_KEY , -1) );

            if (!duplicateExist(malarmList , resultAlarm)){
                malarmList.add(resultAlarm);
                alarmDatabase.alarmDao().insertAlarm(resultAlarm);
            }


            if (AlarmAdapter.requestCodeArrayList.size() == 0){

                Command.setupRecyclerView(malarmList , this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

            } else {

                Command.resetAllPedingIntentAndService(this);

                Command.setupRecyclerView(malarmList , this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

            }
        }

        if (requestCode == AlarmAdapter.ViewHolder.EDIT_REQUEST_CODE && resultCode == RESULT_OK){

            Alarm oldAlarm = Command.getOldAlarm(data);

            for (Alarm anAlarm : malarmList){
                if (anAlarm.getHour() == oldAlarm.getHour() && anAlarm.getMinute() == oldAlarm.getMinute()){
                    anAlarm.setHour(data.getIntExtra(AlarmTimePicker.HOUR_KEY, -1));
                    anAlarm.setMinute(data.getIntExtra(AlarmTimePicker.MINUTE_KEY , -1));
                }
            }

            alarmDatabase.alarmDao().nukeDatabase();
            alarmDatabase.alarmDao().insertAll(malarmList);

            if (AlarmAdapter.requestCodeArrayList.size() == 0){

                Command.setupRecyclerView(malarmList, this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

            } else {

                Command.resetAllPedingIntentAndService(this);

                Command.setupRecyclerView(malarmList , this);
                Log.d(TAG, "onActivityResult: " + AlarmAdapter.requestCodeArrayList.toString());

            }


        }
        
        if (requestCode == AlarmAdapter.ViewHolder.DELETE_REQUEST_CODE && resultCode == RESULT_OK){

            final Alarm oldAlarm = Command.getOldAlarm(data);

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

            alarmDatabase.alarmDao().nukeDatabase();
            alarmDatabase.alarmDao().insertAll(malarmList);


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

    public boolean duplicateExist (List<Alarm> compareArrayList , Alarm compareAlarm){
        boolean result = false;

        for (Alarm anAlarm : compareArrayList){
            if (anAlarm.getHour() == compareAlarm.getHour() && anAlarm.getMinute() == compareAlarm.getMinute()){
             result = true;
            }
        }

        return result;
    }

    @Override
    protected void onDestroy() {
        RoomAlarmDatabase.destroyInstance();
        super.onDestroy();
    }
}
