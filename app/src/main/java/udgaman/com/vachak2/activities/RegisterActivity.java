package udgaman.com.vachak2.activities;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import udgaman.com.vachak2.DAO.DatabaseConnection;
import udgaman.com.vachak2.DAO.VachakDatabase;
import udgaman.com.vachak2.R;
import udgaman.com.vachak2.adapters.CountrySpinnerAdapter;
import udgaman.com.vachak2.model.Countries;
import udgaman.com.vachak2.model.Friend;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.model.User;
import udgaman.com.vachak2.services.RegistrationIntentService;

public class RegisterActivity extends AppCompatActivity {

    final static String TAG = "REGISTER";

    Spinner registerCountrySpinner;
    EditText phone, registerFirstName, registerLastName;
    PhoneNumberUtil phoneUtil;

    String phoneNumber, deviceid, countryISO, userPhone;
    Countries countries;
    PhoneNumber phoneNumber1;

    SharedPreferences sharedPreferences;
    SQLiteDatabase sqLiteDatabase;

    private BroadcastReceiver registrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
        String token = sharedPreferences.getString(QuickPreference.SENT_TOKEN_TO_SERVER,"fail");
        if(sharedPreferences.getBoolean(QuickPreference.REGISTRATION_COMPLETE,false) && !token.equals("fail") ){
            Intent intent = new Intent(this,FriendsActivity.class);
            startActivity(intent);
            finish();

        }else {

            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            try {
                phoneNumber = manager.getLine1Number();
            } catch (Exception s) {
                phoneNumber = null;
            }
            registerCountrySpinner = (Spinner) findViewById(R.id.register_country_spinner);
            CountrySpinnerAdapter countrySpinnerAdapter = new CountrySpinnerAdapter(this);
            registerCountrySpinner.setAdapter(countrySpinnerAdapter);
            String code = manager.getNetworkCountryIso();

            countries = new Countries();


            if (!code.equals(null)) {
                int pos = countries.findCode(code.toUpperCase());

                registerCountrySpinner.setSelection(pos);
            }

            phone = (EditText) findViewById(R.id.register_phone);
            phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

            if (phoneNumber != null) {
                try {
                    phoneUtil = PhoneNumberUtil.getInstance();
                    PhoneNumber pho = phoneUtil.parse(phoneNumber, countries.countryISO(registerCountrySpinner.getSelectedItemPosition()));
                    phone.setText(phoneUtil.format(pho, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
                } catch (NumberParseException e) {

                }
            }

            deviceid = manager.getDeviceId();
            if (deviceid.equals(null)) {
                Toast.makeText(this, "DeviceId not present!!", Toast.LENGTH_LONG).show();
                finish();
            }


            registrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String response = intent.getExtras().getString("REGISTRATION");
                    if (!response.equals("fail")) {

                        new Intitialization().execute(response);

                    }
                }
            };

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(registrationBroadcastReceiver,
                new IntentFilter("REGISTRATION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationBroadcastReceiver);

    }


    public void registerButtonPress(View v) {

        if (validation()) {

            ((ProgressBar) findViewById(R.id.register_progress_bar)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.register_button)).setVisibility(View.INVISIBLE);
            // disableBack = true;
            //  sendRegisterData();

//            if (checkPlayServices()) {
//                // Start IntentService to register this application with GCM.
            Log.d("register", "button Clicked");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putStringArrayListExtra("registerData", sendRegisterData());

            startService(intent);
//            }


        }

    }

    public boolean validation() {
        String ph0 = phone.getText().toString().trim();
        try {
            registerFirstName = (EditText) findViewById(R.id.register_first_name);
            registerLastName = (EditText) findViewById(R.id.register_last_name);
            String name1 = registerFirstName.getText().toString().trim();
            String name2 = registerLastName.getText().toString().trim();
            String ph1 = "";
            if (!ph0.equals("") && !name1.equals("") && !name2.equals("")) {
                phoneNumber1 = phoneUtil.parse(ph0, countries.countryISO(registerCountrySpinner.getSelectedItemPosition()));

                int c = Integer.parseInt(countries.countryCode(registerCountrySpinner.getSelectedItemPosition()));
                if (phoneUtil.isValidNumber(phoneNumber1) && c == phoneNumber1.getCountryCode()) {

                } else {
                    Toast.makeText(this, "Number not valid !!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                return true;
            } else {
                Toast.makeText(this, "Field(s) empty", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberParseException e) {
            Toast.makeText(this, "Number not correct!!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    public ArrayList<String> sendRegisterData() {
        ArrayList dataToSend;
        int pos = registerCountrySpinner.getSelectedItemPosition();
//        data = model.country(pos);

        dataToSend = new ArrayList();
        dataToSend.add(countries.country(pos)); // country
        dataToSend.add(countries.countryCode(pos)); //countryCode
        dataToSend.add(countries.countryISO(pos)); //countryISO
        countryISO = countries.countryISO(pos);
        dataToSend.add(countries.countryThreeISOCode(pos));  //countryThreeISOCode
        dataToSend.add(phoneUtil.format(phoneNumber1, PhoneNumberUtil.PhoneNumberFormat.E164)); // phone #
        userPhone = phoneUtil.format(phoneNumber1, PhoneNumberUtil.PhoneNumberFormat.E164);
//        SharedPreferences.Editor editor = sharedPreferences1.edit();
//        editor.putString(QuickstartPreferences.PHONE, phoneUtil.format(phoneNumber1, PhoneNumberUtil.PhoneNumberFormat.E164));
//        editor.putString(QuickstartPreferences.COUNTRY, model.countryISO(countrySpinner.getSelectedItemPosition()));


        dataToSend.add(registerFirstName.getText().toString().trim()); // first name
        dataToSend.add(registerLastName.getText().toString().trim());  // last name


        dataToSend.add(deviceid);  // device ID
//        editor.putString(QuickstartPreferences.DEVICE_ID, deviceid);

        dataToSend.add(countries.firstStatus());  // status

//        editor.putString(QuickstartPreferences.STATUS,model.firstStatus());

//        editor.commit();
//        RegistrationDataToServer data = new RegistrationDataToServer();
//        RegistrationDataToServer.registrationData = dataToSend;

        return dataToSend;
    }


    public class Intitialization extends AsyncTask<String,Object,String> {

        String appURL = getString(R.string.url) + "initialization";
        @Override
        protected void onPostExecute(String users) {
            super.onPostExecute(users);
            Log.i(TAG,"In thread post");

            ObjectMapper mapper = new ObjectMapper();
            try {
                List<User> userList = mapper.readValue(users, new TypeReference<List<User>>() { });
                for(User u : userList) {
                    ContentValues values = new ContentValues();
                    values.put(VachakDatabase.Friends.COLUMN_NAME_FIRST_NAME, u.getFirstName());
                    values.put(VachakDatabase.Friends.COLUMN_NAME_LAST_NAME, u.getLastName());
                    values.put(VachakDatabase.Friends.COLUMN_NAME_PHONE,u.getPhone());
                    values.put(VachakDatabase.Friends.COLUMN_NAME_STATUS, u.getStatus());
                    values.put(VachakDatabase.Friends.COLUMN_NAME_IMAGE, u.getThumbnail());
                    sqLiteDatabase.insert(VachakDatabase.Friends.TABLE_NAME, null, values);
                }
            }catch(Exception e){
                Log.e(TAG,e.toString());
            }finally {
                sqLiteDatabase.close();
            }

            ((ProgressBar) findViewById(R.id.register_progress_bar)).setVisibility(View.INVISIBLE);
            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString(QuickPreference.PHONE,userPhone);
            e.putBoolean(QuickPreference.REGISTRATION_COMPLETE,true);
            e.putString(QuickPreference.DEVICE_ID,deviceid);
            e.commit();
            String tokenValue = sharedPreferences.getString(QuickPreference.SENT_TOKEN_TO_SERVER,"fail");
            if(!tokenValue.equals("fail")) {
                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(intent);
                finish();
            }else{
                finish();
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG,"In thread");

        }

        @Override
        protected String doInBackground(String... objects) {

            ContentResolver cr = getContentResolver();
            StringBuffer stringBuffer = new StringBuffer();
            String projection[] = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
            String sort_order = ContactsContract.Contacts.SORT_KEY_PRIMARY;
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, sort_order);
            String phone = null;
            Set<Friend> friends = new HashSet<Friend>();
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                    System.out.println("name : " + name + ", ID : " + id);


                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//                        if(phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE || phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_HOME || phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_WORK || phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MAIN || phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE ) {
                            {
                                phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                try {
                                    Phonenumber.PhoneNumber pho = phoneUtil.parse(phone, countryISO );
                                    String phon = phoneUtil.format(pho, PhoneNumberUtil.PhoneNumberFormat.E164);
                                    if(!userPhone.equals(phon)) {
                                        Friend friend = new Friend();
                                        friend.setName(name);
                                        friend.setNumber(phon);
                                        User u = new User();
                                        Log.i(TAG,objects[0]);
                                        u.setId(Integer.parseInt(objects[0]));
                                        friend.setUserNumber(userPhone);
                                        friends.add(friend);
                                    }
                                }catch (NumberParseException e){

                                }


                            }
                        }
                        pCur.close();
//
                    }

                }


            }
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                StringWriter stringEmp = new StringWriter();
                //objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                objectMapper.writeValue(stringEmp, friends);
                Log.i("REGISTER",stringEmp.toString());


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

                Log.i("REGISTER",connection.getResponseMessage());
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);

                }
                writer.close();
                reader.close();
            }catch(Exception e){
                Log.i(TAG,e.toString());
            }
            sqLiteDatabase = DatabaseConnection.getConnection(getApplicationContext());
            return stringBuffer.toString();

        }
    }


}
