package vn.org.quan.hong.nguyen.myalarmclock;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

// handle if not playing do nothing

// class to actual play the alarm sound
public class AlarmService extends Service {
    private static final String TAG = "AlarmService";

    MediaPlayer mediaPlayer;
    // int to know the state of the service
    // runningAdapterId is the position of which from Adapter the alarm is playing and receivedAdapterId is  the position of which from the Adapter send the broadcast to stop alarm
    int receivedAdapterId = -10;
    int runningAdapterId = -100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // case to play alarm sound
        if (intent.getStringExtra(AlarmReceiver.FROM_ALARM_RECEIVER_KEY).equals(AlarmAdapter.SET_ALARM_KEY)){

            // if no alarm is already playing
            if (mediaPlayer == null){
                playAlarmSound(intent);
            } else {
                // if already an alarm is playing stop it first and then play this (Eg: 1:30 pm and 1:31 pm)
                stopAlarmSound();
                playAlarmSound(intent);
            }


            // case to stop playing alarm
        } else if (intent.getStringExtra(AlarmReceiver.FROM_ALARM_RECEIVER_KEY).equals(AlarmAdapter.STOP_KEY)){
            receivedAdapterId = intent.getIntExtra(AlarmAdapter.ADAPTER_ID_KEY , -10);

            // case received key to cancel all playing
            if (receivedAdapterId== MainActivity.CANCEL_ALL_KEY){
                // only stop if already playing to avoid null error
                if(mediaPlayer != null){
                    stopAlarmSound();
                    // reset state as no alrm is playing
                    runningAdapterId = -100;
                    receivedAdapterId = -10;

                }

                // case only from 1 positon in the adapter will compare the 2 and will stop playing only incase of  the 2 match
            } else if (receivedAdapterId == runningAdapterId)
            if(mediaPlayer != null){
                stopAlarmSound();
                    runningAdapterId = -100;
                    receivedAdapterId = -10;
            }
        }

        return START_NOT_STICKY;
    }

    // method to play Alarm sound
    private void playAlarmSound(Intent intent) {
        // remember the adapter position from which the alarm sound is playing to avoid other View can stop this broadcast
        runningAdapterId = intent.getIntExtra(AlarmAdapter.ADAPTER_ID_KEY , -10);

        mediaPlayer = MediaPlayer.create(this, R.raw.legends);
        mediaPlayer.start();
    }

    // method to stop playing alarm sound and reset mediaPlayer object
    private void stopAlarmSound() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }


}
