package android.example.com.exampleprovider.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by lyla on 11/4/14.
 */
public class ExampleProvider extends ContentProvider{

    private ExampleDbHelper mDbHelper;
    public final static String LOG_TAG = ExampleProvider.class.getSimpleName();

    //URI Matcher Codes

    private static final int FRIEND = 100;
    private static final int FRIEND_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //URI Matchers need your content authority
        final String authority = ExampleContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ExampleContract.ExampleEntry.TABLE_NAME, FRIEND);
        matcher.addURI(authority, ExampleContract.ExampleEntry.TABLE_NAME + "/#", FRIEND_WITH_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new ExampleDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FRIEND: {
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
            case FRIEND_WITH_ID: {
                Cursor cursor = db.query(
                        ExampleContract.ExampleEntry.TABLE_NAME,
                        projection,
                        ExampleContract.ExampleEntry._ID + " = '" + ContentUris.parseId(uri)  + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FRIEND: {
                long id = db.insert(ExampleContract.ExampleEntry.TABLE_NAME, null, contentValues);

                if (id == -1) return null; //it failed!

                //This is where you update anything that might also be watching the content provider
                getContext().getContentResolver().notifyChange(uri, null);

                return ExampleContract.ExampleEntry.buildExampleUriWithID(id);
            }
            default: {
                return null;

            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case FRIEND:
                rowsDeleted = db.delete(
                        ExampleContract.ExampleEntry.TABLE_NAME, null, null);
                break;
            case FRIEND_WITH_ID:
                rowsDeleted = db.delete(
                        ExampleContract.ExampleEntry.TABLE_NAME,
                        ExampleContract.ExampleEntry._ID + " = '" + ContentUris.parseId(uri)  + "'",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
