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


public class MainActivity extends ActionBarActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertData();

        Cursor cursor = this.getContentResolver().query(ExampleEntry.CONTENT_URI,null, null,null,null);
        mListView = (ListView) findViewById(R.id.main_list_view);
        ContentResolver resolver = getContentResolver();
        String[] columnsToBeBound = new String[] {
                ExampleEntry.NAME,
                ExampleEntry.NUMBER_OF_FRIENDS
        };

        int[] layoutItemsToFill = new int[] {
                android.R.id.text1,
                android.R.id.text2
        };


        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursor,
                columnsToBeBound,
                layoutItemsToFill,
                0);


        mListView.setAdapter(adapter);

    }

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
