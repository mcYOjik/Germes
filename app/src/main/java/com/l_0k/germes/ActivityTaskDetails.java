package com.l_0k.germes;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ActivityTaskDetails extends Activity {

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
        ((TextView)findViewById(R.id.StatusHistory)).setText(getString(R.string.StatusHistory) + ":\n" + extras.getString("StatusHistory"));
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
