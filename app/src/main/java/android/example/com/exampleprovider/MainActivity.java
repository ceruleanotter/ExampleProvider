package android.example.com.exampleprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.example.com.exampleprovider.data.ExampleContract;
import android.example.com.exampleprovider.data.ExampleContract.ExampleEntry;
import android.example.com.exampleprovider.data.ExampleDbHelper;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    ExampleDbHelper mDatabaseHelper;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertData();

        Cursor cursor = this.getContentResolver().query(ExampleEntry.TABLE_URI,null, null,null,null);

        TextView textView = (TextView)findViewById(R.id.main_text_view);
        textView.setText("\n");
        while(cursor.moveToNext()) {

            textView.append(Integer.toString(cursor.getInt(cursor.getColumnIndex(ExampleEntry._ID))));
            textView.append("\t|\t");
            textView.append(cursor.getString(cursor.getColumnIndex(ExampleEntry.NAME)));
            textView.append("\t|\t");
            textView.append(Integer.toString(cursor.getInt(cursor.getColumnIndex(ExampleEntry.NUMBER_OF_FRIENDS))));
            textView.append("\n");

        }
        cursor.close();
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

    private void insertData(){
        ContentResolver resolver =  this.getContentResolver();

        String nameOne = "Dan";
        String nameTwo = "Katherine";
        int friendsOne = 500;
        int friendsTwo = 500;

        ContentValues values = new ContentValues();
        values.put(ExampleContract.ExampleEntry.NAME, nameOne);
        values.put(ExampleContract.ExampleEntry.NUMBER_OF_FRIENDS, friendsOne);


        resolver.insert(
                ExampleContract.ExampleEntry.TABLE_URI, values
        );

        values.clear();
        values.put(ExampleContract.ExampleEntry.NAME, nameTwo);
        values.put(ExampleContract.ExampleEntry.NUMBER_OF_FRIENDS, friendsTwo);


        resolver.insert(
                ExampleContract.ExampleEntry.TABLE_URI, values
        );
    }
}
