package android.example.com.exampleprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {


    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertData();

        Cursor cursor = this.getContentResolver().query(ExampleEntry.TABLE_URI,null, null,null,null);
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


        RatingAdapter adapter = new RatingAdapter(this,cursor,0);


        mListView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertData() {
        ContentResolver resolver =  this.getContentResolver();

        Cursor cursor = resolver.query(ExampleEntry.TABLE_URI, null,null,null,null);
        if (cursor.getCount() == 0) {

            String nameOne = "Dan";
            String nameTwo = "Katherine";
            int friendsOne = 400;
            int friendsTwo = 425;

            ContentValues[] values = new ContentValues[2];
            values[0] = new ContentValues();
            values[0].put(ExampleEntry.NAME, nameOne);
            values[0].put(ExampleEntry.NUMBER_OF_FRIENDS, friendsOne);


            values[1] = new ContentValues();
            values[1].put(ExampleEntry.NAME, nameTwo);
            values[1].put(ExampleEntry.NUMBER_OF_FRIENDS, friendsTwo);

            resolver.bulkInsert(ExampleEntry.TABLE_URI,values);
        }
        cursor.close();
    }
}
