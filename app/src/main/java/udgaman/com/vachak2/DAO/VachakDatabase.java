package udgaman.com.vachak2.DAO;

import android.provider.BaseColumns;

/**
 * Created by shivamawasthi on 8/28/16.
 */

public final class VachakDatabase {

    private VachakDatabase(){}

    public static class Friends implements BaseColumns{
        public static final String TABLE_NAME = "friends";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_IMAGE = "image";
    }
}
