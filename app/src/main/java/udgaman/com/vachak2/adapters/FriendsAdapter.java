package udgaman.com.vachak2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.activities.ChatAreaActivity;
import udgaman.com.vachak2.model.User;

/**
 * Created by shivamawasthi on 8/28/16.
 */

public class FriendsAdapter extends BaseAdapter {

    List<User> users;
    Context context;
    LayoutInflater layoutInflater ;

    public FriendsAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
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
        view = layoutInflater.inflate(R.layout.activity_friends_item_row,viewGroup,false);
        ImageView image = (ImageView)view.findViewById(R.id.friends_imageView);
        final TextView name = (TextView)view.findViewById(R.id.friends_name_textView);
        TextView status = (TextView)view.findViewById(R.id.friends_status_textView);
        TextView time = (TextView)view.findViewById(R.id.friends_time_textView);
        final TextView phone = (TextView)view.findViewById(R.id.friends_phone_textView);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.friends_linear_layout);

        User user = users.get(i);
        name.setText(user.getFirstName()+" "+user.getLastName());
        status.setText(user.getStatus());
        phone.setText(user.getPhone());

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatAreaActivity.class);
                intent = intent.putExtra("phone", phone.getText());
                intent = intent.putExtra("name", name.getText());
                context.startActivity(intent);
            }
        });


        return view;
    }
}
