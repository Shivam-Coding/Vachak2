package udgaman.com.vachak2.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import udgaman.com.vachak2.DAO.DatabaseConnection;
import udgaman.com.vachak2.DAO.VachakDatabase;
import udgaman.com.vachak2.R;
import udgaman.com.vachak2.activities.ChatAreaActivity;
import udgaman.com.vachak2.activities.RegisterActivity;
import udgaman.com.vachak2.model.User;

/**
 * Created by shivamawasthi on 9/7/16.
 */

public class Notifications {

    Context context;

    public Notifications(Context context){
        this.context = context;
    }

    public void sendNotification(HashMap messages) {

        String messageToShow ="";
        if(messages != null && !messages.keySet().isEmpty()){
            Set<String> keys = messages.keySet();
            User ut = null;
            String s1 = null;
            for(String s: keys) {
                s1 = s;
                ut = findUser(s);
                if(ut == null)
                messageToShow = messageToShow + messages.get(s) + " Message(s) from "+ s+ "\n";
                else
                    messageToShow = messageToShow + messages.get(s) + " Message(s) from "+ ut.getFirstName()+" "+ut.getLastName()+ "\n";
            }

            Intent intent;

            if(keys.size() == 1){
                intent = new Intent(context, ChatAreaActivity.class);
                if(ut != null) {
                    intent = intent.putExtra("phone", ut.getPhone());
                    intent = intent.putExtra("name", ut.getFirstName() + " " + ut.getLastName());
                }else{
                    intent = intent.putExtra("phone", s1);
                    intent = intent.putExtra("name", s1);
                }
            }else{
                intent = new Intent(context, RegisterActivity.class);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Vachak")
                    .setContentText(messageToShow)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }





    }


    public User findUser(String key){
        SQLiteDatabase sqLiteDatabase = DatabaseConnection.getConnection(context);
        String[] projection = {
                VachakDatabase.Friends.COLUMN_NAME_FIRST_NAME,
                VachakDatabase.Friends.COLUMN_NAME_LAST_NAME,
                VachakDatabase.Friends.COLUMN_NAME_PHONE,
                VachakDatabase.Friends.COLUMN_NAME_STATUS,
                VachakDatabase.Friends.COLUMN_NAME_IMAGE
        };


        String sortOrder =
                VachakDatabase.Friends.COLUMN_NAME_FIRST_NAME + " DESC";

        Cursor c = sqLiteDatabase.query(
                VachakDatabase.Friends.TABLE_NAME,
                projection,
                VachakDatabase.Friends.COLUMN_NAME_PHONE+"=?",
                new String[]{key},
                null,
                null,
                null
        );
        User u = new User();
        while(c.moveToNext()){

            u.setFirstName(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_FIRST_NAME)));
            u.setLastName(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_LAST_NAME)));
            u.setPhone(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_PHONE)));
            u.setStatus(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_STATUS)));
            u.setThumbnail(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_IMAGE)));

        }
        sqLiteDatabase.close();

        return u;

    }


}
