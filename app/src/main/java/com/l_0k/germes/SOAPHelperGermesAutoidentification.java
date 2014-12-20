package com.l_0k.germes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by knyazev_o on 05.11.2014.
 */

public class SOAPHelperGermesAutoidentification extends SOAPHelper
{
    protected String nameSpace;
    protected String URL;
    protected String soapAction;
    protected String methodName;

    ProgressDialog dialog;
    private Context context;
    private boolean logonResult = false;

    private String identifier;
    private String password;

    SOAPHelperGermesAutoidentification(Context context, ProgressDialog dialog, String identifier, String password) {
        this.dialog = dialog;
        this.context = context;
        this.identifier = identifier;
        this.password = password;

        nameSpace = "http://localhost/";
        //URL = "http://10.0.2.22/UPP_TEST/Germes.1cws";
        URL = "http://androidapp.alser.kz/1cws/Germes.1cws";
        soapAction = "http://localhost/#WebСервис_Germes:GermesAutoidentification";
        methodName = "GermesAutoidentification";
    }

    @Override
    protected void executeInBackground() {
        //Create request
        SoapObject soapObjectRequest = new SoapObject(nameSpace, methodName);
        //Property which holds input parameters
        PropertyInfo propertyInfoInputParameters = new PropertyInfo();
        propertyInfoInputParameters.setName("Identifier");
        propertyInfoInputParameters.setValue(identifier);
        propertyInfoInputParameters.setType(String.class);
        soapObjectRequest.addProperty(propertyInfoInputParameters);
        //Add the property to request object
        PropertyInfo propertyInfoInputParameters2 = new PropertyInfo();
        propertyInfoInputParameters2.setName("Password");
        propertyInfoInputParameters2.setValue(password);
        propertyInfoInputParameters2.setType(String.class);
        //Add the property to request object
        soapObjectRequest.addProperty(propertyInfoInputParameters2);
        //Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        //Set output SOAP object
        envelope.setOutputSoapObject(soapObjectRequest);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            //Invole web service
            androidHttpTransport.call(soapAction, envelope);
            //Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            logonResult = Boolean.parseBoolean(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processResult(){
        dialog.dismiss();

        if (logonResult) {
            //Logon success
            UtilHelper utilHelper = new UtilHelper();
            utilHelper.insertLoginPasswordToDB(context, identifier, password);

            //context.startService(new Intent(context, ServiceExchange.class)); //service of downloads new tasks
            Intent intent = new Intent(context, ActivityTasks.class);
            context.startActivity(intent);
        } else {
            //Logon failed
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
    }
}
