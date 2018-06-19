package udgaman.com.vachak2.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.util.StatusDetails;

/**
 * Created by shivamawasthi on 9/19/16.
 */

public class StatusUpdateAdapter extends BaseAdapter {

    Context context;
    StatusDetails currentStatus;
    List<StatusDetails> statusList;
    LayoutInflater layoutInflater ;

    public StatusUpdateAdapter(List<StatusDetails> statusList, Context context, StatusDetails currentStatus){
        this.context = context;
        this.currentStatus = currentStatus;
        this.statusList = statusList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        StatusDetails sd = statusList.get(i);
        view = layoutInflater.inflate(R.layout.status_list_row, viewGroup, false);
        TextView status = (TextView) view.findViewById(R.id.status_list_textView1);
        TextView time = (TextView)view.findViewById(R.id.status_list_textView2);
        if(sd.getStatus().equals(currentStatus.getStatus())){
            status.setTextColor(Color.parseColor("#20A90B"));
        }
        status.setText(sd.getStatus());
        return view;
    }

    public void setAdapter(List<StatusDetails> statusList, StatusDetails currentStatus){
        this.currentStatus = currentStatus;
        this.statusList = statusList;
    }


}
