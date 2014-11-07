/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.example.com.exampleprovider.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;
import android.net.Uri;

/**
 * {@link ExampleProvider} is a ContentProvider for the friends database. This content provider
 * works with {@link ExampleContract} and {@link ExampleDbHelper} to provide managed and secure
 * access to the friends database.
 */
public class ExampleProvider extends ContentProvider{

    private ExampleDbHelper mDbHelper;

    //URI Matcher Codes
    private static final int FRIEND = 100;
    private static final int FRIEND_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Builds a UriMatcher object for the friends database URIs
     * @return
     */
    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI you want to add, create a corresponding code. Not that URIMatchers
        // Need your content authority.
        matcher.addURI(ExampleContract.CONTENT_AUTHORITY, ExampleEntry.PATH_FRIENDS, FRIEND);
        matcher.addURI(ExampleContract.CONTENT_AUTHORITY, ExampleEntry.PATH_FRIENDS + "/#", FRIEND_WITH_ID);

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
                return db.query(
                        ExampleEntry.PATH_FRIENDS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            case FRIEND_WITH_ID: {
                return db.query(
                        ExampleEntry.PATH_FRIENDS,
                        projection,
                        ExampleEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    //TODO in insert, check content values; check that something
    //Check for null, if it's null, throw IllegalArgumentException
    //for ratings, also check 1-5
    //put validation in a common method so that it can be used in on upgrade as well
    //add these validation methods to the contract
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FRIEND: {
                long id = db.insert(ExampleEntry.PATH_FRIENDS, null, contentValues);

                if (id == -1) return null; //it failed!

                //This is where you update anything that might also be watching the content provider
                getContext().getContentResolver().notifyChange(uri, null);

                return ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    //TODO
    //If return count is not = 0 then notify
    //add comments on this
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FRIEND:

                //Allows you to issue multiple transactions and then have them executed in a batch
                db.beginTransaction();

                //Counts the number of inserts that are successful
                int numberInserted = 0;
                try {
                    for (ContentValues value : values) {
                        //Try to insert
                        long _id = db.insert(ExampleEntry.PATH_FRIENDS, null, value);
                        //As long as the insert didn't fail, increment the numberInserted
                        if (_id != -1) {
                            numberInserted++;
                        }
                    }
                    //If you get to the end without an exception, set the transaction as successful
                    //No further database operations should be done after this call.
                    db.setTransactionSuccessful();
                } finally {
                    //Causes all of the issued transactions to occur at once
                    db.endTransaction();
                }
                if (numberInserted > 0) {
                    //Notifies the content resolver that the underlying data has changed
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numberInserted;
            default:
                //The default case is not optimized
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    //TODO Also do valiations here
    public int update(Uri uri, ContentValues contentValues, String where, String[] whereargs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numberUpdated = 0;

        switch (sUriMatcher.match(uri)) {
            case FRIEND_WITH_ID: {
                numberUpdated = db.update(
                        ExampleEntry.PATH_FRIENDS,
                        contentValues,
                        ExampleEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))}
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numberDeleted;
        switch (match) {
            case FRIEND:
                numberDeleted = db.delete(
                        ExampleEntry.PATH_FRIENDS, null, null);
                break;
            case FRIEND_WITH_ID:
                numberDeleted = db.delete(
                        ExampleEntry.PATH_FRIENDS,
                        ExampleEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // The first condition works because a null deletes all rows
        if (selection == null || numberDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberDeleted;
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
}