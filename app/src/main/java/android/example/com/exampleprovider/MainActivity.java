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

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;

/**
 * This is the main activity for the ExampleProvider App. It contains a {@link ListView}
 * which displays the contents of the database accessed through the
 * {@link android.example.com.exampleprovider.data.ExampleProvider} class.
 */
public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView mListView;
    private SimpleCursorAdapter mAdapter;

    //For the SimpleCursorAdapter to match the in the friends database columns to layout items
    private static final String[] COLUMNS_TO_BE_BOUND = new String[] {
            ExampleEntry.NAME,
            ExampleEntry.NUMBER_OF_FRIENDS
    };

    private static final int[] LAYOUT_ITEMS_TO_FILL = new int[] {
            android.R.id.text1,
            android.R.id.text2
    };

    // Identifies a particular Loader being used in this component.
    private static final int CURSOR_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertData();

        mListView = (ListView) findViewById(R.id.main_list_view);

        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                null,
                COLUMNS_TO_BE_BOUND,
                LAYOUT_ITEMS_TO_FILL,
                0);

        mListView.setAdapter(mAdapter);

        //Initializes the loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    /**
     * Inserts dummy data into the friends database via
     * {@link android.example.com.exampleprovider.data.ExampleProvider#bulkInsert(android.net.Uri, android.content.ContentValues[])}
     */
    private void insertData() {
        ContentResolver resolver =  this.getContentResolver();

        Cursor cursor = resolver.query(ExampleEntry.CONTENT_URI, null,null,null,null);
        if (cursor.getCount() == 0) {

            String nameOne = "Dan";
            String nameTwo = "Katherine";
            int friendsOne = 500;
            int friendsTwo = 500;

            ContentValues[] values = new ContentValues[2];
            values[0] = new ContentValues();
            values[0].put(ExampleEntry.NAME, nameOne);
            values[0].put(ExampleEntry.NUMBER_OF_FRIENDS, friendsOne);


            values[1] = new ContentValues();
            values[1].put(ExampleEntry.NAME, nameTwo);
            values[1].put(ExampleEntry.NUMBER_OF_FRIENDS, friendsTwo);

            resolver.bulkInsert(ExampleEntry.CONTENT_URI,values);
        }
        cursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                this,
                ExampleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    /*
     * Moves the query results into the adapter, causing the
     * ListView fronting this adapter to re-display
     */
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    /*
     * Clears out the adapter's reference to the Cursor.
     * This prevents memory leaks.
     */
        mAdapter.changeCursor(null);
    }
}
