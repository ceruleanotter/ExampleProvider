package android.example.com.exampleprovider.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;

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
        matcher.addURI(authority, ExampleEntry.TABLE_NAME, FRIEND);
        matcher.addURI(authority, ExampleEntry.TABLE_NAME + "/#", FRIEND_WITH_ID);

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

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FRIEND: {
                Cursor cursor = db.query(
                        ExampleEntry.TABLE_NAME,
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
                        ExampleEntry.TABLE_NAME,
                        projection,
                        ExampleEntry._ID + " = '" + ContentUris.parseId(uri)  + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FRIEND: {
                return ExampleEntry.CONTENT_DIR_TYPE;
            }
            case FRIEND_WITH_ID: {
                return ExampleEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FRIEND: {
                long id = db.insert(ExampleEntry.TABLE_NAME, null, contentValues);

                if (id == -1) return null; //it failed!

                //This is where you update anything that might also be watching the content provider
                getContext().getContentResolver().notifyChange(uri, null);

                return ExampleEntry.buildExampleUriWithID(id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

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
                        ExampleEntry.TABLE_NAME, null, null);
                break;
            case FRIEND_WITH_ID:
                rowsDeleted = db.delete(
                        ExampleEntry.TABLE_NAME,
                        ExampleEntry._ID + " = '" + ContentUris.parseId(uri)  + "'",
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
    public int update(Uri uri, ContentValues contentValues, String where, String[] whereargs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numberUpdated = 0;

        switch (sUriMatcher.match(uri)) {
            case FRIEND_WITH_ID: {
                numberUpdated = db.update(
                        ExampleEntry.TABLE_NAME,
                        contentValues,
                        ExampleEntry._ID + " = '" + ContentUris.parseId(uri)  + "'",
                        null
                        );
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numberUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberUpdated;

    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FRIEND:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ExampleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
