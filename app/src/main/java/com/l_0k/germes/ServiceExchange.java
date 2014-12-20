package com.l_0k.germes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class ServiceExchange extends Service {
    public ServiceExchange() {

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServiceExchange", "--------------------------------------");
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        if (ActivityTasks.mGoogleApiClient != null) {
                            if (!ActivityTasks.mGoogleApiClient.isConnected()) {
                                ActivityTasks.mGoogleApiClient.connect();
                            }
                        }

                        //send task statuses
                        UtilHelper sendStatusTo1c = new UtilHelper();
                        sendStatusTo1c.sendTasksStatusesTo1c(getApplicationContext());

                        //download new tasks
                        SOAPHelperGermesSendDeliveryTasks soapHelperGermesSendDeliveryTasks;
                        soapHelperGermesSendDeliveryTasks = new SOAPHelperGermesSendDeliveryTasks(getApplicationContext());
                        soapHelperGermesSendDeliveryTasks.run();

                        TimeUnit.SECONDS.sleep(300);
                        //Log.w("Germes ------------------", "Service Run");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
