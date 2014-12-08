package com.l_0k.germes;

import android.os.AsyncTask;

/**
 * Created by knyazev_o on 05.11.2014.
 */

public abstract class SOAPHelper {
    protected String nameSpace;
    protected String URL;
    protected String soapAction;
    protected String methodName;

    SOAPHelper(){
    }

    public void run(){
        AsyncTaskSOAPHelper asyncTask = new AsyncTaskSOAPHelper();
        asyncTask.execute();
    }

    private class AsyncTaskSOAPHelper extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            processResult();
        }

        @Override
        protected Void doInBackground(String... strings) {
            executeInBackground();
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    protected abstract void executeInBackground();

    protected abstract void processResult();

}


