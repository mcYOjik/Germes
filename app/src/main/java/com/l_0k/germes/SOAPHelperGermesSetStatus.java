package com.l_0k.germes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by knyazev_o on 30.11.2014.
 * class for send task status to 1c
 */
public class SOAPHelperGermesSetStatus extends SOAPHelper  {
    private Context context;
    private int _id;
    private String identifier;
    private String createDate;
    private String status;
    public boolean isSend = false;

    SOAPHelperGermesSetStatus(Context context, int _id, String identifier, String createDate, String status){
        this.context = context;
        this._id = _id;
        this.identifier = identifier;
        this.identifier = identifier;
        this.createDate = createDate;
        this.status = status;

        nameSpace = "http://localhost/";
        URL = "http://androidapp.alser.kz/1cws/Germes.1cws";
        //URL = "http://test-1cdb101-001/1cws/Germes.1cws";
        soapAction = "http://localhost/#WebСервис_Germes:GermesSetStatus";
        methodName = "GermesSetStatus";
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
        //task date param
        PropertyInfo propertyInfoInputParameters1 = new PropertyInfo();
        Calendar calendar = Calendar.getInstance();
        propertyInfoInputParameters1.setName("Date");
        propertyInfoInputParameters1.setValue(createDate);
        propertyInfoInputParameters1.setType(Date.class);
        //task status param
        PropertyInfo propertyInfoInputParameters2 = new PropertyInfo();
        propertyInfoInputParameters2.setName("Status");
        propertyInfoInputParameters2.setValue(status);
        propertyInfoInputParameters2.setType(String.class);

        //Add the property to request object
        soapObjectRequest.addProperty(propertyInfoInputParameters);
        soapObjectRequest.addProperty(propertyInfoInputParameters1);
        soapObjectRequest.addProperty(propertyInfoInputParameters2);

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
            isSend = Boolean.valueOf(soapPrimitive.toString());
            //Log.d("Germes-----------------------", soapPrimitive.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processResult() {
//        Log.w("Germes------------------------processResult", "!");
        if (isSend) {
            GermesDBOpenHelper germesDBOpenHelper = new GermesDBOpenHelper(context);
            SQLiteDatabase sqLiteDatabase = germesDBOpenHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_SENT_TO_1C, "1");
            sqLiteDatabase.update(GermesDBOpenHelper.TABLE_STATUSES_HISTORY, contentValues
                    , GermesDBOpenHelper.COLUMN_ID
                    + " = ?", new String[] { Integer.toString(_id) });
            germesDBOpenHelper.close();
        }
    }
}
