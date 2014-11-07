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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;

/**
 * {@link ExampleDbHelper} helps organize database versioning and gives easy access to a
 * SQLiteDatabase object.
 */
public class ExampleDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "example_database.db";

    public ExampleDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude

        //TODO LOG TO OUTPUT DATABASE VERSION

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + ExampleEntry.PATH_FRIENDS + " (" +
                ExampleEntry._ID + " INTEGER PRIMARY KEY," +
                ExampleEntry.NAME + " TEXT UNIQUE NOT NULL, " +
                ExampleEntry.NUMBER_OF_FRIENDS + " INTEGER NOT NULL " +
                " );"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ExampleEntry.PATH_FRIENDS);
        onCreate(sqLiteDatabase);

    }
}