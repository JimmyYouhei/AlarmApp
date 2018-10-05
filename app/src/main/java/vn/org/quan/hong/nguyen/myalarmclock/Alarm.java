package vn.org.quan.hong.nguyen.myalarmclock;

import android.content.Intent;

public class Alarm {

    private int hour;
    private int minute;
    private String sHour;
    private String sMinute;
    private String amOrPm;

    public Alarm() {
    }

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        if(hour >=0 && minute >=0){
         if(hour >12){
          amOrPm = "Pm";
          sHour = String.valueOf(hour - 12);
         } else {
             amOrPm = "Am";
             sHour = String.valueOf(hour);
         }

         if (minute <10){
             sMinute = "0"+ minute;
         } else {
             sMinute = String.valueOf(minute);
         }

         if (Integer.valueOf(sHour) <10) {
             sHour = "0" + sHour;
         }

        }
    }

    public String getsHour() {

        return sHour;
    }

    public String getsMinute() {
        return sMinute;
    }

    public String getAmOrPm() {
        return amOrPm;
    }

    public int getHour() {

        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
        if(hour >12){
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

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
        if (minute <10){
            sMinute = "0"+ minute;
        } else {
            sMinute = String.valueOf(minute);
        }
    }


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
