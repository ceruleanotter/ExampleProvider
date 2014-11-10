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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.example.com.exampleprovider.data.ExampleContract;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;
import android.example.com.exampleprovider.data.ExampleProvider;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * This is a collection of tests for the associated Content Provider. See
 * {@link ExampleProvider}
 */
public class TestExampleProvider extends AndroidTestCase {
    /**
     *     Setup is called before each test
     */
    @Override
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
                null, null, null, null
        );
        try {
            assertEquals(0, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    /**
     * Helper method to create data for rows in the database to help perform further tests.
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

    public ContentValues createOneRowDummyData() {
        return createDummyData()[0];
    }

    /**
     * Helper method to create data for rows in the database and insert to help perform
     * further tests.
     */
    public Uri[] insertDummyData(ContentValues[] values) {
        Uri[] uris = new Uri[values.length];
        for(int i = 0; i < values.length; i++) {
            uris[i] = mContext.getContentResolver().insert(
                    ExampleContract.ExampleEntry.CONTENT_URI, values[i]
            );
        }
        return uris;
    }

    /**
     * Tests {@link ExampleProvider}'s query method with an empty table.
     */
    public void testQueryEmptyTable(){
        Cursor cursor = mContext.getContentResolver().query(ExampleContract.ExampleEntry.CONTENT_URI,
                null, null, null, null
        );
        // Table queried should be empty
        try {
            assertEquals(cursor.getCount(), 0);
        } finally {
            cursor.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s query method with an invalid ID.
     */
    public void testQueryBadID(){
        Cursor cursor = null;
        try {
            // Appending the bad ID -1
            cursor = mContext.getContentResolver().query(
                    ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, -1),
                    null, null, null, null
            );
            fail("Query given -1, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // This is the expected case.
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with one entry.
     */
    public void testInsertOne() {
        ContentValues values = createOneRowDummyData();

        // Insert the values.
        Uri uri = mContext.getContentResolver().insert(
                ExampleContract.ExampleEntry.CONTENT_URI, values);

        Cursor cursor1 = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.CONTENT_URI,
                null, null, null, null);

        try {
            // Check the insert worked.
            assertEquals(1, cursor1.getCount());
        } finally {
            cursor1.close(); // Always close your cursor!
        }

        // Get the value in the database.
        Cursor cursor2 = mContext.getContentResolver().query(uri, null, null, null, null);
        try {
            cursor2.moveToFirst();
            assertEquals(cursor2.getString(cursor2.getColumnIndex(
                            ExampleContract.ExampleEntry.NAME)),
                    values.getAsString(ExampleEntry.NAME)
            );
            assertEquals(cursor2.getInt(cursor2.getColumnIndex(
                            ExampleContract.ExampleEntry.NUMBER_OF_FRIENDS)),
                    values.getAsInteger(ExampleEntry.NUMBER_OF_FRIENDS).intValue()
            );
        } finally {
            cursor2.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with a null entry.
     */
    public void testInsertNull() {
        ContentValues values = null;

        // Insert the values.
        try {
            mContext.getContentResolver().insert(
                    ExampleContract.ExampleEntry.CONTENT_URI, values);
            fail("Insert with null should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // The expected case.
        }
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with a negative number of friends.
     */
    public void testBadNumberFriends() {
        ContentValues values = new ContentValues();
        values.put(ExampleEntry.NAME, "Sarah");
        values.put(ExampleEntry.NUMBER_OF_FRIENDS, -42);

        // Insert the values.
        try {
            mContext.getContentResolver().insert(
                    ExampleContract.ExampleEntry.CONTENT_URI, values);
            fail("Insert with a negative number should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // The expected case.
        }
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with a missing number of friends.
     */
    public void testNullNumberFriends() {
        ContentValues values = new ContentValues();
        values.put(ExampleEntry.NAME, "Sarah");

        // Note how we do not store a number of friends in values.
        Uri uri = mContext.getContentResolver().insert(
                ExampleContract.ExampleEntry.CONTENT_URI, values);

        // A failed insert will return null.
        assertNull(uri);
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with a missing name entry.
     */
    public void testNameNull() {
        ContentValues values = new ContentValues();
        values.put(ExampleEntry.NUMBER_OF_FRIENDS, 42);

        // Note how we do not store a name in values.
        Uri uri = mContext.getContentResolver().insert(
                ExampleContract.ExampleEntry.CONTENT_URI, values);

        // A failed insert will return null.
        assertNull(uri);
    }

    /**
     * Tests {@link ExampleProvider}'s bulk insert method.
     */
    public void testBulkInsert() {
        ContentValues[] values = createDummyData();

        mContext.getContentResolver().bulkInsert(ExampleEntry.CONTENT_URI, values);

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI, null, null, null, null
        );

        try {
            assertEquals(2, cursor.getCount());
            cursor.moveToFirst();
            int i = 1;
            while (cursor.moveToNext()) {
                assertEquals(cursor.getString(cursor.getColumnIndex(
                                ExampleContract.ExampleEntry.NAME)),
                        values[i].getAsString(ExampleContract.ExampleEntry.NAME)
                );
                assertEquals(cursor.getInt(cursor.getColumnIndex(
                                ExampleContract.ExampleEntry.NUMBER_OF_FRIENDS)),
                        values[i].getAsInteger(ExampleEntry.NUMBER_OF_FRIENDS).intValue()
                );
                i--;
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s bulk insert method with a bad input.
     */
    public void testBadInputBulkInsert() {
        ContentValues[] values = createDummyData();
        values[1] = new ContentValues();

        mContext.getContentResolver().bulkInsert(ExampleEntry.CONTENT_URI, values);

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI, null, null, null, null
        );

        try {
            assertEquals(values.length - 1, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s update by changing one values in one row.
     */
    public void testUpdateOneEntry() {
        ContentValues[] values = createDummyData();
        insertDummyData(values);

        int friendsToAdd = 100;

        Cursor cursor1 = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.CONTENT_URI,
                new String[] { ExampleEntry._ID, ExampleEntry.NUMBER_OF_FRIENDS },
                null, null, null
        );
        long id = -1;
        int friends = -1;
        try {
            assertEquals(2, cursor1.getCount());
            if (cursor1.moveToFirst()) {
                // Based off of the index of the projection, _ID is 0
                id = cursor1.getLong(0);
                // Based off of the index of the projection, NUMBER_OF_FRIENDS is 1
                friends = cursor1.getInt(1);
            }
        } finally {
            cursor1.close();
        }

        ContentValues value = new ContentValues();
        value.put(ExampleEntry.NUMBER_OF_FRIENDS, friends + friendsToAdd);

        int rows = mContext.getContentResolver().update(
                ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                value, null, null
        );
        assertEquals(rows, 1);

        Cursor cursor2 = mContext.getContentResolver().query(
                ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                null, null, null, null
        );
        try {
            cursor2.moveToFirst();
            assertEquals(cursor2.getCount(), 1);

            int newFriends = cursor2.getInt(cursor2.getColumnIndex(ExampleEntry.NUMBER_OF_FRIENDS));
            assertEquals(newFriends, friends + friendsToAdd);
        } finally {
            cursor2.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s update by changing multiple entries.
     */
    public void testUpdateMultipleEntries() {
        ContentValues[] values = createDummyData();
        insertDummyData(values);

        int updatedFriends = 42;

        ContentValues value = new ContentValues();
        value.put(ExampleEntry.NUMBER_OF_FRIENDS, updatedFriends);

        // Put the values in updatedFriends into every number of friends field.
        int rows = mContext.getContentResolver().update(
                ExampleEntry.CONTENT_URI, value, null, null
        );

        // Make sure the assert worked.
        assertEquals(rows, values.length);

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI,
                new String[] { ExampleEntry._ID, ExampleEntry.NUMBER_OF_FRIENDS },
                null, null, null
        );

        try {
            cursor.moveToFirst();
            // Assert that everything was updated to the value of updatedFriends.
            while(cursor.moveToNext()) {
                int newFriends = cursor.getInt(1);
                assertEquals(newFriends, updatedFriends);
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s update by trying to change an entry to an invalid value.
     */
    public void testUpdateInvalid() {
        ContentValues[] values = createDummyData();
        insertDummyData(values);
        int updatedFriends = -42;

        ContentValues value = new ContentValues();
        value.put(ExampleEntry.NUMBER_OF_FRIENDS, updatedFriends);

        // Put the invalid updated friends value
        try {
            int rows = mContext.getContentResolver().update(
                    ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, 0),
                    value, null, null
            );
            fail("Insert with invalid update number should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // The expected case.
        }
    }

    /**
     * Tests {@link ExampleProvider}'s delete method by
     * deleting the last entry in the table.
     */
    public void testDeleteLastEntry() {
        ContentValues[] values = createDummyData();
        insertDummyData(values);

        Cursor cursor1 = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.CONTENT_URI,
                new String[] { ExampleEntry._ID, ExampleEntry.NUMBER_OF_FRIENDS },
                null, null, null
        );

        long id = -1;
        try {
            assertEquals(2, cursor1.getCount());

            cursor1.moveToLast();

            id = cursor1.getLong(0);

            int rows = mContext.getContentResolver().delete(
                    ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                    null, null
            );
            assertEquals(rows, 1);
        } finally {
            cursor1.close();
        }

        //Make sure that the id was actually set
        assertNotSame(id, -1);

        Cursor cursor2 = null;
        try {
            cursor2 = mContext.getContentResolver().query(
                    ContentUris.withAppendedId(ExampleContract.ExampleEntry.CONTENT_URI, id),
                    null, null, null, null
            );
        } finally {
            assertEquals(cursor2.getCount(), 0);
            if (cursor2 != null) cursor2.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s getType method with
     * both datatypes.
     */
    public void testGetType() {
        assertEquals(mContext.getContentResolver().getType(ExampleEntry.CONTENT_URI),
                ExampleEntry.CONTENT_DIR_TYPE);
        assertEquals(mContext.getContentResolver().getType(
                        ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, 1
                        )),
                ExampleEntry.CONTENT_ITEM_TYPE);
    }
}