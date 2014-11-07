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
package android.example.com.exampleprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.example.com.exampleprovider.data.ExampleContract;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;

/**
 * This is a collection of tests for the associated Content Provider. See
 * {@link android.example.com.exampleprovider.data.ExampleProvider}
 */
public class TestExampleProvider extends AndroidTestCase {

    @Override
    // Setup is called before each test
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteAllRecords();
    }

    /**
     * Helper method to delete all of the record in the database.
     */
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                ExampleEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    /**
     * Helper method to create rows in the database to help perform further tests.
     */
    public ContentValues[] createDummyData() {
        String nameOne = "Dan";
        String nameTwo = "Katherine";
        int friendsOne = 444;
        int friendsTwo = 555;

        ContentValues[] values = new ContentValues[2];
        values[0] = new ContentValues();
        values[0].put(ExampleEntry.NAME, nameOne);
        values[0].put(ExampleEntry.NUMBER_OF_FRIENDS, friendsOne);

        values[1] = new ContentValues();
        values[1].put(ExampleEntry.NAME, nameTwo);
        values[1].put(ExampleEntry.NUMBER_OF_FRIENDS, friendsTwo);
        return values;
    }

    /**
     * Tests {@link android.example.com.exampleprovider.data.ExampleProvider}'s insert method.
     */
    public void testInsert() throws Throwable {
        ContentResolver resolver =  mContext.getContentResolver();

        ContentValues[] values = createDummyData();
        Uri[] uris = new Uri[values.length];

        for(int i = 0; i < values.length; i++) {
            uris[i] = resolver.insert(
                    ExampleContract.ExampleEntry.CONTENT_URI, values[i]
            );
        }

        Cursor cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(values.length, cursor.getCount());
        cursor.close(); // Always close your cursor!

        for(int i = 0; i < uris.length; i++) {
            cursor = mContext.getContentResolver().query(
                    uris[i],
                    null,
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();
            assertEquals(cursor.getString(cursor.getColumnIndex(
                            ExampleContract.ExampleEntry.NAME)),
                    values[i].getAsString(ExampleEntry.NAME));
            assertEquals(cursor.getInt(cursor.getColumnIndex(
                            ExampleContract.ExampleEntry.NUMBER_OF_FRIENDS)),
                    values[i].getAsInteger(ExampleEntry.NUMBER_OF_FRIENDS).intValue());
            cursor.close();
        }

    }

    /**
     * Tests {@link android.example.com.exampleprovider.data.ExampleProvider}'s bulk insert method.
     */
    public void testBulkInsert(){
        ContentResolver resolver =  mContext.getContentResolver();
        ContentValues[] values = createDummyData();

        resolver.bulkInsert(ExampleEntry.CONTENT_URI, values);

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(2, cursor.getCount());

        cursor.moveToFirst();
        int i = 1;
        while (cursor.moveToNext()) {
            assertEquals(cursor.getString(cursor.getColumnIndex(
                            ExampleContract.ExampleEntry.NAME)),
                    values[i].getAsString(ExampleContract.ExampleEntry.NAME));
            assertEquals(cursor.getInt(cursor.getColumnIndex(
                            ExampleContract.ExampleEntry.NUMBER_OF_FRIENDS)),
                    values[i].getAsInteger(ExampleEntry.NUMBER_OF_FRIENDS).intValue()
            );
            i--;
        }

        cursor.close();

    }

    /**
     * Tests {@link android.example.com.exampleprovider.data.ExampleProvider}'s update by changing
     * one values in one row.
     */
    public void testUpdateEntry() throws Throwable {
        ContentResolver resolver =  mContext.getContentResolver();

        ContentValues[] values = createDummyData();
        Uri[] uris = new Uri[values.length];

        for(int i = 0; i < values.length; i++) {
            uris[i] = resolver.insert(
                    ExampleContract.ExampleEntry.CONTENT_URI, values[i]
            );
        }

        int friendsToAdd = 100;

        Cursor cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(2, cursor.getCount());
        cursor.moveToLast();

        long id = cursor.getLong(cursor.getColumnIndex(ExampleEntry._ID));
        int friends = cursor.getInt(cursor.getColumnIndex(ExampleEntry.NUMBER_OF_FRIENDS));

        ContentValues value = new ContentValues();
        value.put(ExampleEntry.NUMBER_OF_FRIENDS, friends + friendsToAdd);

        int rows = mContext.getContentResolver().update(
                ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                value,
                null,
                null
        );
        assertEquals(rows, 1);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        assertEquals(cursor.getCount(), 1);

        int newFriends = cursor.getInt(cursor.getColumnIndex(ExampleEntry.NUMBER_OF_FRIENDS));
        assertEquals(newFriends, friends + friendsToAdd);

        cursor.close();
    }

    /**
     * Tests {@link android.example.com.exampleprovider.data.ExampleProvider}'s delete method by
     * deleting the last entry in the table.
     */
    public void testDeleteLastEntry() throws Throwable {
        ContentValues[] values = createDummyData();

        ContentResolver resolver =  mContext.getContentResolver();
        for(int i = 0; i < values.length; i++) {
            resolver.insert(
                    ExampleContract.ExampleEntry.CONTENT_URI, values[i]
            );
        }

        Cursor cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(2, cursor.getCount());
        cursor.moveToLast();

        long id = cursor.getLong(cursor.getColumnIndex(ExampleContract.ExampleEntry._ID));

        int rows = mContext.getContentResolver().delete(
                ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                null,
                null
        );

        assertEquals(rows, 1);

        cursor.close();

        cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(1, cursor.getCount());
        cursor.close();
    }

    /**
     * Tests {@link android.example.com.exampleprovider.data.ExampleProvider}'s getType method with
     * both datatypes.
     */
    public void testGetType(){
        assertEquals(mContext.getContentResolver().getType(ExampleEntry.CONTENT_URI),
                ExampleEntry.CONTENT_DIR_TYPE);
        assertEquals(mContext.getContentResolver().getType(
                        ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, 1
                        )),
                ExampleEntry.CONTENT_ITEM_TYPE);
    }
}