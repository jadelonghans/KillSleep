package jp.ac.titech.itpro.sdk.killsleep;

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

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String KEY_NAME = "MainActivity.name";
    private String name = "";

    private boolean alarmEnabled, nfcSet;

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

        final TextView topMessage = findViewById(R.id.topmessage);
        final Button toggleAlarm = findViewById(R.id.togglealarm);
        final Button setNfc = findViewById(R.id.setnfc);
        final Button resetNfc = findViewById(R.id.resetnfc);
        final TimePicker timePicker = findViewById(R.id.timepicker);

        resetNfc.setEnabled(nfcSet);
        setNfc.setEnabled(!nfcSet);
        toggleAlarm.setEnabled(true);

        toggleAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alarmEnabled = !alarmEnabled;

                // disable nfc buttons
                if(alarmEnabled){
                    resetNfc.setEnabled(false);
                    setNfc.setEnabled(false);

                    toggleAlarm.setText(R.string.disable_alarm);

                    Calendar calendar = Calendar.getInstance();

                    if(Build.VERSION.SDK_INT >= 23) {

                        calendar.set(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                timePicker.getHour(),
                                timePicker.getMinute(),
                                0
                        );
                        topMessage.setText(new String ("Alarm set"  ));

                    }else {
                        calendar.set(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute(),
                                0
                        );
                        topMessage.setText(new String ("Alarm set" ));
                    }

                    Toast.makeText(mainContext, R.string.toast_alarmSet, Toast.LENGTH_SHORT).show();
                }
                else{
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

                nfcSet = !nfcSet;
                resetNfc.setEnabled(nfcSet);
                setNfc.setEnabled(!nfcSet);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
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
    }
}
