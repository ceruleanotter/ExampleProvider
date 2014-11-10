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

import android.content.ContentValues;
import android.database.Cursor;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.example.com.exampleprovider.data.ExampleProvider;

/**
 * This is the main activity for the ExampleProvider App. It contains a {@link ListView}
 * which displays the contents of the database accessed through the
 * {@link ExampleProvider} class.
 */
public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
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

        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                null,
                COLUMNS_TO_BE_BOUND,
                LAYOUT_ITEMS_TO_FILL,
                0);
        mListView = (ListView) findViewById(R.id.main_list_view);
        mListView.setAdapter(mAdapter);

        // Initializes the loader.
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    /**
     * Inserts dummy data into the friends database via
     * {@link ExampleProvider#bulkInsert(android.net.Uri, android.content.ContentValues[])}
     * To keep the code simple for this toy app we are inserting the data here.
     * Normally this should be done on a separate thread, as we do in Sunshine with AsyncTask.
     */
    private void insertData() {
        Cursor cursor = getContentResolver().query(ExampleEntry.CONTENT_URI,
                null, null, null, null);
        try {
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

                getContentResolver().bulkInsert(ExampleEntry.CONTENT_URI,values);
            }
        } finally {
            // If there is a problem reading from the cursor, we still try to close it to avoid
            // memory leaks from a lingering open cursor.
            cursor.close();
        }
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Moves the query results into the adapter, causing the
        // ListView fronting this adapter to re-display
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clears out the adapter's reference to the Cursor.
        // This prevents memory leaks.
        mAdapter.changeCursor(null);
    }
}