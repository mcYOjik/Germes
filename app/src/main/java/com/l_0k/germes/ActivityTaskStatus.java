package com.l_0k.germes;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.google.android.gms.location.LocationServices;

import java.util.Calendar;


public class ActivityTaskStatus extends Activity {

    private SQLiteDatabase sqLiteDatabase;
    String _id;
    int status;
    String identifier;
    String createDate;

    private RadioButton radioButtonDelivering;
    private RadioButton radioButtonDriverRefused;
    private RadioButton radioButtonDelivered;
    private RadioButton radioButtonCustomerRefused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_status);

        radioButtonDelivering = (RadioButton)findViewById(R.id.radioButtonDelivering);
        radioButtonDriverRefused = (RadioButton)findViewById(R.id.radioButtonDriverRefused);
        radioButtonDelivered = (RadioButton)findViewById(R.id.radioButtonDelivered);
        radioButtonCustomerRefused = (RadioButton)findViewById(R.id.radioButtonCustomerRefused);

        Bundle extras = getIntent().getExtras();
        _id = extras.getString("_id"); //Task DB _id
        setStatus();

        //update status
        View.OnClickListener radioClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newStatus = Task.STATUS_DELIVERING;
                if (view.getId() == radioButtonDelivering.getId()) {
                    newStatus = Task.STATUS_DELIVERING;
                }
                if (view.getId() == radioButtonDriverRefused.getId()) {
                    newStatus = Task.STATUS_DRIVER_REFUSED;
                }
                if (view.getId() == radioButtonDelivered.getId()) {
                    newStatus = Task.STATUS_DELIVERED;
                }
                if (view.getId() == radioButtonCustomerRefused.getId()) {
                    newStatus = Task.STATUS_CUSTOMER_REFUSED;
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put(GermesDBOpenHelper.TABLE_TASKS_COLUMN_STATUS, newStatus);
                sqLiteDatabase.update(GermesDBOpenHelper.TABLE_TASKS, contentValues, "_id = ?", new String[] { _id });

                //send status to 1c - insert to DB status record
                contentValues.clear();
                contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_TASK_ID, _id);
                Calendar calendar = Calendar.getInstance();
                contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_STATUS_TIMES_TAMP
                        , String.valueOf(calendar.get(Calendar.YEAR))
                        + "-" + String.valueOf(calendar.get(Calendar.MONTH))
                        + "-" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
                        + "T" + String.valueOf(calendar.get(Calendar.HOUR))
                        + ":" + String.valueOf(calendar.get(Calendar.MINUTE))
                        + ":" + String.valueOf(calendar.get(Calendar.SECOND)));
                contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_STATUS, newStatus);

                //Берём последнюю известную локацию
                if (ActivityTasks.mGoogleApiClient.isConnected()) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(ActivityTasks.mGoogleApiClient);
                    if (location != null) {
                        contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_LATITUDE, String.valueOf(location.getLatitude()));
                        contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_LONGITUDE, String.valueOf(location.getLongitude()));

                        //пробуем найти и добавить адрес
                        String address = UtilHelper.getAddress(ActivityTaskStatus.this, location, 5);
                        if (address != "") {
                            contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_ADDRESS, address);
                        }
                    }
                };

                sqLiteDatabase.insert(GermesDBOpenHelper.TABLE_STATUSES_HISTORY, null, contentValues);

                //вызываем отправку изменений статуса в 1с
                UtilHelper sendStatusTo1c = new UtilHelper();
                sendStatusTo1c.sendTasksStatusesTo1c(getApplicationContext());

                setResult(RESULT_OK);
                finish();
            }
        };

        radioButtonDelivering.setOnClickListener(radioClickListener);
        radioButtonDriverRefused.setOnClickListener(radioClickListener);
        radioButtonDelivered.setOnClickListener(radioClickListener);
        radioButtonCustomerRefused.setOnClickListener(radioClickListener);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_choose_status, menu);
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

    private void setStatus(){
        sqLiteDatabase = ActivityTasks.germesDBOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + ActivityTasks.germesDBOpenHelper.TABLE_TASKS + " WHERE _id = " + _id, null);
        if (cursor.moveToFirst()) {
            identifier = cursor.getString(cursor.getColumnIndex(ActivityTasks.germesDBOpenHelper.TABLE_TASKS_COLUMN_TASK_1C_ID));
            createDate = cursor.getString(cursor.getColumnIndex(ActivityTasks.germesDBOpenHelper.TABLE_TASKS_COLUMN_CREATE_DATE));

            status = cursor.getInt(cursor.getColumnIndex(ActivityTasks.germesDBOpenHelper.TABLE_TASKS_COLUMN_STATUS));
            switch (status) {
                case Task.STATUS_DELIVERING:
                    radioButtonDelivering.setChecked(true);
                    break;
                case Task.STATUS_DRIVER_REFUSED:
                    radioButtonDriverRefused.setChecked(true);
                    break;
                case Task.STATUS_DELIVERED:
                    radioButtonDelivered.setChecked(true);
                    break;
                case Task.STATUS_CUSTOMER_REFUSED:
                    radioButtonCustomerRefused.setChecked(true);
                    break;
                default:
                    radioButtonDelivering.setChecked(true);
            }
        }
        cursor.close();

        radioButtonDelivering.setEnabled(status == Task.STATUS_DELIVERING);
        radioButtonDriverRefused.setEnabled(status == Task.STATUS_DELIVERING);
        radioButtonDelivered.setEnabled(status == Task.STATUS_DELIVERING);
        radioButtonCustomerRefused.setEnabled(status == Task.STATUS_DELIVERING);
    }
}
