package udgaman.com.vachak2.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by shivamawasthi on 8/29/16.
 */

public class DatabaseConnection {

    private static SQLiteDatabase sqLiteDatabase;

    public static SQLiteDatabase getConnection(Context context){
        DatabaseReader databaseReader = new DatabaseReader(context);
        sqLiteDatabase = databaseReader.getWritableDatabase();
        return sqLiteDatabase;
    }


}
