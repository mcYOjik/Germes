package com.l_0k.germes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
//Окно тасков

public class ActivityTasks extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static GermesDBOpenHelper germesDBOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    ArrayList<Task> tasks = new ArrayList<Task>();
    TaskAdapter taskAdapter;
    ListView listViewTasks;
    Spinner spinner;

    public static GoogleApiClient mGoogleApiClient;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

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
                intentTaskDetails.putExtra(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_TASK_ID, task.get_id());
                intentTaskDetails.putExtra("TaskID1c", task.getTask1cID());
                intentTaskDetails.putExtra("CreateDate", task.getCreateDate());
                intentTaskDetails.putExtra("UpToDate", task.getUpToDate());
                intentTaskDetails.putExtra("UpToTime", task.getUpToTime());
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

        //Включение геолокации
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //запуск сервиса
        startService(new Intent(this, ServiceExchange.class));
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
                        cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.TABLE_TASKS_COLUMN_UP_TO_TIME)),
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
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }

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

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(5 * 60 * 1000) //no metter how often other application updates location
                .setInterval(5 * 60 * 1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), DIALOG_ERROR);
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        //i need updates, but don't need immediately reaction
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((ActivityTasks)getActivity()).onDialogDismissed();
            System.exit(0);
        }
    }
}
