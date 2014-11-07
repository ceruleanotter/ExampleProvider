package android.example.com.exampleprovider.data;

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
    public static final String CONTENT_AUTHORITY = "com.example.android.exampleprovider";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Columns and Uris for the friends table. Each row represents a person and the number of
     * friends they have.
     */
    public static final class ExampleEntry implements BaseColumns {

        public static final String PATH_FRIENDS = "friends";

        /**
         * The MIME type for a list of people.
         */
        public static final String CONTENT_DIR_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_FRIENDS;
        /**
         * The MIME type for a single person.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_FRIENDS;

        /**
         * Base Uri for the Friends table.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIENDS).build();

        /**
         * Name of the person.
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";

        /**
         * Number of friends the person has
         * <P>Type: INTEGER</P>
         */
        public static final String NUMBER_OF_FRIENDS = "num_friends";

    }
}
