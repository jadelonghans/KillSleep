package jp.ac.titech.itpro.sdk.killsleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("hello", "Alarm fired!");

//        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
//        mediaPlayer.start();


//        Intent serviceIntent = new Intent(context, AlarmPlayer.class);
//        serviceIntent.putExtra("random", "alarmplayer");
//
//        context.startService(serviceIntent);


//        startActivity of Alarm Screen
    }
}
