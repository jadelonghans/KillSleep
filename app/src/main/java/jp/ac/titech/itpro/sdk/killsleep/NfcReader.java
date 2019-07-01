package jp.ac.titech.itpro.sdk.killsleep;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class NfcReader extends AppCompatActivity {
    public static final String INTENT_EXTRA = "xyz" ;
    public static final String NFC_IDENTIFIER = "wxyz" ;
    private final static String TAG = NfcReader.class.getSimpleName();

    private NfcAdapter nfcAdapter;
    private final static int REQ_ENABLE_NFC = 1111;

//    Intent intent;
//    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate NfcReader");
        setContentView(R.layout.nfc_reader);

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
//            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            
//            new ReaderTask().execute() ;
        }
    }

    // copied starts here
    private void handleIncomingDataIntent(Intent intent) {
        Log.d(TAG, "type:" + intent.getType());
//        if ("text/plain".equals(intent.getType())) {
        if(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) != null){
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);

            // directly doing tag.getId() gave inconsistent values every time, converting to hex gave same codes consistently.
            String nfcIdentifier = toHex(tag.getId());
            Log.d(TAG, nfcIdentifier);

            if(nfcIdentifier != null){
                Intent data = new Intent();
                data.putExtra(NFC_IDENTIFIER, nfcIdentifier);
                setResult(RESULT_OK, data);
                finish();
            }

//            NdefMessage ndefMessage = ndef.getCachedNdefMessage();


//            NdefRecord[] records = ndefMessage.getRecords();
//            for (NdefRecord ndefRecord : records) {
//                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
//                    // debug purpose
////                    Log.d(TAG,readNfcTagText(ndefRecord));
//
//                    if(readNfcTagText(ndefRecord).equals("<¨D.")) {
//                        Toast.makeText(this, "alarm tag attached", Toast.LENGTH_LONG).show();
////                        stopAlarm();
//                        finish();
//                    } else {
//                        Toast.makeText(this, "You are using a wrong tag", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
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

    /* not in use
    private String readNfcTagText(NdefRecord ndefRecord)  {
        byte[] payload = ndefRecord.getPayload(); // i.e {2, 101, 110, 97, 108, 97, 114, 109} represents (start character) enalarm, en is english
        Charset textEncodingCharset = Charset.forName(((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16");
        int languageCodeLength = payload[0] & 51;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncodingCharset);
    }

    private NdefMessage createNdefMessage(boolean addAndroidApplicationRecord) throws UnsupportedEncodingException {
        String text = "alarm";
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("UTF-8");

        byte[] payload = new byte[1 + langBytes.length + textBytes.length];
        payload[0] = (byte) langBytes.length;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langBytes.length);
        System.arraycopy(textBytes, 0, payload, 1 + langBytes.length, textBytes.length);
        NdefRecord rtdTextRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        if (addAndroidApplicationRecord) { // add a record to launch an application on tag discovery
            return new NdefMessage(
                    new NdefRecord[] { rtdTextRecord, NdefRecord.createApplicationRecord("your app package id") }
            );
        } else {
            return new NdefMessage(new NdefRecord[] { rtdTextRecord });
        }
    }
    */
    // copied until here

//    private class ReaderTask extends AsyncTask<Tag, Void, String>{
//        @Override
//        protected String doInBackground(Tag... tags) {
////            Tag tag =
//            return null;
//        }
//    }
}
