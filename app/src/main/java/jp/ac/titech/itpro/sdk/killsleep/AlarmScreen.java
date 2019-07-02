package jp.ac.titech.itpro.sdk.killsleep;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class AlarmScreen extends AppCompatActivity {

    public static final String INTENT_EXTRA = "xyz" ;
    public static final String NFC_IDENTIFIER = "wxyz" ;
    public final static String HEX_CODE = "HEX_CODE";
    private final static String TAG = AlarmScreen.class.getSimpleName();

    private NfcAdapter nfcAdapter;
    private final static int REQ_ENABLE_NFC = 1111;
    private String original_hex = null;

    private MediaPlayer mediaPlayer;

//    Intent intent;
//    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate AlarmScreen");
        setContentView(R.layout.alarm_screen);

        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        Intent intent = getIntent();
        original_hex = intent.getStringExtra(HEX_CODE);
        if (original_hex != null){
            Log.d(TAG,"original hex is: "+ original_hex);
        }
        else
            Log.d(TAG,"ERROR: original hex is null!");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){
            Log.d(TAG, getString(R.string.nfc_not_found));
            Toast.makeText(this, R.string.nfc_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        else{
            if(!nfcAdapter.isEnabled()){
                Log.d(TAG, "Please turn on NFC");
                Toast.makeText(this, "Please turn on NFC.", Toast.LENGTH_SHORT).show();
                finish();
                return;
//                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
//                startActivityForResult(intent, REQ_ENABLE_NFC );
            }
            else{
                Log.d(TAG, "NFC is on!");
//                Toast.makeText(this, R.string.)
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart NfcReader");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume NfcReader");

        Intent intent = new Intent(this, getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent, 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause NfcReader");

        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop NfcReader");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy NfcReader");
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            Toast.makeText(this, "Tag detected", Toast.LENGTH_SHORT);
            Log.d(TAG, "Tag detected");

            handleIncomingDataIntent(intent);
        }
    }

    // copied starts here
    private void handleIncomingDataIntent(Intent intent) {
//        if ("text/plain".equals(intent.getType())) {
        if(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) != null){
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            // directly doing tag.getId() gave inconsistent values every time, converting to hex gave same codes consistently.
            String nfcIdentifier = toHex(tag.getId());

            if(nfcIdentifier != null){
                Log.d(TAG, nfcIdentifier);

                if (nfcIdentifier.equals(original_hex)){
                    Toast.makeText(this, "Correct Tag!", Toast.LENGTH_SHORT).show();
                    mediaPlayer.stop();

                    Intent data = new Intent(this, MainActivity.class);
                    data.putExtra(NFC_IDENTIFIER, "alarm_diffused");
                    data.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(data);
                    finish();
                }
                else{
                    Toast.makeText(this, "Wrong Tag!", Toast.LENGTH_SHORT).show();
                }
            }

        } else {

            Toast.makeText(this, "parcelable extra found null", Toast.LENGTH_LONG).show();
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
