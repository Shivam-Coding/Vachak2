package udgaman.com.vachak2.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.model.Message;
import udgaman.com.vachak2.model.QuickPreference;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shivamawasthi on 8/30/16.
 */

public class ChatAreaAdapter extends BaseAdapter {

    final static String TAG = "chatAreaAdapter";
    SharedPreferences sharedPreferences;
    String date1, date2;

    List<Message> messageList;
    Context context;
    LayoutInflater layoutInflater ;

    public ChatAreaAdapter(Context context){
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferences = context.getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
        date1 = "";
    }

    public void setMessageList(List<Message> messageList){

        this.messageList = messageList;

    }


    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int i) {
        return messageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.activity_chat_area_text_row,viewGroup,false);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.chat_area_text_row_linearLayer);
        LinearLayout msgLinearLayout =(LinearLayout)view.findViewById(R.id.chat_area_text_row_msgLinearLayout);
        TextView textView = (TextView)view.findViewById(R.id.chat_area_text_row_textView);
        TextView date = (TextView)view.findViewById(R.id.chat_area_text_row_date);
        TextView time = (TextView)view.findViewById(R.id.chat_area_text_row_time);
        Message message = messageList.get(i);

        if(message.getFromPhoneNumber().equals(sharedPreferences.getString(QuickPreference.PHONE,"0000000"))){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) msgLinearLayout.getLayoutParams();
            params.setMargins(40, 0, 0, 0); //substitute parameters for left, top, right, bottom
            msgLinearLayout.setLayoutParams(params);
            linearLayout.setGravity(Gravity.RIGHT);

            if(message.getDelivery().equals("0"))
            msgLinearLayout.setBackgroundColor(Color.parseColor("#FDF9F3"));
            msgLinearLayout.setBackgroundColor(Color.parseColor("#FAEBD7"));

            String[] sd = message.getSent().split(";");
            setDate(i,true);
            time.setText(sd[1]);
            if (!date1.equals(date2)) {
                date.setText(date2);
                date.setVisibility(View.VISIBLE);

            } else {
                date.setVisibility(View.GONE);
            }

        }else{
            linearLayout.setGravity(Gravity.LEFT);
            msgLinearLayout.setBackgroundColor(Color.parseColor("#77DDFF"));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) msgLinearLayout.getLayoutParams();
            params.setMargins(0, 0, 40, 0); //substitute parameters for left, top, right, bottom
            msgLinearLayout.setLayoutParams(params);
            String[] sd = message.getDelivery().split(";");
            setDate(i,false);
            time.setText(sd[1]);
            if (!date1.equals(date2)) {
                date.setText(date2);
                date.setVisibility(View.VISIBLE);

            } else {
                date.setVisibility(View.GONE);
            }

        }


        textView.setText(message.getMessage());

        return view;
    }


    protected void setDate(int p, boolean b){
        Message message = messageList.get(p);
        String[] sd,sd1;
        if(p != 0) {
            Message m = messageList.get(p-1);
            if (b){
                sd = message.getSent().split(";");
                    if(m.getFromPhoneNumber().equals(sharedPreferences.getString(QuickPreference.PHONE,"0000000"))) {
                        sd1 = m.getSent().split(";");
                    }else{
                        sd1 = m.getDelivery().split(";");
                    }
            } else {
                sd = message.getDelivery().split(";");
                if(m.getFromPhoneNumber().equals(sharedPreferences.getString(QuickPreference.PHONE,"0000000"))) {
                    sd1 = m.getSent().split(";");
                }else{
                    sd1 = m.getDelivery().split(";");
                }
            }

            date1 = sd1[0];
            date2 = sd[0];
        }else{
            if(b){
                sd = message.getSent().split(";");
            }else{
                sd = message.getDelivery().split(";");
            }

            date1 = "";
            date2 = sd[0];
        }

        }



}
