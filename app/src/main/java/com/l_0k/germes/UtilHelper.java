package com.l_0k.germes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by knyazev_o on 01.12.2014.
 * Это временный клас - пока не знаю архитектурно куда запихать
 */
public class UtilHelper {

    //отправляет статусы в 1с по задачам, что важно - задача сначала пишет свой статус в БД, а уж потом
    //по этим статусам я обхожу и отправляю на сервер, чтобы гарантировать доставку статуса в любом случае
    public void sendTasksStatusesTo1c(Context context){
        int _id;
        String identifier;
        String createDate;
        String status;
        //получаем список всех неотправленных на серввис статусов
        GermesDBOpenHelper germesDBOpenHelper = new GermesDBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = germesDBOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT T.Task1cID, T.CreateDate, S.* FROM "
                + GermesDBOpenHelper.TABLE_STATUSES_HISTORY + " S, "
                + GermesDBOpenHelper.TABLE_TASKS + " T "
                + " WHERE T._id = S.Task_id AND ("
                + GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_SENT_TO_1C
                + " <> \"1\"  OR SentTo1c is NULL)", null);
        if (cursor.moveToFirst()) {
            do {
                _id = cursor.getInt(cursor.getColumnIndex(GermesDBOpenHelper.COLUMN_ID));
                identifier = cursor.getString(cursor.getColumnIndex(GermesDBOpenHelper.TABLE_TASKS_COLUMN_TASK_1C_ID));
                createDate = cursor.getString(cursor.getColumnIndex(GermesDBOpenHelper.TABLE_TASKS_COLUMN_CREATE_DATE)).substring(0, 10);
                status = cursor.getString(cursor.getColumnIndex(GermesDBOpenHelper.TABLE_STATUSES_HISTORY_COLUMN_STATUS));

                //вызываем SOAP метод отправки статусов
                SOAPHelperGermesSetStatus soapHelperGermesSetStatus;
                soapHelperGermesSetStatus = new SOAPHelperGermesSetStatus(context, _id, identifier, createDate, status);
                soapHelperGermesSetStatus.run();
                //Log.w("Germes ------------------SetStatus", identifier);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    //в этом методе я проверяю - есть ли возможность залогиниться offline - это в том случае, если в
    //локальной БД сохранились логин и пароль, которые вводит пользователь
    public boolean testLogon(Context context, String userName, String password){
        boolean searchResult = false;

        try {
            GermesDBOpenHelper germesDBOpenHelper = new GermesDBOpenHelper(context);
            SQLiteDatabase sqLiteDatabase = germesDBOpenHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "
                    + GermesDBOpenHelper.TABLE_APP_USERS
                    + " WHERE " + GermesDBOpenHelper.TABLE_APP_USERS_COLUMN_LOGIN + " = \""
                    + userName + "\" AND " + GermesDBOpenHelper.TABLE_APP_USERS_COLUMN_PASSWORD
                    + " = \"" + password + "\"", null);
            if (cursor.moveToFirst()) {
                searchResult = true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            searchResult = false;
        }
        return searchResult;
    }

    /**
     * Добавление в БД имени пользователя и пароля при успешном входе
     */
    public void insertLoginPasswordToDB(Context context, String userName, String password){
        GermesDBOpenHelper germesDBOpenHelper = new GermesDBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = germesDBOpenHelper.getWritableDatabase();
        //удаляем логин и пароль из БД
        sqLiteDatabase.delete(GermesDBOpenHelper.TABLE_APP_USERS
                , GermesDBOpenHelper.TABLE_APP_USERS_COLUMN_LOGIN + " = ?"
                , new String[] { userName });
        //добавляем логин и пароль
        ContentValues contentValues = new ContentValues();
        contentValues.put(GermesDBOpenHelper.TABLE_APP_USERS_COLUMN_LOGIN, userName);
        contentValues.put(GermesDBOpenHelper.TABLE_APP_USERS_COLUMN_PASSWORD, password);
        sqLiteDatabase.insert(GermesDBOpenHelper.TABLE_APP_USERS, null, contentValues);
    }

}
