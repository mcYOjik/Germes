package com.l_0k.germes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;


public class ActivityTasks extends Activity {

    public static GermesDBOpenHelper germesDBOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    ArrayList<Task> tasks = new ArrayList<Task>();
    TaskAdapter taskAdapter;
    ListView listViewTasks;
    Spinner spinner;

    static final int FILTER_TYPE_NEW = 0;
    static final int FILTER_TYPE_CLOSED = 1;
    static final int FILTER_TYPE_ALL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        //setContentView(R.layout.activity_logon);

        //Task list
        listViewTasks = (ListView) findViewById(R.id.listViewTasks);

        germesDBOpenHelper = new GermesDBOpenHelper(this);
        //Task Status
        spinner = (Spinner) findViewById(R.id.spinnerTaskStatuses);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_statuses_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                fillData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Fill list data from DB
        fillData(FILTER_TYPE_NEW);

        //Run change status activity
        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view,
                                    int position, long id) {
                Task task = (Task) adapterView.getItemAtPosition(position);

                Intent intentTaskDetails = new Intent(getApplicationContext(), ActivityTaskDetails.class);
                intentTaskDetails.putExtra("TaskID1c", task.getTask1cID());
                intentTaskDetails.putExtra("CreateDate", task.getCreateDate());
                intentTaskDetails.putExtra("UpToDate", task.getUpToDate());
                intentTaskDetails.putExtra("CustomerName", task.getCustomerName());
                intentTaskDetails.putExtra("CustomerPhone", task.getCustomerPhone());
                intentTaskDetails.putExtra("CustomerAddress", task.getCustomerAddress());
                intentTaskDetails.putExtra("ShippingWarehouse", task.getShippingWarehouse());
                intentTaskDetails.putExtra("Status", task.getStatusText());
                intentTaskDetails.putExtra("Goods", task.getGoods().substring(0, task.getGoods().length() - 1));//task.getGoods());
                intentTaskDetails.putExtra("StatusHistory", "TODO:");
                startActivity(intentTaskDetails);
            }
        });

        //SoapObject soapObject
        //Включение геолокации
        //GermesLocationListener.SetUpLocationListener(this);
    }

    private void fillData(int filterType) {
        sqLiteDatabase = germesDBOpenHelper.getWritableDatabase();
        Cursor cursor;

        switch (filterType){
            case FILTER_TYPE_ALL:
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + germesDBOpenHelper.TABLE_TASKS, null);
                break;
            case FILTER_TYPE_NEW:
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + germesDBOpenHelper.TABLE_TASKS
                        + " WHERE " + germesDBOpenHelper.TABLE_TASKS_COLUMN_STATUS + " = "
                        + Integer.toString(Task.STATUS_DELIVERING), null);
                break;
            case FILTER_TYPE_CLOSED:
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + germesDBOpenHelper.TABLE_TASKS
                        + " WHERE " + germesDBOpenHelper.TABLE_TASKS_COLUMN_STATUS + " <> "
                        + Integer.toString(Task.STATUS_DELIVERING), null);
                break;
            default:
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + germesDBOpenHelper.TABLE_TASKS, null);
                break;
        }

        tasks.clear();
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(this,
                        cursor.getInt(cursor.getColumnIndex(germesDBOpenHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_TASK_1C_ID)),
                        cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_CREATE_DATE)),
                        cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_UP_TO_DATE)),
                        cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_CUSTOMER_PHONE)),
                        cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_CUSTOMER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_SHIPPING_WAREHOUSE)),
                        cursor.getInt(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_STATUS)),
                        "");

                //add goods to task
                Cursor cursorGoods;
                cursorGoods = sqLiteDatabase.rawQuery("SELECT * FROM " + germesDBOpenHelper.TABLE_GOODS
                        + " WHERE " + germesDBOpenHelper.TABLE_GOODS_COLUMN_TASK_ID + " = "
                        + String.valueOf(cursor.getInt(cursor.getColumnIndex(germesDBOpenHelper.COLUMN_ID))), null);
                if (cursorGoods.moveToFirst()) {
                    do {
                        task.addGoods(cursorGoods.getString(cursorGoods.getColumnIndex(germesDBOpenHelper.TABLE_GOODS_COLUMN_GOODS)),
                                cursorGoods.getInt(cursorGoods.getColumnIndex(germesDBOpenHelper.TABLE_GOODS_COLUMN_QUANTITY)));
                    } while (cursorGoods.moveToNext());
                }

                tasks.add(task);
            } while (cursor.moveToNext());
        }

        taskAdapter = new TaskAdapter(this, tasks);
        listViewTasks.setAdapter(taskAdapter);

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
    //Show change status window
    public void onClickImageViewTaskStatus(View view) {
        Intent intentTaskStatus = new Intent(getApplicationContext(), ActivityTaskStatus.class);
        intentTaskStatus.putExtra("_id", ((Integer) view.getTag()).toString());
        startActivityForResult(intentTaskStatus, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            fillData(FILTER_TYPE_NEW);
        }
    }

    public void onClickImageButtonRefresh(View view) {
        SOAPHelperGermesSendDeliveryTasks soapHelperGermesSendDeliveryTasks;
        soapHelperGermesSendDeliveryTasks = new SOAPHelperGermesSendDeliveryTasks(getApplicationContext());
        soapHelperGermesSendDeliveryTasks.run();

        fillData(spinner.getSelectedItemPosition());
    }
}
