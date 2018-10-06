package vn.org.quan.hong.nguyen.myalarmclock.RoomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import vn.org.quan.hong.nguyen.myalarmclock.Alarm;

@Database(entities = {Alarm.class} , version =  1)
public abstract class RoomAlarmDatabase extends RoomDatabase {

    private static RoomAlarmDatabase instance ;

    public abstract AlarmDao alarmDao();

    public static RoomAlarmDatabase getInstance(Context context){

        if(instance == null){

            instance = Room.databaseBuilder(context.getApplicationContext() , RoomAlarmDatabase.class ,
                    "alarm-database.db")
                    .allowMainThreadQueries()
                    .build();

        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
}
