package com.l_0k.germes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by knyazev_o on 05.11.2014.
 * this class download task form 1c
 */
public class SOAPHelperGermesSendDeliveryTasks extends SOAPHelper {

    private String Identifier;
    private Context context;
    private boolean needToNotify = false; //маркер, что получено новое задание на доставку - требуется уведомить пользователя

    SOAPHelperGermesSendDeliveryTasks(Context _context){
        context = _context;

        Identifier = GermesApp.Identifier;

        nameSpace = "http://localhost/";
        //URL = "http://10.0.2.22/UPP_TEST/Germes.1cws";
        URL = "http://androidapp.alser.kz/1cws/Germes.1cws";
        soapAction = "http://localhost/#WebСервис_Germes:GermesSendDeliveryTasks";
        methodName = "GermesSendDeliveryTasks";
    }

    @Override
    protected void executeInBackground() {
        //Create request
        SoapObject soapObjectRequest = new SoapObject(nameSpace, methodName);
        //Property which holds input parameters
        PropertyInfo propertyInfoInputParameters = new PropertyInfo();
        propertyInfoInputParameters.setName("Identifier");
        propertyInfoInputParameters.setValue(Identifier);
        propertyInfoInputParameters.setType(String.class);
        //Add the property to request object
        soapObjectRequest.addProperty(propertyInfoInputParameters);
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
            SoapObject soapObject = (SoapObject)envelope.getResponse();
            for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                SoapObject soapObjectTask = (SoapObject)soapObject.getProperty(i);

                Task task = new Task();
                task.context = context;

                task.setTask1cID(soapObjectTask.getProperty(0).toString());
                task.setCreateDate(soapObjectTask.getProperty(1).toString());
                task.setUpToDate(soapObjectTask.getProperty(2).toString());
                task.setCustomerName(soapObjectTask.getProperty(3).toString());
                task.setCustomerPhone(soapObjectTask.getProperty(4).toString());
                task.setCustomerAddress(soapObjectTask.getProperty(5).toString());
                task.setShippingWarehouse(soapObjectTask.getProperty(6).toString());
                task.setStatus(0);

                if (soapObjectTask.getPropertyCount() > 7) {
                    for (int propertyGoods = 7; propertyGoods < soapObjectTask.getPropertyCount(); propertyGoods++) {
                        SoapObject soapObjectGoods = (SoapObject)soapObjectTask.getProperty(propertyGoods);
                        task.addGoods(soapObjectGoods.getProperty(0).toString()
                                , Integer.parseInt(soapObjectGoods.getProperty(1).toString()));
                    }
                }

                Log.w("Germes", task.getTask1cID());

                taskToDB(task);
                needToNotify = true; //получено новое задание на доставку будем уведомлять
            }
            //Log.w("Germes", "Done");

            //fillData(FILTER_TYPE_NEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void taskToDB(Task taskForDownload){
        //Search task in DB
        SQLiteDatabase sqLiteDatabase;
        GermesDBOpenHelper germesDBOpenHelper = new GermesDBOpenHelper(context);

