package udgaman.com.vachak2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.model.Countries;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.util.StatusDetails;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shivamawasthi on 9/17/16.
 */

public class DrawerAdapter extends BaseAdapter {

    LayoutInflater layoutInflater ;
    Context context;
    File profilePic,status;
    SharedPreferences sharedPreferences;


    public DrawerAdapter(Context context){
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        File path = context.getDir(QuickPreference.MAIN_DIRECTORY, Context.MODE_PRIVATE);
        sharedPreferences = context.getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
        String phone = sharedPreferences.getString(QuickPreference.PHONE,"00000000");
        profilePic  = new File(path,phone+".jpg");
        status = new File(path,phone+".txt");
    }

    @Override
    public int getCount() {
        return 3;
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
        switch (i) {
            case 0: {
                view = layoutInflater.inflate(R.layout.drawer_header, viewGroup, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.drawer_profilePic);
                if (profilePic.exists()) {
                    imageView.setImageDrawable(Drawable.createFromPath(profilePic.toString()));
                }

                break;
            }
            case 1: {
                view = layoutInflater.inflate(R.layout.drawer_status, viewGroup, false);
                TextView textView = ((TextView)view.findViewById(R.id.drawer_status_text));
                if(status.exists()){

                    try {

                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(status));
                        StatusDetails currentStatus = (StatusDetails) objectInputStream.readObject();
                        textView.setText(currentStatus.getStatus());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    textView.setText(new Countries().firstStatus());
                }
                break;
            }
            default: {
                view = layoutInflater.inflate(R.layout.drawer_list_item, viewGroup, false);
            }
        }
        return view;
    }
}
