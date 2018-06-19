package udgaman.com.vachak2.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.adapters.StatusUpdateAdapter;
import udgaman.com.vachak2.model.Countries;
import udgaman.com.vachak2.model.Message;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.util.StatusDetails;
import udgaman.com.vachak2.util.UpdateProfileStatus;

public class StatusUpdate extends AppCompatActivity implements View.OnClickListener{

    File status;
    SharedPreferences sharedPreferences;
    StatusDetails currentStatus;
    List<StatusDetails> statusList;
    EditText newStatus;
    Button update,likes,comments;
    StatusUpdateAdapter statusUpdateAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);
        statusList = new ArrayList<StatusDetails>();
        File path = getDir(QuickPreference.MAIN_DIRECTORY, Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
        String phone = sharedPreferences.getString(QuickPreference.PHONE,"00000000");
        status = new File(path,phone+".txt");
        if(status.exists()) {
            try {

                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(status));
                currentStatus = (StatusDetails) objectInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
               // Toast.makeText(this,"jhgghvm",Toast.LENGTH_SHORT).show();
            }



        }else{

            currentStatus = new StatusDetails();
            currentStatus.setStatus(new Countries().firstStatus());
            currentStatus.setLikes(0);
            currentStatus.setComments(0);

        }
        statusList.add(currentStatus);
        progressBar = (ProgressBar)findViewById(R.id.status_progressBar);
        newStatus = (EditText)findViewById(R.id.status_editText);
        update = (Button)findViewById(R.id.status_button_update);
        update.setOnClickListener(this);
        likes = (Button)findViewById(R.id.status_button_like) ;
        likes.setOnClickListener(this);
        comments = (Button)findViewById(R.id.status_button_comment);
        ListView listView = (ListView)findViewById(R.id.status_listView);
        statusUpdateAdapter = new StatusUpdateAdapter(statusList,this,currentStatus);
        listView.setAdapter(statusUpdateAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StatusDetails sd = statusList.get(i);
                newStatus.setText(sd.getStatus());
                likes.setText(sd.getLikes()+" Likes");
                comments.setText(sd.getComments()+" Comments");
            }
        });

    }

    @Override
    public void onClick(View view) {

        String tag = (String) view.getTag();
        if(tag.equals("update")){
            String st = newStatus.getText().toString().trim();
            if(!st.equals("")){
                progressBar.setVisibility(View.VISIBLE);
                new UpdateStatusThread().execute(st);
            }
        }

    }


    protected class UpdateStatusThread extends AsyncTask<String, Void, Boolean>{

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
             statusList = new ArrayList<StatusDetails>(); // have to change
                statusList.add(currentStatus);
                newStatus.setText("");
                statusUpdateAdapter.setAdapter(statusList,currentStatus);
                statusUpdateAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            Boolean re = false;
            String appURL = getString(R.string.url) + "updateprofilestatus";
            StringBuffer stringBuffer = new StringBuffer();

            try {

                SharedPreferences sharedPreferences = getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
                DateTime date = new DateTime();
                UpdateProfileStatus updateProfileStatus = new UpdateProfileStatus();
                updateProfileStatus.setStatus(strings[0]);
                updateProfileStatus.setDeviceID(sharedPreferences.getString(QuickPreference.DEVICE_ID,"0000000"));
                updateProfileStatus.setPhone(sharedPreferences.getString(QuickPreference.PHONE,"00000000"));
                updateProfileStatus.setTimeZone(date.getZone().toString());

                ObjectMapper objectMapper = new ObjectMapper();
                StringWriter updateProfile = new StringWriter();
                objectMapper.writeValue(updateProfile, updateProfileStatus);

                URL url = null;


                url = new URL(appURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type","application/json");
                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(updateProfile.toString());
                writer.flush();
                String line;

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);

                }
                writer.close();
                reader.close();

                currentStatus = new StatusDetails();
                currentStatus.setComments(0);
                currentStatus.setLikes(0);
                currentStatus.setStatus(strings[0]);

                // things to implement get id , date, time from server

                    FileOutputStream outputStream = new FileOutputStream(status);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(currentStatus);
                    objectOutputStream.flush();
                    objectOutputStream.close();

                re = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return re;
        }
    }


}
