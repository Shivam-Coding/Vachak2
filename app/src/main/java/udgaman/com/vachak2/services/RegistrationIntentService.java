package udgaman.com.vachak2.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.util.QueryString;

/**
 * Created by shivamawasthi on 8/15/16.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    ArrayList<String> registerData;
    private String response="";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
        try {
            registerData = intent.getStringArrayListExtra("registerData");

            String token = FirebaseInstanceId.getInstance().getToken();
            Log.i(TAG, "GCM Registration Token: " + token);
            sendRegistrationToServer(token);

            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString(QuickPreference.SENT_TOKEN_TO_SERVER,token);
            e.commit();
            Log.i(TAG, "GCM Registration ended: " + "okay");


        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
        Intent registrationComplete = new Intent("REGISTRATION");
        registrationComplete.putExtra("REGISTRATION",response);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    private void sendRegistrationToServer(String token) throws IOException, ClassNotFoundException {
        // Add custom implementation, as needed.

        Log.i(TAG,"In the method");
        registerData.add(token);
        QueryString queryString = new QueryString("firstName",registerData.get(5));
        queryString.add("country.numberCode",registerData.get(1));
        queryString.add("country.twoLetterCode",registerData.get(2));
        queryString.add("country.threeLetterCode",registerData.get(3));
        queryString.add("phone",registerData.get(4));
        queryString.add("country.name",registerData.get(0));
        queryString.add("lastName",registerData.get(6));
        queryString.add("deviceID",registerData.get(7));
        queryString.add("status",registerData.get(8));
        queryString.add("gcm",registerData.get(9));

        URL url = null;

        url = new URL(getString(R.string.url)+"register");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(queryString.toString());
        writer.flush();
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = reader.readLine()) != null) {
          response = line;
        }
        writer.close();
        reader.close();

    }

}