        sqLiteDatabase = germesDBOpenHelper.getWritableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + germesDBOpenHelper.TABLE_TASKS
                + " WHERE " + germesDBOpenHelper.TABLE_TASKS_COLUMN_TASK_1C_ID + "=?"
                , new String[] { taskForDownload.getTask1cID() });
        if (cursor.moveToFirst()) {//delete Task, if exists
            //delete task
            //save id for goods
            String task_id = cursor.getString(cursor.getColumnIndex(germesDBOpenHelper.COLUMN_ID));
            //delete task
            sqLiteDatabase.delete(germesDBOpenHelper.TABLE_TASKS
                    , germesDBOpenHelper.TABLE_TASKS_COLUMN_TASK_1C_ID + "=?"
                    , new String[] { taskForDownload.getTask1cID() });
            //delete task's goods
            sqLiteDatabase.delete(germesDBOpenHelper.TABLE_GOODS
                    , germesDBOpenHelper.TABLE_GOODS_COLUMN_TASK_ID + "=?"
                    , new String[] { task_id });

        }

        //prepare for insert
        ContentValues contentValues = new ContentValues();
        contentValues.put(germesDBOpenHelper.TABLE_TASKS_COLUMN_TASK_1C_ID, taskForDownload.getTask1cID());
        contentValues.put(germesDBOpenHelper.TABLE_TASKS_COLUMN_CREATE_DATE, taskForDownload.getCreateDate());
        contentValues.put(germesDBOpenHelper.TABLE_TASKS_COLUMN_UP_TO_DATE, taskForDownload.getUpToDate());
        contentValues.put(germesDBOpenHelper.TABLE_TASKS_COLUMN_CUSTOMER_NAME, taskForDownload.getCustomerName());
        contentValues.put(germesDBOpenHelper.TABLE_TASKS_COLUMN_CUSTOMER_PHONE, taskForDownload.getCustomerPhone());
        contentValues.put(germesDBOpenHelper.TABLE_TASKS_COLUMN_CUSTOMER_ADDRESS, taskForDownload.getCustomerAddress());
        contentValues.put(germesDBOpenHelper.TABLE_TASKS_COLUMN_SHIPPING_WAREHOUSE, taskForDownload.getShippingWarehouse());
        contentValues.put(germesDBOpenHelper.TABLE_TASKS_COLUMN_STATUS, taskForDownload.getStatus());
//        if (cursor.moveToFirst()) {//Update if exists
//            sqLiteDatabase.update(germesDBOpenHelper.TABLE_TASKS, contentValues,
//                    germesDBOpenHelper.TABLE_TASKS_COLUMN_TASK_1C_ID + "=?",  new String[] { taskToUpdate.getTask1cID() });
//        } else { //Insert if new
//            long task_id = sqLiteDatabase.insert(germesDBOpenHelper.TABLE_TASKS, null, contentValues);
//        }
        long task_id = sqLiteDatabase.insert(germesDBOpenHelper.TABLE_TASKS, null, contentValues);

        //insert goods
        if (taskForDownload.getGoodsListItemsCount() > 0) {
            for (int i = 0; i < taskForDownload.getGoodsListItemsCount(); i++) {
                ContentValues contentValuesGoods = new ContentValues();
                contentValuesGoods.put(germesDBOpenHelper.TABLE_GOODS_COLUMN_TASK_ID, task_id);
                contentValuesGoods.put(germesDBOpenHelper.TABLE_GOODS_COLUMN_GOODS, taskForDownload.getGoodsListGoods(i));
                contentValuesGoods.put(germesDBOpenHelper.TABLE_GOODS_COLUMN_QUANTITY, taskForDownload.getGoodsListQuantity(i));
                sqLiteDatabase.insert(germesDBOpenHelper.TABLE_GOODS, null, contentValuesGoods);
            }
        }

        germesDBOpenHelper.close();
        cursor.close();

        //Mark Task as downloaded - marked task will not be send by service to app
        SOAPHelperGermesGotTask soapHelperGermesGotTask;
        soapHelperGermesGotTask = new SOAPHelperGermesGotTask(taskForDownload.getTask1cID()
                , taskForDownload.getCreateDate().substring(0, 10));
        soapHelperGermesGotTask.run();
    }

    @Override
    protected void processResult() {
        //уведомить пользователя о новой задаче на доставку
        if (needToNotify) {
            Intent notificationIntent = new Intent(context, ActivityTasks.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent
                    , PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Resources res = context.getResources();
            Notification.Builder builder = new Notification.Builder(context);

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                    .setTicker(context.getResources().getString(R.string.NewDeliveryTaskNotify))
                    .setWhen(System.currentTimeMillis()) // java.lang.System.currentTimeMillis()
                    .setAutoCancel(true)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(context.getResources().getString(R.string.NewDeliveryTaskNotify)); // Текст уведомленимя

            Notification n = builder.getNotification();

            nm.notify(101, n);
        }
    }
}
