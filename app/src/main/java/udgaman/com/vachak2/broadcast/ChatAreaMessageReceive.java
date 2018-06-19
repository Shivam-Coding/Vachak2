package udgaman.com.vachak2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

import udgaman.com.vachak2.adapters.ChatAreaAdapter;
import udgaman.com.vachak2.model.Message;
import udgaman.com.vachak2.util.Notifications;

/**
 * Created by shivamawasthi on 9/6/16.
 */

public class ChatAreaMessageReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        HashMap userMessage;
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                userMessage = (HashMap) bundle.getSerializable("NewMessage");
               new Notifications(context).sendNotification(userMessage);
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
