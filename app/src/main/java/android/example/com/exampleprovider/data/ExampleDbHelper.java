package android.example.com.exampleprovider.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;
/**
 * Created by lyla on 11/3/14.
 */
public class ExampleDbHelper extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "example_database.db";

    public ExampleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_FRIEND_TABLE = "CREATE TABLE " + ExampleEntry.TABLE_NAME + " (" +
                ExampleEntry._ID + " INTEGER PRIMARY KEY," +
                ExampleEntry.NAME + " TEXT UNIQUE NOT NULL, " +
                ExampleEntry.NUMBER_OF_FRIENDS + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FRIEND_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ExampleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
