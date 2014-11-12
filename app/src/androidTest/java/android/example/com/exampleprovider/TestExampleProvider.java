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
     * Tests {@link ExampleProvider}'s query method with an empty table.
     */
    public void testQueryEmptyTable(){
        // Table queried should be empty since setup was called and everything was destroyed in
        // the table.
        assertResultCount(ExampleEntry.CONTENT_URI, 0);
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
                    null, null, null, null);
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
        ContentValues values = createDummyDataOnePerson("Dan", 555);

        // Insert the values.
        Uri uri = mContext.getContentResolver().insert(
                ExampleEntry.CONTENT_URI, values);

        // Checks that there is one value in the database
        assertResultCount(ExampleEntry.CONTENT_URI, 1);

        // Get the value in the database.
        assertCorrectStoredValues(uri, values);
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with a null entry.
     */
    public void testInsertNull() {
        // Insert the values.
        try {
            mContext.getContentResolver().insert(
                    ExampleEntry.CONTENT_URI, null);
            fail("Insert with null should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // The expected case.
        }
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with a negative number of friends.
     */
    public void testInsertBadNumberFriends() {
        ContentValues values = new ContentValues();
        values.put(ExampleEntry.NAME, "Sarah");
        values.put(ExampleEntry.NUMBER_OF_FRIENDS, -42);

        // Insert the values.
        try {
            mContext.getContentResolver().insert(
                    ExampleEntry.CONTENT_URI, values);
            fail("Insert with a negative number should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // The expected case.
        }
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with a missing number of friends.
     */
    public void testInsertNullNumberFriends() {
        ContentValues values = new ContentValues();
        values.put(ExampleEntry.NAME, "Sarah");

        // Note how we do not store a number of friends in values.
        Uri uri = mContext.getContentResolver().insert(
                ExampleEntry.CONTENT_URI, values);

        // A failed insert will return null.
        assertNull("URI is not null even though no friends given", uri);
    }

    /**
     * Tests {@link ExampleProvider}'s insert method with a missing name entry.
     */
    public void testInsertNameNull() {
        ContentValues values = new ContentValues();
        values.put(ExampleEntry.NUMBER_OF_FRIENDS, 42);

        // Note how we do not store a name in values.
        Uri uri = mContext.getContentResolver().insert(
                ExampleEntry.CONTENT_URI, values);

        // A failed insert will return null.
        assertNull("URI is not null even though no name given", uri);
    }

    /**
     * Tests {@link ExampleProvider}'s bulk insert method.
     */
    public void testBulkInsert() {
        ContentValues[] values = createDummyDataArray();

        mContext.getContentResolver().bulkInsert(ExampleEntry.CONTENT_URI, values);

        // Create a cursor containing only the ids
        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI, new String[] {ExampleEntry._ID}, null, null, null);

        try {
            assertEquals(cursor.getCount(), 2);
            cursor.moveToFirst();
            int i = 1;
            while (cursor.moveToNext()) {
                assertCorrectStoredValues(
                        ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, cursor.getLong(0)),
                        values[i]
                );
                i--;
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Tests {@link ExampleProvider}'s update by changing one values in one row.
     */
    public void testUpdateOneEntry() {
        ContentValues[] values = createDummyDataArray();
        insertDummyData(values);

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI,
                new String[] { ExampleEntry._ID, ExampleEntry.NUMBER_OF_FRIENDS },
                null, null, null);
        long id = -1;
        int friends = -1;
        try {
            assertEquals(cursor.getCount(), 2);
            if (cursor.moveToFirst()) {
                // Based off of the index of the projection, _ID is 0
                id = cursor.getLong(0);
                // Based off of the index of the projection, NUMBER_OF_FRIENDS is 1
                friends = cursor.getInt(1);
            }
        } finally {
            cursor.close();
        }

        ContentValues value = new ContentValues();
        int newFriends = 100;
        // Add 100 new friends
        value.put(ExampleEntry.NUMBER_OF_FRIENDS, friends + newFriends);

        int rows = mContext.getContentResolver().update(
                ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                value, null, null);
        assertEquals(rows, 1);
        assertCorrectStoredValues(ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id), value);
    }

    /**
     * Tests {@link ExampleProvider}'s update by changing multiple entries.
     */
    public void testUpdateMultipleEntries() {
        ContentValues[] values = createDummyDataArray();
        insertDummyData(values);
        ContentValues value = new ContentValues();
        int updatedFriends = 42;
        value.put(ExampleEntry.NUMBER_OF_FRIENDS, updatedFriends);

        // Put the values in updatedFriends into every number of friends field.
        int rows = mContext.getContentResolver().update(
                ExampleEntry.CONTENT_URI, value, null, null);
        // Make sure the assert worked.
        assertEquals(rows, values.length);

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI,
                new String[] { ExampleEntry._ID, ExampleEntry.NUMBER_OF_FRIENDS },
                null, null, null);

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
        ContentValues[] values = createDummyDataArray();
        insertDummyData(values);

        ContentValues value = new ContentValues();
        value.put(ExampleEntry.NUMBER_OF_FRIENDS, -42);

        // Put the invalid updated friends value
        try {
            mContext.getContentResolver().update(
                    ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, 0),
                    value, null, null);
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
        ContentValues[] values = createDummyDataArray();
        insertDummyData(values);

        Cursor cursor1 = mContext.getContentResolver().query(
                ExampleEntry.CONTENT_URI,
                new String[] { ExampleEntry._ID, ExampleEntry.NUMBER_OF_FRIENDS },
                null, null, null);

        long id = -1;
        try {
            assertEquals(cursor1.getCount(), 2);
            cursor1.moveToLast();
            id = cursor1.getLong(0);
        } finally {
            cursor1.close();
        }

        int rows = mContext.getContentResolver().delete(
                ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                null, null);
        assertEquals(rows, 1);

        //Make sure that the id was actually set
        assertNotSame(id, -1);
        assertResultCount(ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, id),
                0);
    }

    /**
     * Tests {@link ExampleProvider}'s delete method by deleting all records and the testing that
     * there are no records.
     */
    public void testDeleteAllRecords() {
        deleteAllRecords();
        assertResultCount(ExampleEntry.CONTENT_URI, 0);
    }

    /**
     * Tests {@link ExampleProvider}'s getType method with
     * both datatypes.
     */
    public void testGetType() {
        ContentResolver r = getContext().getContentResolver();
        assertEquals(
                r.getType(ExampleEntry.CONTENT_URI),
                ExampleEntry.CONTENT_DIR_TYPE);
        assertEquals(
                r.getType(ContentUris.withAppendedId(ExampleEntry.CONTENT_URI, 1)),
                ExampleEntry.CONTENT_ITEM_TYPE);
    }

    /**
     * Helper Methods are below
     */

    /**
     * Helper method to delete all of the record in the database.
     */
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                ExampleEntry.CONTENT_URI,
                null,
                null
        );
    }

    /**
     * Helper method to create one row of data in the database to help perform further tests.
     */
    public ContentValues createDummyDataOnePerson(String name, int friendsNumber) {
        ContentValues values = new ContentValues();
        values.put(ExampleEntry.NAME, name);
        values.put(ExampleEntry.NUMBER_OF_FRIENDS, friendsNumber);
        return values;
    }

    /**
     * Helper method to create data for rows in the database to help perform further tests.
     */
    public ContentValues[] createDummyDataArray() {
        ContentValues[] valuesArr = new ContentValues[2];
        valuesArr[0] = createDummyDataOnePerson("Katherine", 554);
        valuesArr[1] = createDummyDataOnePerson("Dan", 523);
        return valuesArr;
    }

    /**
     * Helper method to create data for rows in the database and insert to help perform
     * further tests.
     */
    public Uri[] insertDummyData(ContentValues[] values) {
        Uri[] uris = new Uri[values.length];
        for(int i = 0; i < values.length; i++) {
            uris[i] = mContext.getContentResolver().insert(
                    ExampleEntry.CONTENT_URI, values[i]
            );
        }
        return uris;
    }

    /**
     * Helper method to test whether the number of objects returned in a query matches the
     * expected amount in the table.
     */
    public void assertResultCount(Uri uri, String[] projection, String selection,
                                  String[] selectionArgs, int expectedCount) {
        Cursor cursor = mContext.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
        try {
            assertEquals("Row count " + cursor.getCount(), expectedCount, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    /**
     * Shortened form of the method assertResultCount if no additional where clause or projection
     * is needed.
     */
    private void assertResultCount(Uri uri, int expectedCount) {
        assertResultCount(uri, null, null, null, expectedCount);
    }

    /**
     * Helper method to test whether the object stored at the URI has the same values as the
     * ContentValues passed as a parameter.
     */
    private void assertCorrectStoredValues(Uri uri, ContentValues values) {
        Cursor cursor = mContext.getContentResolver().query(uri,
                new String[] {
                        ExampleEntry._ID,
                        ExampleEntry.NAME,
                        ExampleEntry.NUMBER_OF_FRIENDS },
                null, null, null);
        try {
            // Assert there is only one entry.
            assertEquals("Row count " + cursor.getCount(), 1, cursor.getCount());

            //Move to the first and only row.
            cursor.moveToFirst();
            //If it contains the key, assert the value is the same.
            if (values.containsKey(ExampleEntry._ID)) {
                assertEquals(
                        new Long(cursor.getLong(0)),
                        values.getAsLong(ExampleEntry._ID)
                );
            }
            if (values.containsKey(ExampleEntry.NAME)) {
                assertEquals(
                        cursor.getString(1),
                        values.getAsString(ExampleEntry.NAME)
                );
            }
            if (values.containsKey(ExampleEntry.NUMBER_OF_FRIENDS)) {
                assertEquals(
                        new Integer(cursor.getInt(2)),
                        values.getAsInteger(ExampleEntry.NUMBER_OF_FRIENDS)
                );
            }
        } finally {
            cursor.close();
        }
    }
}