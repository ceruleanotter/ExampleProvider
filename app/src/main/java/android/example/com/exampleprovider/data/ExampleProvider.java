package android.example.com.exampleprovider.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by lyla on 11/4/14.
 */
public class ExampleProvider extends ContentProvider{

    private ExampleDbHelper mDbHelper;
    public final static String LOG_TAG = ExampleProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mDbHelper = new ExampleDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Log.e(LOG_TAG, "the uri is " + uri + "\nAnd the constant uri is " + ExampleContract.ExampleEntry.TABLE_URI);


        if (uri.equals(ExampleContract.ExampleEntry.TABLE_URI)) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            Cursor cursor = db.query(
                    ExampleContract.ExampleEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            return cursor;
        }

        long id;
        try {
            id = ContentUris.parseId(uri);
            if (uri.equals(ExampleContract.ExampleEntry.buildExampleUriWithID(id))) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                Cursor cursor = db.query(
                        ExampleContract.ExampleEntry.TABLE_NAME,
                        projection,
                        ExampleContract.ExampleEntry._ID + " = '" + id + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }

        } catch (NumberFormatException e) {
            return null;

        } catch (UnsupportedOperationException e) {
            return null;
        }
        return null;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
