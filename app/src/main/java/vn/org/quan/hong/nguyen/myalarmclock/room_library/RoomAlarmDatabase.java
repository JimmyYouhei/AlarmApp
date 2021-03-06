/*
 * MIT License
 *
 * Copyright (c) 2019.  Jimmy Youhei(Quan Nguyen)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package vn.org.quan.hong.nguyen.myalarmclock.room_library;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import vn.org.quan.hong.nguyen.myalarmclock.Alarm;
import vn.org.quan.hong.nguyen.myalarmclock.room_library.AlarmDao;

// Room abstract class to make SQLite
@Database(entities = {Alarm.class} , version =  1)
public abstract class RoomAlarmDatabase extends RoomDatabase {

    private static RoomAlarmDatabase instance ;

    public abstract AlarmDao alarmDao();

    public static RoomAlarmDatabase getInstance(Context context){

        if(instance == null){

            instance = Room.databaseBuilder(context.getApplicationContext() , RoomAlarmDatabase.class ,
                    "alarm-database.db")
                    // eliminate run on main thread for better performance
//                    .allowMainThreadQueries()
                    .build();

        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
}

