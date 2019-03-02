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

package vn.org.quan.hong.nguyen.myalarmclock;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
// class for alarm object and annotation for Room

@Entity
public class Alarm {



    @PrimaryKey
    @NonNull
    private String alarmId;

    @ColumnInfo
    private int hour;
    @ColumnInfo
    private int minute;
    @ColumnInfo
    private String sHour;
    @ColumnInfo
    private String sMinute;
    @ColumnInfo
    private String amOrPm;


    public Alarm(int hour, int minute) {

        this.hour = hour;
        this.minute = minute;

        if(hour >=0 && minute >=0){
            setStringAccordingToIntHour(hour);

            setStringAccordingToIntMinute(minute);
        }

        alarmId = sHour+sMinute+amOrPm;
    }

    // setter and getter for this class

    @NonNull
    public String getAlarmId() {
        return alarmId;
    }

    private void setStringAccordingToIntMinute(int minute) {
        if (minute <10){
         sMinute = "0"+ minute;
     } else {
         sMinute = String.valueOf(minute);
     }
    }

    private void setStringAccordingToIntHour(int hour) {
        if(hour >=12){
         amOrPm = "Pm";
         sHour = String.valueOf(hour - 12);
        } else {
            amOrPm = "Am";
            sHour = String.valueOf(hour);
        }

        if (Integer.valueOf(sHour) <10) {
            sHour = "0" + sHour;
        }
    }

    public String getAmOrPm() {
        return amOrPm;
    }

    public int getHour() {

        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
        setStringAccordingToIntHour(hour);

        alarmId = sHour+sMinute+amOrPm;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
        setStringAccordingToIntMinute(minute);

        alarmId = sHour+sMinute+amOrPm;
    }

    public void setAlarmId(@NonNull String alarmId) {
        this.alarmId = alarmId;
    }

    public void setAmOrPm(String amOrPm) {
        this.amOrPm = amOrPm;
    }

    public String getSHour() {

        return sHour;
    }

    public String getSMinute() {

        return sMinute;
    }

    public void setSHour(String sHour) {
        this.sHour = sHour;
    }

    public void setSMinute(String sMinute) {
        this.sMinute = sMinute;
    }

    // toString for test purpose
    @Override
    public String toString() {
        return "Alarm{" +
                "hour=" + hour +
                ", minute=" + minute +
                ", sHour='" + sHour + '\'' +
                ", sMinute='" + sMinute + '\'' +
                ", amOrPm='" + amOrPm + '\'' +
                '}';
    }

}
