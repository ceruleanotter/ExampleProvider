package android.example.com.exampleprovider.data;

import android.provider.BaseColumns;

/**
 * Created by lyla on 11/3/14.
 */
public class ExampleContract {


    public static final class ExampleEntry implements BaseColumns {
        public static final String TABLE_NAME = "friends";

        public static final String NAME = "name";
        public static final String NUMBER_OF_FRIENDS = "num_friends";
    }

}
