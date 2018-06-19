package udgaman.com.vachak2.activities;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.adapters.ChatAreaAdapter;
import udgaman.com.vachak2.broadcast.ChatAreaMessageReceive;
import udgaman.com.vachak2.model.Message;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.util.CheckNetworkConnection;
import udgaman.com.vachak2.util.ListConstant;
import udgaman.com.vachak2.util.Notifications;
import udgaman.com.vachak2.util.OnlineStatusUpdate;
import udgaman.com.vachak2.util.RetrieveInfo;
import udgaman.com.vachak2.util.UpdateStatus;

public class ChatAreaActivity extends AppCompatActivity {

    final static String TAG = "CHAT_AREA";
    final static int limit = 10;
    ActionBar actionBar;
    File file;
    List<Message> messageList;
    EditText message;
    ListView listView;
    String phone, name, onlineStatus ;
    SharedPreferences sharedPreferences;
    boolean keepRunning;
    private Timer timer;
    int id;
    private BroadcastReceiver messageReciever;

    ChatAreaAdapter chatAreaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_area);



        sharedPreferences = getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);

        messageList = new LinkedList<Message>();
        message = (EditText)findViewById(R.id.chat_area_editText);

        listView = (ListView)findViewById(R.id.chat_area_list_view);
        chatAreaAdapter = new ChatAreaAdapter(this);


        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        name = intent.getStringExtra("name");
        actionBar = getSupportActionBar();
        actionBar.setTitle(name);
        actionBar.setSubtitle(phone);

        File path = getApplicationContext().getDir(QuickPreference.MAIN_DIRECTORY, Context.MODE_PRIVATE);
        file  = new File(path,phone);


        try {
            file.createNewFile();
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            messageList = (List<Message>) objectInputStream.readObject();
            Message m = messageList.get(messageList.size()-1);
            id = m.getId();

        }
        catch (Exception e) {
          e.printStackTrace();
        }



