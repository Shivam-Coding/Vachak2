package udgaman.com.vachak2.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.activities.RegisterActivity;
import udgaman.com.vachak2.model.GCMCodes;
import udgaman.com.vachak2.model.Message;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.util.ListConstant;
import udgaman.com.vachak2.util.RetrieveInfo;

/**
 * Created by shivamawasthi on 9/1/16.
 */

public class VachakFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";
    SharedPreferences sharedPreferences;
    HashMap<String,Integer> userMessages;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        sharedPreferences = getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            int code = Integer.parseInt(remoteMessage.getData().get("code"));
            switch (code){
                case ListConstant.newMessage : {
                    downStreamMessage();
                    Intent messageRecieved = new Intent(QuickPreference.MESSAGE_RECIEVED);
//                    messageRecieved.putStringArrayListExtra("from", from);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("NewMessage", userMessages);
                    messageRecieved.putExtras(bundle);
                    sendOrderedBroadcast(messageRecieved,null);
//                    sendNotification("hello");
                }case ListConstant.deliveryMessageCode : {

                    String info = remoteMessage.getData().get("info");

                }
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }


//    /**
//     * Create and show a simple notification containing the received FCM message.
//     *
//     * @param messageBody FCM message body received.
//     */
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, RegisterActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.in)
//                .setContentTitle("FCM Message")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }


    public void retrieveMessage(){
        DateTime date = new DateTime();
        Log.d(TAG, date.getZone().toString());
    }


    public void downStreamMessage(){

        String appURL = getString(R.string.url) + "downstreammessage";

        StringBuffer stringBuffer = new StringBuffer();

        try {

            DateTime date = new DateTime();

            RetrieveInfo retrieveInfo = new RetrieveInfo();
            retrieveInfo.setDeviceID(sharedPreferences.getString(QuickPreference.DEVICE_ID,"0000000"));
            retrieveInfo.setPhoneNumber(sharedPreferences.getString(QuickPreference.PHONE,"00000000"));
            retrieveInfo.setTimeZone(date.getZone().toString());


            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter stringEmp = new StringWriter();
            objectMapper.writeValue(stringEmp, retrieveInfo);

            URL url = null;


            url = new URL(appURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type","application/json");
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


            ObjectMapper mapper = new ObjectMapper();
            File path = getApplicationContext().getDir(QuickPreference.MAIN_DIRECTORY, Context.MODE_PRIVATE);
            List<Message> messages = mapper.readValue(stringBuffer.toString(), new TypeReference<List<Message>>() { });
            if(!messages.isEmpty()) {
                for (Message msg : messages) {
                    File file = new File(path, msg.getFromPhoneNumber());
//                    File file1 = new File(path, messages.get(0).getFromPhoneNumber());

                    userMessages = new HashMap<String,Integer>();
                    Integer count = userMessages.get(msg.getFromPhoneNumber());
                    if(count == null){
                        userMessages .put(msg.getFromPhoneNumber(),new Integer(1));
                    }else {
                        count = count + 1;
                        userMessages.put(msg.getFromPhoneNumber(),count);
                    }

                    file.createNewFile();
                    FileInputStream fileInputStream = new FileInputStream(file);
                    if (fileInputStream.available() != 0) {
                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                        List<Message> messageList = (List<Message>) objectInputStream.readObject();

                        messageList.add(msg);
                        objectInputStream.close();

                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                        objectOutputStream.writeObject(messageList);
                        objectOutputStream.close();
                    } else {

                        List<Message> m = new ArrayList<Message>(1);
                        m.add(msg);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                        objectOutputStream.writeObject(m);
                        objectOutputStream.close();
                    }

                }
            }





            Log.i(TAG,stringBuffer.toString());

        }catch (Exception e){
            Log.d(TAG,e.toString());
            e.printStackTrace();
        }
    }




}
