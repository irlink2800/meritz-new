package com.example;

import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.*;

public class SQLDemoActivity extends Activity
{
    private static final String DB_PASSWORD = "foo123";

    private final List<String>   eventList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<String>(this,
                                           R.layout.activity_listview,
                                           eventList);

        ListView listView = (ListView) findViewById(R.id.event_list);
        listView.setAdapter(adapter);

        // You must set Context on SQLiteDatabase first.
        SQLiteDatabase.loadLibs(this);

        new DatabaseTask(this).execute();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private class DatabaseTask extends AsyncTask<Void, String, Void> {
        private Context context;

        DatabaseTask(Context context) {
            this.context = context;
        }

        protected Void doInBackground(Void... params) {
            // Then you can open the writeable database using a password.
            EventDataSQLHelper eventsData = new EventDataSQLHelper(context);
            SQLiteDatabase db = eventsData.getWritableDatabase(DB_PASSWORD);
            resetDatabase(db);

            for (int i = 1; i <= 100; i++)
            {
                addEvent("Event: " + i, db);
            }

            db.close();

            // Read from the database.
            db = eventsData.getReadableDatabase(DB_PASSWORD);
            Cursor cursor = getEvents(db);

            while (cursor.moveToNext())
            {
                long id = cursor.getLong(0);
                long time = cursor.getLong(1);
                String title = cursor.getString(2);
                publishProgress(id + ": " + time + ": " + title);
            }

            db.close();
            eventsData.close();
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            adapter.addAll(Arrays.asList(progress));
        }

        protected void onPostExecute(Void result) {
        }
    }

    private void addEvent(String title, SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put(EventDataSQLHelper.TIME, System.currentTimeMillis());
        values.put(EventDataSQLHelper.TITLE, title);
        db.insert(EventDataSQLHelper.TABLE, null, values);
    }

    private Cursor getEvents(SQLiteDatabase db)
    {
        Cursor cursor = db.query(EventDataSQLHelper.TABLE,
                                 null, null, null, null, null, null);

        startManagingCursor(cursor);
        return cursor;
    }

    private void resetDatabase(SQLiteDatabase db)
    {
        db.delete(EventDataSQLHelper.TABLE, null, null);
    }
}
