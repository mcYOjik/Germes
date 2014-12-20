package com.l_0k.germes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationServices;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by knyazev_o on 13.11.2014.
 */

//This class for mark task as downloaded in 1c
public class SOAPHelperGermesGotTask extends SOAPHelper {

    private Context context;
    private long _id;
    private String identifier;
    private String createDate;
    private boolean isMarked; //show - marked task as downloaded in 1c or not (yes if SOAP method receive true)

    SOAPHelperGermesGotTask(Context context, long _id, String identifier, String createDate){
        this._id = _id;
        this.context = context;
        this.identifier = identifier;
        this.createDate = createDate;

        nameSpace = "http://localhost/";
        //URL = "http://10.0.2.22/UPP_TEST/Germes.1cws";
        URL = "http://androidapp.alser.kz/1cws/Germes.1cws";
        soapAction = "http://localhost/#WebСервис_Germes:GermesGotTask";
        methodName = "GermesGotTask";
    }

    @Override
    protected void executeInBackground() {
        //Create request
        SoapObject soapObjectRequest = new SoapObject(nameSpace, methodName);
        //Property which holds input parameter Identifier in fact Task.Task1cID
        PropertyInfo propertyInfoInputParameters = new PropertyInfo();
        propertyInfoInputParameters.setName("Identifier");
        propertyInfoInputParameters.setValue(identifier);
        propertyInfoInputParameters.setType(String.class);

        //current date
        PropertyInfo propertyInfoInputParameters1 = new PropertyInfo();
        Calendar calendar = Calendar.getInstance();
        propertyInfoInputParameters1.setName("Date");
        //propertyInfoInputParameters1.setValue(String.valueOf(calendar.get(Calendar.YEAR)) + "-"
        //    + String.valueOf(calendar.get(Calendar.MONTH)) + "-"
        //   + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        propertyInfoInputParameters1.setValue(createDate);
        propertyInfoInputParameters1.setType(Date.class);
        //Add the property to request object
        soapObjectRequest.addProperty(propertyInfoInputParameters);
        soapObjectRequest.addProperty(propertyInfoInputParameters1);
        //Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        //Set output SOAP object
        envelope.setOutputSoapObject(soapObjectRequest);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            //Involve web service
            androidHttpTransport.call(soapAction, envelope);
            //Get the response
            SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
            isMarked = Boolean.valueOf(soapPrimitive.toString());
            Log.d("SOAPHelperGermesGotTask", soapPrimitive.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processResult() {
        //insert Location, status, time stamp to
        if (isMarked) {
            GermesDBOpenHelper germesDBOpenHelper = new GermesDBOpenHelper(context);
            SQLiteDatabase sqLiteDatabase = germesDBOpenHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_TASK_ID, _id);
            Calendar calendar = Calendar.getInstance();
            contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_STATUS_TIMES_TAMP
                    , String.valueOf(calendar.get(Calendar.YEAR))
                    + "-" + String.valueOf(calendar.get(Calendar.MONTH))
                    + "-" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
                    + "T" + String.valueOf(calendar.get(Calendar.HOUR))
                    + ":" + String.valueOf(calendar.get(Calendar.MINUTE))
                    + ":" + String.valueOf(calendar.get(Calendar.SECOND)));
            contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_STATUS, Task.STATUS_DELIVERING);

            //Берём последнюю известную локацию
            if (ActivityTasks.mGoogleApiClient.isConnected()) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(ActivityTasks.mGoogleApiClient);
                if (location != null) {
                    contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_LATITUDE, String.valueOf(location.getLatitude()));
                    contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_LONGITUDE, String.valueOf(location.getLongitude()));

                    //пробуем найти и добавить адрес
                    String address = UtilHelper.getAddress(context, location, 5);
                    if (address != "") {
                        contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_ADDRESS, address);
                    }
                }
            };

            sqLiteDatabase.insert(GermesDBOpenHelper.TABLE_STATUSES_HISTORY, null, contentValues);
            germesDBOpenHelper.close();
        }
    }
}
