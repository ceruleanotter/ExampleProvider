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
import android.content.ContentValues;
import android.database.Cursor;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * This is the {@link MainActivity} for the ExampleProvider App. It contains a {@link ListView}
 * which displays the contents of the database accessed through the
 * {@link android.example.com.exampleprovider.data.ExampleProvider} class.
 */
public class MainActivity extends ActionBarActivity {
    private ListView mListView;

    //For the SimpleCursorAdapter to match the in the friends database columns to layout items
    private static final String[] COLUMNS_TO_BE_BOUND = new String[] {
            ExampleEntry.NAME,
            ExampleEntry.NUMBER_OF_FRIENDS
    };

    private static final int[] LAYOUT_ITEMS_TO_FILL = new int[] {
            android.R.id.text1,
            android.R.id.text2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertData();

        Cursor cursor = this.getContentResolver().query(ExampleEntry.CONTENT_URI,null, null,null,null);
        mListView = (ListView) findViewById(R.id.main_list_view);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursor,
                COLUMNS_TO_BE_BOUND,
                LAYOUT_ITEMS_TO_FILL,
                0);

        mListView.setAdapter(adapter);
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
}