//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            List<Message> messages = mapper.readValue(stringBuffer.toString(), new TypeReference<List<Message>>() { });
//            for(Message msg : messages) {
//               messageList.add(msg);
//                id = msg.getId();
//            }
//        }catch(Exception e){
//
//        }
        chatAreaAdapter.setMessageList(messageList);
        listView.setAdapter(chatAreaAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Message msg = messageList.get(i);
                if(msg.getFromPhoneNumber().equals(sharedPreferences.getString(QuickPreference.PHONE,"0000000"))) {

                    if(new CheckNetworkConnection().isOnline(getApplicationContext())){
                        new DetailsThread().execute(msg.getId());
                    }else {
                        showDetails(msg.getId());
                    }
                }

            }
        });


        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    OnlineStatusUpdate onlineStatusUpdate1 = new OnlineStatusUpdate();
                    onlineStatusUpdate1.setFriend(phone);
                    onlineStatusUpdate1.setPhonenumber(sharedPreferences.getString(QuickPreference.PHONE ,"00000000"));
                    onlineStatusUpdate1.setStatus(ListConstant.TYPING);
                    new UpdateTypingStatus().execute(onlineStatusUpdate1);
            }

            @Override
            public void afterTextChanged(Editable editable) {

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        OnlineStatusUpdate onlineStatusUpdate1 = new OnlineStatusUpdate();
                        onlineStatusUpdate1.setFriend(phone);
                        onlineStatusUpdate1.setPhonenumber(sharedPreferences.getString(QuickPreference.PHONE ,"00000000"));
                        onlineStatusUpdate1.setStatus(ListConstant.ONLINE);
                        new UpdateTypingStatus().execute(onlineStatusUpdate1);
                    }
                               }, 1000);



            }
        });


       // messageReciever = new ChatAreaMessageReceive(file,messageList,listView,chatAreaAdapter);
        messageReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HashMap userMessage;
                try {
                    Bundle bundle = intent.getExtras();
                    if(bundle!=null) {
                        userMessage = (HashMap) bundle.getSerializable("NewMessage");
                        if(userMessage != null && userMessage.containsKey(phone)) {
                            file.createNewFile();
                            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                            messageList = (List<Message>) objectInputStream.readObject();
                            Message m = messageList.get(messageList.size() - 1);
                            id = m.getId();

                            chatAreaAdapter.setMessageList(messageList);
                            chatAreaAdapter.notifyDataSetChanged();
                            new SendSeenReport().execute();
                            userMessage.remove(phone);
                            abortBroadcast();
                            new Notifications(getApplicationContext()).sendNotification(userMessage);
                        }
                    }


                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        IntentFilter intentFilter =new IntentFilter((QuickPreference.MESSAGE_RECIEVED));
        intentFilter.setPriority(2);
        registerReceiver(messageReciever,intentFilter);

        new SendSeenReport().execute();
         onlineStatus = ListConstant.ONLINE;
        keepRunning = true;
        (new Thread( new Repeat())).start();
        (new Thread( new ThreadForOnlineStatus())).start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_area_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(getApplicationContext(), ImageGalleryActivity.class));
                return true;
            case R.id.help:
                startActivity(new Intent(this, MainActivity.class));
                return true;
        }
        return false;
    }

    public void onClickSend(View V){

        String msg = message.getText().toString().trim();
        if(!msg.equals("")){
            Message m = new Message();
            m.setFromPhoneNumber(sharedPreferences.getString(QuickPreference.PHONE ,"00000000"));
            m.setToPhoneNumber(phone);
            m.setMessage(msg);
            m.setType("text");
            m.setDelivery("0");
            m.setSeen("0");

            DateTime date1 = new DateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM YYYY");
            DateTimeFormatter fmt1 = DateTimeFormat.forPattern("hh:mm a");
            String sent = date1.toString(fmt)+";"+date1.toString(fmt1);


            m.setSent(sent);
            id++;
            m.setId(id);
            messageList.add(m);
            chatAreaAdapter.setMessageList(messageList);
            chatAreaAdapter.notifyDataSetChanged();
            message.setText("");


            new SendMessageThread(id).execute(m);

        }


    }

    public void writeToFile(){
        int size = messageList.size();
        List<Message> temp;
        if(size > limit){
            temp = new LinkedList<Message>(messageList.subList(size-limit,size));
        }else{
            temp = new LinkedList<Message>(messageList);
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(temp);
            objectOutputStream.flush();
            objectOutputStream.close();
        }catch (Exception e){
         e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        keepRunning = false;
        onlineStatus = ListConstant.OFFLINE;
//        unregisterReceiver(messageReciever);
        OnlineStatusUpdate onlineStatusUpdate = new OnlineStatusUpdate();
        onlineStatusUpdate.setFriend(phone);
        onlineStatusUpdate.setPhonenumber(sharedPreferences.getString(QuickPreference.PHONE ,"00000000"));
        onlineStatusUpdate.setStatus(ListConstant.OFFLINE);
        new UpdateStatus(getString(R.string.url) + "updatestatus").execute(onlineStatusUpdate);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        keepRunning = false;
        onlineStatus = ListConstant.OFFLINE;
        OnlineStatusUpdate onlineStatusUpdate = new OnlineStatusUpdate();
        onlineStatusUpdate.setFriend(phone);
        onlineStatusUpdate.setPhonenumber(sharedPreferences.getString(QuickPreference.PHONE ,"00000000"));
        onlineStatusUpdate.setStatus(ListConstant.OFFLINE);
        new UpdateStatus(getString(R.string.url) + "updatestatus").execute(onlineStatusUpdate);
       // unregisterReceiver(messageReciever);
    }

    @Override
    protected void onPause() {
        super.onPause();
        keepRunning = false;
        onlineStatus = ListConstant.OFFLINE;
        unregisterReceiver(messageReciever);
        OnlineStatusUpdate onlineStatusUpdate = new OnlineStatusUpdate();
        onlineStatusUpdate.setFriend(phone);
        onlineStatusUpdate.setPhonenumber(sharedPreferences.getString(QuickPreference.PHONE ,"00000000"));
        onlineStatusUpdate.setStatus(ListConstant.OFFLINE);
        new UpdateStatus(getString(R.string.url) + "updatestatus").execute(onlineStatusUpdate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        keepRunning = true;
        onlineStatus = ListConstant.ONLINE;
        IntentFilter intentFilter =new IntentFilter((QuickPreference.MESSAGE_RECIEVED));
        intentFilter.setPriority(2);
        registerReceiver(messageReciever,intentFilter);
        (new Thread( new Repeat())).start();
        (new Thread( new ThreadForOnlineStatus())).start();
        new SendSeenReport().execute();
    }


    protected void showDetails(int id){

        for(Message msg : messageList){

            if(msg.getId() == id){
                final Dialog dialog = new Dialog(ChatAreaActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.message_detail);
                LinearLayout msgLinearLayout =(LinearLayout)dialog.findViewById(R.id.message_detail_msgLinearLayout);
                TextView text = (TextView) dialog.findViewById(R.id.message_detail_textView);
                TextView sentDate = (TextView) dialog.findViewById(R.id.message_detail_sent_date);
                TextView sentTime = (TextView) dialog.findViewById(R.id.message_detail_sent_time);
                TextView deliverDate = (TextView) dialog.findViewById(R.id.message_detail_deliver_date);
                TextView deliverTime = (TextView) dialog.findViewById(R.id.message_detail_deliver_time);
                TextView seenDate = (TextView) dialog.findViewById(R.id.message_detail_seen_date);
                TextView seenTime = (TextView) dialog.findViewById(R.id.message_detail_seen_time);

                if(msg.getDelivery().equals("0")) {
                    msgLinearLayout.setBackgroundColor(Color.parseColor("#FDF9F3"));
                    deliverDate.setText("-");
                    deliverTime.setText("-");
                }else {
                    msgLinearLayout.setBackgroundColor(Color.parseColor("#FAEBD7"));
                    String deliver[] = msg.getDelivery().split(";");
                    deliverDate.setText(deliver[0]);
                    deliverTime.setText(deliver[1]);
                }

                if(msg.getSeen().equals("0")){

                    seenDate.setText("-");
                    seenTime.setText("-");
                }else{
                    String seen[] = msg.getSeen().split(";");
                    seenDate.setText(seen[0]);
                    seenTime.setText(seen[1]);
                }

                text.setText(msg.getMessage());
                String sent[] = msg.getSent().split(";");



                sentDate.setText(sent[0]);
                sentTime.setText(sent[1]);





                dialog.show();

            }
        }

    }



    public class SendMessageThread extends AsyncTask<Message, Void, String>{

        String appURL = getString(R.string.url) + "upstreammessage";
        int id;


        public SendMessageThread(int id){

            this.id = id;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.equals("0")) {
                for(Message m : messageList) {
                    if(m.getId() == id) {
                        int i = messageList.indexOf(m);
                        m.setId(Integer.parseInt(s));
                        messageList.set(i,m);

                        int size = messageList.size();
                        List<Message> temp;
                        if(size > limit){
                            temp = new LinkedList<Message>(messageList.subList(size-limit,size));
                        }else{
                            temp = new LinkedList<Message>(messageList);
                        }
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            objectOutputStream.writeObject(temp);
                            objectOutputStream.flush();
                            objectOutputStream.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.i(TAG,"in post "+m.getId()+i);
                        break;
                    }
                }
            }

        }

        @Override
        protected String doInBackground(Message... messages) {

            StringBuffer stringBuffer = new StringBuffer();

            try {

                ObjectMapper objectMapper = new ObjectMapper();
                StringWriter stringEmp = new StringWriter();
                objectMapper.writeValue(stringEmp, messages[0]);

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

                Log.i(TAG,stringBuffer.toString());

            }catch (Exception e){}


            return stringBuffer.toString();
        }
    }

    public class DetailsThread extends AsyncTask<Integer, Void, String>{

        String appURL = getString(R.string.url) + "getmessagedetail";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                ObjectMapper objectMapper = new ObjectMapper();
                Message message = objectMapper.readValue(s,Message.class);
                if( message != null) {
                    final Dialog dialog = new Dialog(ChatAreaActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.message_detail);
                    LinearLayout msgLinearLayout =(LinearLayout)dialog.findViewById(R.id.message_detail_msgLinearLayout);
                    TextView text = (TextView) dialog.findViewById(R.id.message_detail_textView);
                    TextView sentDate = (TextView) dialog.findViewById(R.id.message_detail_sent_date);
                    TextView sentTime = (TextView) dialog.findViewById(R.id.message_detail_sent_time);
                    TextView deliverDate = (TextView) dialog.findViewById(R.id.message_detail_deliver_date);
                    TextView deliverTime = (TextView) dialog.findViewById(R.id.message_detail_deliver_time);
                    TextView seenDate = (TextView) dialog.findViewById(R.id.message_detail_seen_date);
                    TextView seenTime = (TextView) dialog.findViewById(R.id.message_detail_seen_time);

                    if(message.getDelivery().equals("0"))
                        msgLinearLayout.setBackgroundColor(Color.parseColor("#FDF9F3"));
                    msgLinearLayout.setBackgroundColor(Color.parseColor("#FAEBD7"));

                    text.setText(message.getMessage());
                    String sent[] = message.getSent().split(";");
                    String deliver[] = message.getDelivery().split(";");
                    String seen[] = message.getSeen().split(";");

                    for(Message msg : messageList){
                        if(msg.getId() == message.getId()){
                            msg.setSeen(message.getSent());
                            msg.setDelivery(message.getDelivery());
                            msg.setSeen(message.getSeen());
                        }
                    }

                    sentDate.setText(sent[0]);
                    sentTime.setText(sent[1]);

                    deliverDate.setText(deliver[0]);
                    deliverTime.setText(deliver[1]);

                    seenDate.setText(seen[0]);
                    seenTime.setText(seen[1]);

                    dialog.show();

//                    Toast.makeText(getApplicationContext(),"in Post",Toast.LENGTH_LONG).show();


                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Integer... integers) {

            StringBuffer stringBuffer = new StringBuffer();

            try {


                DateTime date = new DateTime();

                RetrieveInfo retrieveInfo = new RetrieveInfo();
                retrieveInfo.setDeviceID(sharedPreferences.getString(QuickPreference.DEVICE_ID,"0000000"));
                retrieveInfo.setPhoneNumber(sharedPreferences.getString(QuickPreference.PHONE,"00000000"));
                retrieveInfo.setTimeZone(date.getZone().toString());
                retrieveInfo.setId(integers[0]);


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

            }catch (Exception e){
                e.printStackTrace();
            }

             Log.i(TAG,stringBuffer.toString());
            return stringBuffer.toString();
        }
    }

    public class GetOnlineStatus extends AsyncTask<OnlineStatusUpdate, Void, String>{

        String appURL = getString(R.string.url) + "getfriendstatus";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                OnlineStatusUpdate onlineStatusUpdate = objectMapper.readValue(s,OnlineStatusUpdate.class);
                String status = onlineStatusUpdate.getFriendStatus();
                if(status.equals(ListConstant.OFFLINE)){
                    actionBar.setSubtitle(phone);
                }else{
                    actionBar.setSubtitle(status);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(OnlineStatusUpdate... onlineStatus) {
            StringBuffer stringBuffer = new StringBuffer();
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                StringWriter stringEmp = new StringWriter();
                objectMapper.writeValue(stringEmp, onlineStatus[0]);

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
            return stringBuffer.toString();
        }
    }

    public class UpdateTypingStatus extends AsyncTask<OnlineStatusUpdate, Void, Void>{

        String appURL = getString(R.string.url) + "updatetypingstatus";

        @Override
        protected Void doInBackground(OnlineStatusUpdate... onlineStatusUpdates) {
            StringBuffer stringBuffer = new StringBuffer();
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                StringWriter stringEmp = new StringWriter();
                objectMapper.writeValue(stringEmp, onlineStatusUpdates[0]);

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

    public class SendSeenReport extends AsyncTask<Void, Void, Void>{

        String appURL = getString(R.string.url) + "submitseenreport";

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            writeToFile();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuffer stringBuffer = new StringBuffer();

            try {

                RetrieveInfo retrieveInfo = new RetrieveInfo();
                retrieveInfo.setFriendNumber(phone);
                retrieveInfo.setPhoneNumber(sharedPreferences.getString(QuickPreference.PHONE,"0000000"));
                retrieveInfo.setDeviceID(sharedPreferences.getString(QuickPreference.DEVICE_ID,"0000000"));

                ObjectMapper objectMapper = new ObjectMapper();
                StringWriter stringEmp = new StringWriter();
                objectMapper.writeValue(stringEmp,retrieveInfo);

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


//                DateTime date1 = new DateTime();
//                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM YYYY");
//                DateTimeFormatter fmt1 = DateTimeFormat.forPattern("hh:mm a");
//                String seen = date1.toString(fmt)+";"+date1.toString(fmt1);
//                for(Message msg : messageList){
//                    msg.setSeen(seen);
//                }


            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }
    }


    public class Repeat implements Runnable{

        @Override
        public void run() {
            while (keepRunning) {
                try {
                    Thread.sleep(1000);
                    OnlineStatusUpdate onlineStatusUpdate = new OnlineStatusUpdate();
                    onlineStatusUpdate.setFriend(phone);
                    onlineStatusUpdate.setPhonenumber(sharedPreferences.getString(QuickPreference.PHONE ,"00000000"));
                    new GetOnlineStatus().execute(onlineStatusUpdate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ThreadForOnlineStatus implements Runnable{

        @Override
        public void run() {
            while (keepRunning) {
                try {
                    Thread.sleep(4000);
                    OnlineStatusUpdate onlineStatusUpdate1 = new OnlineStatusUpdate();
                    onlineStatusUpdate1.setFriend(phone);
                    onlineStatusUpdate1.setPhonenumber(sharedPreferences.getString(QuickPreference.PHONE ,"00000000"));
                    onlineStatusUpdate1.setStatus(onlineStatus);
                    new UpdateStatus(getString(R.string.url) + "updatestatus").execute(onlineStatusUpdate1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
