package jp.ac.titech.itpro.sdk.killsleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private final static int REQ_1 = 12;
    private final static String HEX_CODE = "HEX_CODE";
    private final static String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        String fetch_hex = intent.getExtras().getString(HEX_CODE);
        Log.d(TAG, "Alarm fired!");
        if (fetch_hex != null ) Log.d(TAG, fetch_hex);
        else Log.d(TAG, "null hex_string!");

//        startActivity of Alarm Screen
        Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmScreen.class);
        alarmIntent.putExtra(HEX_CODE, fetch_hex);
        context.startActivity(alarmIntent);
    }
}
