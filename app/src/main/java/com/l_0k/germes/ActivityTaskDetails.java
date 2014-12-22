package com.l_0k.germes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ActivityTaskDetails extends Activity {
    private ListView listViewStatusesHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Bundle extras = getIntent().getExtras();
        ((TextView)findViewById(R.id.TaskID1c)).setText(getString(R.string.TaskID1c) + ": " + extras.getString("TaskID1c"));
        ((TextView)findViewById(R.id.CreateDate)).setText(getString(R.string.CreateDate) + ": " + extras.getString("CreateDate"));
        ((TextView)findViewById(R.id.UpToDate)).setText(getString(R.string.UpToDate) + ": " + extras.getString("UpToDate"));
        ((TextView)findViewById(R.id.UpToTime)).setText(getString(R.string.UpToTime) + ": " + extras.getString("UpToTime"));
        ((TextView)findViewById(R.id.CustomerName)).setText(getString(R.string.CustomerName) + ": " + extras.getString("CustomerName"));
        ((TextView)findViewById(R.id.CustomerPhone)).setText(getString(R.string.CustomerPhone) + ": " + extras.getString("CustomerPhone"));
        ((TextView)findViewById(R.id.CustomerAddress)).setText(getString(R.string.CustomerAddress) + ": " + extras.getString("CustomerAddress"));
        ((TextView)findViewById(R.id.ShippingWarehouse)).setText(getString(R.string.ShippingWarehouse) + ": " + extras.getString("ShippingWarehouse"));
        ((TextView)findViewById(R.id.Status)).setText(getString(R.string.StatusTaskDetail) + ": " + extras.getString("Status"));
        ((TextView)findViewById(R.id.Goods)).setText(getString(R.string.Goods) + ":\n" + extras.getString("Goods"));
        //((TextView)findViewById(R.id.StatusHistory)).setText(getString(R.string.StatusHistory) + ":\n" + extras.getString("StatusHistory"));

        listViewStatusesHistory = (ListView)findViewById(R.id.listViewStatusesHistory);
        fillData(extras.getInt(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_TASK_ID));
        listViewStatusesHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StatusHistory statusHistory = (StatusHistory) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:"
                        + statusHistory.getLatitude() + ","
                        + statusHistory.getLongitude() + "?z=17"));
                /*intent.setData(Uri.parse("https://www.google.ru/maps/place//@"
                        + statusHistory.getLatitude() + ","
                        + statusHistory.getLongitude() + ",17z/data=!4m2!3m1!1s0x0:0x0"));*/
                startActivity(intent);
            }
        });
    }

    private void fillData(int _id) {
        GermesDBOpenHelper germesDBOpenHelper = new GermesDBOpenHelper(this);
        SQLiteDatabase sqLiteDatabase = germesDBOpenHelper.getWritableDatabase();

        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + GermesDBOpenHelper.TABLE_STATUSES_HISTORY
                + " WHERE " + GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_TASK_ID
                + " = " + Integer.toString(_id), null);

        ArrayList<StatusHistory> statusHistories = new ArrayList<StatusHistory>();
        if (cursor.moveToFirst()) {
            do {
                StatusHistory statusHistory = new StatusHistory(cursor.getString(cursor.getColumnIndex(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_STATUS_TIMES_TAMP)),
                        cursor.getString(cursor.getColumnIndex(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_STATUS)),
                        cursor.getString(cursor.getColumnIndex(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndex(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_ADDRESS)));

                statusHistories.add(statusHistory);
            } while (cursor.moveToNext());
        }

        StatusesHistoryAdapter statusesHistoryAdapter = new StatusesHistoryAdapter(this, statusHistories);
        listViewStatusesHistory.setAdapter(statusesHistoryAdapter);

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_task_details, menu);
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
