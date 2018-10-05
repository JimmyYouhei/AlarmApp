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

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";

    MediaPlayer mediaPlayer;
    int receivedAdapterId = -10;
    int runningAdapterId = -100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getStringExtra(AlarmReceiver.FROM_ALARM_RECEIVER_KEY).equals(AlarmAdapter.SET_ALARM_KEY)){

            if (mediaPlayer == null){
                runningAdapterId = intent.getIntExtra(AlarmAdapter.ADAPTER_ID_KEY , -10);
                mediaPlayer = MediaPlayer.create(this, R.raw.legends);
                mediaPlayer.start();
            } else {
                mediaPlayer.stop();
                mediaPlayer.reset();
                runningAdapterId = intent.getIntExtra(AlarmAdapter.ADAPTER_ID_KEY , -10);
                mediaPlayer = MediaPlayer.create(this, R.raw.legends);
                mediaPlayer.start();
            }

        } else if (intent.getStringExtra(AlarmReceiver.FROM_ALARM_RECEIVER_KEY).equals(AlarmAdapter.STOP_KEY)){
            receivedAdapterId = intent.getIntExtra(AlarmAdapter.ADAPTER_ID_KEY , -10);

            if (receivedAdapterId== MainActivity.CANCEL_ALL_KEY){
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    runningAdapterId = -100;
                    receivedAdapterId = -10;

                }
            } else if (receivedAdapterId == runningAdapterId)
            if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    runningAdapterId = -100;
                    receivedAdapterId = -10;
            }
        }

        return START_NOT_STICKY;
    }


}
