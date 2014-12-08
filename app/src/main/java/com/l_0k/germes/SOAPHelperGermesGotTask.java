package com.l_0k.germes;

import android.util.Log;

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

    private String identifier;
    private String createDate;

    SOAPHelperGermesGotTask(String identifier, String createDate){
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
            Log.w("Germes-----------------------", soapPrimitive.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processResult() {

    }
}
