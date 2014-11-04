package android.example.com.exampleprovider.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lyla on 11/3/14.
 */
public class ExampleContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.exampleprovider.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class ExampleEntry implements BaseColumns {
        public static final String TABLE_NAME = "friends";


        public static final String CONTENT_DIR_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;


        public static final Uri TABLE_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String NAME = "name";
        public static final String NUMBER_OF_FRIENDS = "num_friends";


        public static Uri buildExampleUriWithID(long id) {
            return ContentUris.withAppendedId(TABLE_URI, id);
        }
    }

}
