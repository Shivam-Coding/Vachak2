package udgaman.com.vachak2.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shivamawasthi on 8/28/16.
 */

public class DatabaseReader extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Vachak2.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + VachakDatabase.Friends.TABLE_NAME + " (" +
                    VachakDatabase.Friends._ID + " INTEGER PRIMARY KEY," +
                    VachakDatabase.Friends.COLUMN_NAME_PHONE + TEXT_TYPE + COMMA_SEP +
                    VachakDatabase.Friends.COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                    VachakDatabase.Friends.COLUMN_NAME_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                    VachakDatabase.Friends.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
                    VachakDatabase.Friends.COLUMN_NAME_IMAGE + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + VachakDatabase.Friends.TABLE_NAME;

    public DatabaseReader(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);

    }
}
