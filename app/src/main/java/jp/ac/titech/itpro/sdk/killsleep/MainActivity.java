package jp.ac.titech.itpro.sdk.killsleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Context mainContext = this;
    private final static int REQ_1 = 12;
    private final static String HEX_CODE = "HEX_CODE";
    public static final String NFC_IDENTIFIER = "wxyz" ;

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String KEY_NAME = "MainActivity.name";
    private String name = "";

    private boolean alarmEnabled, nfcSet;

    private TextView topMessage;
    private Button toggleAlarm ;
    private Button setNfc;
    private Button resetNfc;
    private TimePicker timePicker;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    public String hexString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            name = savedInstanceState.getString(KEY_NAME);
        }

        alarmEnabled = false;
        nfcSet = false;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        topMessage = findViewById(R.id.topmessage);
        toggleAlarm = findViewById(R.id.togglealarm);
        setNfc = findViewById(R.id.setnfc);
        resetNfc = findViewById(R.id.resetnfc);
        timePicker = findViewById(R.id.timepicker);

        resetNfc.setEnabled(nfcSet);
        setNfc.setEnabled(!nfcSet);
        toggleAlarm.setEnabled(true);

        toggleAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hexString == null){
                    Toast.makeText(MainActivity.this, "NFC not set!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // disable nfc buttons
                if(!alarmEnabled){

                    Calendar calendar = Calendar.getInstance();
                    int minute, hour;

                    if(Build.VERSION.SDK_INT >= 23) {

                        minute = timePicker.getMinute();
                        hour = timePicker.getHour();

                    }else {

                        minute = timePicker.getCurrentMinute();
                        hour = timePicker.getCurrentHour();
                    }

                    calendar.set(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                hour,
                                minute,
                                0
                        );

                    String hour_s = String.valueOf(hour);
                    String minute_s = String.valueOf(minute);

                    if(hour<10) hour_s = "0" + hour_s;
                    if(minute<10) minute_s = "0" + minute_s;

                    // set alarm
                    setAlarm(calendar.getTimeInMillis(), calendar);

                    // update UI
                    Toast.makeText(mainContext, R.string.toast_alarmSet, Toast.LENGTH_SHORT).show();

                    topMessage.setText(new String ("Alarm set at " + hour_s + ":" + minute_s ));
                    toggleAlarm.setText(R.string.disable_alarm);
                    timePicker.setEnabled(false);
                    alarmEnabled = !alarmEnabled;
                    resetNfc.setEnabled(false);
                    setNfc.setEnabled(false);
                }

                else{
                    // Cancel alarm
                    alarmManager.cancel(pendingIntent);

                    alarmEnabled = !alarmEnabled;

                    timePicker.setEnabled(true);

                    resetNfc.setEnabled(nfcSet);
                    setNfc.setEnabled(!nfcSet);

                    toggleAlarm.setText(R.string.enable_alarm);
                    topMessage.setText(R.string.top_message);
                }

            }
        });


        resetNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hexString = null;   //clear saved nfc id
                nfcSet = !nfcSet;
                resetNfc.setEnabled(nfcSet);
                setNfc.setEnabled(!nfcSet);

            }
        });

        setNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // scan nfc tag
                Intent intent = new Intent(MainActivity.this, NfcReader.class);
//                intent.putExtra(NfcReader.INTENT_EXTRA, "hello");
                startActivityForResult(intent, REQ_1);
            }
        });


    }

    private void setAlarm(long timeInMillis, Calendar calendar) {


        Intent intent = new Intent(this, AlarmReceiver.class);
        Log.d(TAG, "hexString pushed: "+ hexString);
        intent.putExtra(HEX_CODE, hexString); // to identify intent when received

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, 10* 1000, pendingIntent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        Intent intent = getIntent();
        String state = intent.getStringExtra(NFC_IDENTIFIER);
        if (state != null && state.equals("alarm_diffused")){
            Log.d(TAG,"received state is: "+ state);

            alarmEnabled = !alarmEnabled;

                timePicker.setEnabled(true);

                resetNfc.setEnabled(nfcSet);
                setNfc.setEnabled(!nfcSet);

                toggleAlarm.setText(R.string.enable_alarm);
                topMessage.setText(R.string.top_message);
        }
        else
            Log.d(TAG,"no intent was sent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putString(KEY_NAME, name);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        Log.d(TAG,"onActivityResult");

        if (resCode == RESULT_OK){
            String nfcIdentifier = data.getStringExtra(NFC_IDENTIFIER);
            if(nfcIdentifier != null){
                Toast.makeText(this, nfcIdentifier, Toast.LENGTH_SHORT).show();

                // modify this based on if setNfc returns a successful scan
                nfcSet = !nfcSet;
                resetNfc.setEnabled(nfcSet);
                setNfc.setEnabled(!nfcSet);

                hexString = new String(nfcIdentifier);
            }
        }

//        else if (resCode == 100){
//            String result = data.getStringExtra(NFC_IDENTIFIER);
//            if(result != null && result.equals("match")){
//                Log.d(TAG, "Alarm diffused.");
//
//                alarmEnabled = !alarmEnabled;
//
//                timePicker.setEnabled(true);
//
//                resetNfc.setEnabled(nfcSet);
//                setNfc.setEnabled(!nfcSet);
//
//                toggleAlarm.setText(R.string.enable_alarm);
//                topMessage.setText(R.string.top_message);
//            }
//        }

        else Log.d(TAG,"onActivityResult, resCode:" + String.valueOf(resCode));
    }
}
