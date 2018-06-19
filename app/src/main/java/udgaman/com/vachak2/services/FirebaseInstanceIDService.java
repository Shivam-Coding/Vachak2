package udgaman.com.vachak2.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.model.User;
import udgaman.com.vachak2.util.QueryString;

/**
 * Created by shivamawasthi on 9/1/16.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

   private final static String TAG = "FIREBASEINSTANCEID";


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendToServer(refreshedToken);



    }

    public void sendToServer(String token){
        String line;
        SharedPreferences sharedPreferences = getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
        try {


                QueryString queryString = new QueryString("phone", sharedPreferences.getString(QuickPreference.PHONE, "00000000"));
                queryString.add("gcm", token);

                URL url = null;

                url = new URL(getString(R.string.url) + "updateGCM");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(queryString.toString());
                writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            line = reader.readLine();
                writer.close();
                reader.readLine();
                Log.i(TAG,"in loop");
            SharedPreferences.Editor e = sharedPreferences.edit();
             if(!line.equals("success")){
                 e.putString(QuickPreference.SENT_TOKEN_TO_SERVER,"fail");

             }else{
                 e.putString(QuickPreference.SENT_TOKEN_TO_SERVER, token);
             }
            e.commit();

        }catch (Exception e){
            SharedPreferences.Editor e1 = sharedPreferences.edit();
            e1.putString(QuickPreference.SENT_TOKEN_TO_SERVER,"fail");
            e1.commit();
          Log.e(TAG,e.toString());
        }
    }

}
