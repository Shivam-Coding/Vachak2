package udgaman.com.vachak2.util;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import udgaman.com.vachak2.R;

/**
 * Created by shivamawasthi on 9/6/16.
 */

public class UpdateStatus extends AsyncTask<OnlineStatusUpdate, Void, Void>{

    String appURL;

    public UpdateStatus(String appURL){
        this.appURL = appURL;
    }


    @Override
    protected Void doInBackground(OnlineStatusUpdate... onlineStatus) {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter stringEmp = new StringWriter();
            objectMapper.writeValue(stringEmp, onlineStatus[0]);

            StringBuffer stringBuffer = new StringBuffer();
            URL url = null;


            url = new URL(appURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(stringEmp.toString());
            writer.flush();
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);

            }
            writer.close();
            reader.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
