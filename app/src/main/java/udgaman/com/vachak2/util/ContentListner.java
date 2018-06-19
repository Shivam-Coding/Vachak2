package udgaman.com.vachak2.util;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * Created by shivamawasthi on 9/8/16.
 */

public class ContentListner extends ContentObserver {


    public ContentListner(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.i("contentListner","gguhib "+selfChange);
    }
}
