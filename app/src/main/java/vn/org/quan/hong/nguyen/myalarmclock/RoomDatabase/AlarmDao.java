package vn.org.quan.hong.nguyen.myalarmclock.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import vn.org.quan.hong.nguyen.myalarmclock.Alarm;

@Dao
public interface AlarmDao {

    @Insert
    void insertAll(List<Alarm> alarms);

    @Insert
    void insertAll(Alarm...alarms);

    @Query("SELECT COUNT(*) from alarm")
    int countAlarm();

    @Query("SELECT * FROM alarm")
    List<Alarm> getDatabse();

    @Query("DELETE FROM alarm")
    void nukeDatabase();

}
