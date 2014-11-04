package android.example.com.exampleprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        mDatabaseHelper = new ExampleDbHelper(this);
        ContentValues values = new ContentValues();
        values.put(ExampleEntry.NAME, "Dan");
        values.put(ExampleEntry.NUMBER_OF_FRIENDS, 552);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.delete(ExampleEntry.TABLE_NAME,null,null);

        mDatabaseHelper.getWritableDatabase().insert(
                ExampleContract.ExampleEntry.TABLE_NAME, null, values
        );

        values.clear();
        values.put(ExampleEntry.NAME, "Katherine");
        values.put(ExampleEntry.NUMBER_OF_FRIENDS, 599);


        long id = mDatabaseHelper.getWritableDatabase().insert(
                ExampleContract.ExampleEntry.TABLE_NAME, null, values
        );

        Cursor cursor = this.getContentResolver().query(ExampleEntry.buildExampleUriWithID(id),null, null,null,null);


        TextView textView = (TextView)findViewById(R.id.main_text_view);
        textView.setText("");
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
}
