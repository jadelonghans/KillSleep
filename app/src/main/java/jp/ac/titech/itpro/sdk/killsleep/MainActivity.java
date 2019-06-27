package jp.ac.titech.itpro.sdk.killsleep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String KEY_NAME = "MainActivity.name";
    private String name = "";

    private boolean alarmEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            name = savedInstanceState.getString(KEY_NAME);
        }

        alarmEnabled = false;
        final Button toggleAlarm = findViewById(R.id.togglealarm);
        final Button setNfc = findViewById(R.id.setnfc);
        final Button resetNfc = findViewById(R.id.resetnfc);

        resetNfc.setEnabled(false);
        setNfc.setEnabled(true);
        toggleAlarm.setEnabled(true);

        toggleAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmEnabled = !alarmEnabled;
                if (alarmEnabled) toggleAlarm.setText(R.string.disable_alarm);
                else toggleAlarm.setText(R.string.enable_alarm);
            }
        });


        resetNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetNfc.setEnabled(false);
                setNfc.setEnabled(true);
            }
        });

        setNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetNfc.setEnabled(true);
                setNfc.setEnabled(false);
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
}
