package com.l_0k.germes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.net.InetAddress;



public class ActivityLogon extends Activity {

    //SharedPreferences sharedPreferences;
    GermesApp germesApp;
    EditText editTextIdentifier;
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);

        //StartService for download task from server
//        startService(new Intent(this, ServiceExchange.class));

        EditText editTextIdentifier = (EditText)findViewById(R.id.editTextIdentifier);
        germesApp = new GermesApp();
        germesApp.ReadSettings(this);
        //sharedPreferences = getPreferences(MODE_PRIVATE);
        //editTextIdentifier.setText(sharedPreferences.getString("Identifier", ""));
        editTextIdentifier.setText(GermesApp.Identifier);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_logon, menu);
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

    public void onClickButtonLogin(View view) {
        editTextIdentifier = (EditText) findViewById(R.id.editTextIdentifier);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        GermesApp.Identifier = editTextIdentifier.getText().toString();
        germesApp.SaveSettings(getApplicationContext());

        final ProgressDialog dialog = ProgressDialog.show(this, getResources().getString(R.string.TryLogin)
                , getResources().getString(R.string.PleaseWait), true, false);
        final Context context = this;

        AsyncTask asyncTaskLogon = new AsyncTask() {
            boolean isInternetAvailable = false;

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    InetAddress inetAddress = InetAddress.getByName("androidapp.alser.kz");

                    if (inetAddress.equals("")) {
                        isInternetAvailable = false;
                    } else {
                        isInternetAvailable = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    isInternetAvailable = false;
                }

                if (isInternetAvailable) {
                    SOAPHelperGermesAutoidentification soapHelperGermesAutoidentification;
                    soapHelperGermesAutoidentification = new SOAPHelperGermesAutoidentification(context, dialog,
                            editTextIdentifier.getText().toString(), editTextPassword.getText().toString());
                    soapHelperGermesAutoidentification.run();
                } else {
                    dialog.dismiss();
                    //проверка по локальной БД
                    UtilHelper utilHelper = new UtilHelper();
                    if (utilHelper.testLogon(context, editTextIdentifier.getText().toString(), editTextPassword.getText().toString())) {
                        //context.startService(new Intent(context, ServiceExchange.class)); //service of downloads new tasks
                        Intent intent = new Intent(context, ActivityTasks.class);
                        context.startActivity(intent);
                    } else {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alertDialog;
                                alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setTitle(context.getString(R.string.LoginError));
                                alertDialog.setMessage(context.getString(R.string.LoginOrPasswordIncorrect));
                                alertDialog.setIcon(R.drawable.ic_launcher);
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                alertDialog.show();
                            }
                        });
                    }
                }
                return null;
            }
        };
        asyncTaskLogon.execute();
    }
}
