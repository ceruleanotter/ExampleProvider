package android.example.com.exampleprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.example.com.exampleprovider.data.ExampleContract;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class TestExampleProvider extends AndroidTestCase {

    @Override
    //Setup is called before each test
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteAllRecords();

    }

    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                ExampleEntry.TABLE_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.TABLE_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

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


    public void testInsert() throws Throwable {
        ContentResolver resolver =  mContext.getContentResolver();

        ContentValues[] values = createDummyData();
        Uri[] uris = new Uri[values.length];

        for(int i = 0; i < values.length; i++) {
            uris[i] = resolver.insert(
                    ExampleContract.ExampleEntry.TABLE_URI, values[i]
            );
        }

        Cursor cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.TABLE_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(values.length, cursor.getCount());
        cursor.close(); //ALWAYS CLOSE YOUR CURSOR

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


    public void testDeleteLastEntry() throws Throwable {
        ContentValues[] values = createDummyData();

        ContentResolver resolver =  mContext.getContentResolver();
        for(int i = 0; i < values.length; i++) {
            resolver.insert(
                    ExampleContract.ExampleEntry.TABLE_URI, values[i]
            );
        }

        Cursor cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.TABLE_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(2, cursor.getCount());
        cursor.moveToLast();

        long id = cursor.getLong(cursor.getColumnIndex(ExampleContract.ExampleEntry._ID));

         int rows = mContext.getContentResolver().delete(
                ExampleContract.ExampleEntry.buildExampleUriWithID(id),
                null,
                null
        );

        assertEquals(rows, 1);

        cursor.close();

        cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.TABLE_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(1, cursor.getCount());

        cursor.close();

    }

    public void testUpdateEntry() throws Throwable {
        ContentResolver resolver =  mContext.getContentResolver();

        ContentValues[] values = createDummyData();
        Uri[] uris = new Uri[values.length];

        for(int i = 0; i < values.length; i++) {
            uris[i] = resolver.insert(
                    ExampleContract.ExampleEntry.TABLE_URI, values[i]
            );
        }

        int friendsToAdd = 100;

        Cursor cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.TABLE_URI,
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
                ExampleContract.ExampleEntry.buildExampleUriWithID(id),
                value,
                null,
                null
        );

        assertEquals(rows, 1);

        cursor.close();

        cursor = mContext.getContentResolver().query(
                ExampleContract.ExampleEntry.buildExampleUriWithID(id),
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

    public void testGetType(){
        assertEquals(mContext.getContentResolver().getType(ExampleEntry.TABLE_URI),
                ExampleEntry.CONTENT_DIR_TYPE);
        assertEquals(mContext.getContentResolver().getType(ExampleEntry.buildExampleUriWithID(1)),
                ExampleEntry.CONTENT_ITEM_TYPE);
    }


    public void testBulkInsert(){
        ContentResolver resolver =  mContext.getContentResolver();
        ContentValues[] values = createDummyData();

        resolver.bulkInsert(ExampleEntry.TABLE_URI, values);

        Cursor cursor = mContext.getContentResolver().query(
                ExampleEntry.TABLE_URI,
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

}