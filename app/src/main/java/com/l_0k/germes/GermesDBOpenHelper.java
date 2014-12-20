package com.l_0k.germes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GermesDBOpenHelper extends SQLiteOpenHelper {

    //DB
    private static final String DATABASE_NAME = "Germes.db";
    private static final int DATABASE_VERSION = 3;
    public static final String COLUMN_ID = "_id";
    //Table AppUsers
    public static final String TABLE_APP_USERS = "AppUsers";
    public static final String TABLE_APP_USERS_COLUMN_LOGIN = "Login";
    public static final String TABLE_APP_USERS_COLUMN_PASSWORD = "Password";
    private static final String CREATE_TABLE_APP_USERS = "CREATE TABLE " + TABLE_APP_USERS + " (\n " +
            COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n " +
            TABLE_APP_USERS_COLUMN_LOGIN + " TEXT,\n " +
            TABLE_APP_USERS_COLUMN_PASSWORD + " TEXT);";
    //Table Tasks
    public static final String TABLE_TASKS = "Tasks";
    public static final String TABLE_TASKS_COLUMN_TASK_1C_ID = "Task1cID";
    public static final String TABLE_TASKS_COLUMN_CREATE_DATE = "CreateDate";
    public static final String TABLE_TASKS_COLUMN_UP_TO_DATE = "UpToDate";
    public static final String TABLE_TASKS_COLUMN_UP_TO_TIME = "UpToTime";
    public static final String TABLE_TASKS_COLUMN_CUSTOMER_NAME = "CustomerName";
    public static final String TABLE_TASKS_COLUMN_CUSTOMER_PHONE = "CustomerPhone";
    public static final String TABLE_TASKS_COLUMN_CUSTOMER_ADDRESS = "CustomerAddress";
    public static final String TABLE_TASKS_COLUMN_SHIPPING_WAREHOUSE = "ShippingWarehouse";
    public static final String TABLE_TASKS_COLUMN_STATUS = "Status";
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + " (\n " +
            COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n " +
            TABLE_TASKS_COLUMN_TASK_1C_ID + " TEXT,\n " +
            TABLE_TASKS_COLUMN_CREATE_DATE + " TEXT,\n " +
            TABLE_TASKS_COLUMN_UP_TO_DATE + " TEXT,\n " +
            TABLE_TASKS_COLUMN_UP_TO_TIME + " TEXT,\n " +
            TABLE_TASKS_COLUMN_CUSTOMER_NAME + " TEXT,\n " +
            TABLE_TASKS_COLUMN_CUSTOMER_PHONE + " TEXT,\n " +
            TABLE_TASKS_COLUMN_CUSTOMER_ADDRESS + " TEXT,\n " +
            TABLE_TASKS_COLUMN_SHIPPING_WAREHOUSE + " TEXT,\n " +
            TABLE_TASKS_COLUMN_STATUS + " TEXT);";
    //Table AppUsers
    public static final String TABLE_STATUSES_HISTORY = "StatusesHistory";
    public static final String TABLE_STATUSES_HISTORY_COLUMN_TASK_ID = "Task_id";
    public static final String TABLE_STATUSES_HISTORY_COLUMN_STATUS_TIMES_TAMP = "StatusTimeStamp";
    public static final String TABLE_STATUSES_HISTORY_COLUMN_STATUS = "Status";
    public static final String TABLE_STATUSES_HISTORY_COLUMN_LATITUDE = "Latitude";
    public static final String TABLE_STATUSES_HISTORY_COLUMN_LONGITUDE = "Longitude";
    public static final String TABLE_STATUSES_HISTORY_COLUMN_ADDRESS = "Address";
    public static final String TABLE_STATUSES_HISTORY_COLUMN_SENT_TO_1C = "SentTo1c";
    private static final String CREATE_TABLE_STATUSES_HISTORY = "CREATE TABLE " + TABLE_STATUSES_HISTORY + " (\n" +
            COLUMN_ID + " INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n " +
            TABLE_STATUSES_HISTORY_COLUMN_TASK_ID + " INTEGER NOT NULL,\n " +
            TABLE_STATUSES_HISTORY_COLUMN_STATUS_TIMES_TAMP + " TEXT,\n " +
            TABLE_STATUSES_HISTORY_COLUMN_LATITUDE + " TEXT,\n " +
            TABLE_STATUSES_HISTORY_COLUMN_LONGITUDE + " TEXT,\n " +
            TABLE_STATUSES_HISTORY_COLUMN_ADDRESS +  " TEXT,\n " +
            TABLE_STATUSES_HISTORY_COLUMN_STATUS + " TEXT,\n " +
            TABLE_STATUSES_HISTORY_COLUMN_SENT_TO_1C + " TEXT);";
    //Table Goods
    public static final String TABLE_GOODS = "Goods";
    public static final String TABLE_GOODS_COLUMN_TASK_ID = "Task_id";
    public static final String TABLE_GOODS_COLUMN_GOODS = "Goods";
    public static final String TABLE_GOODS_COLUMN_QUANTITY = "Quantity";
    private static final String CREATE_TABLE_GOODS = "CREATE TABLE " + TABLE_GOODS + " (\n" +
            COLUMN_ID + " INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n " +
            TABLE_GOODS_COLUMN_TASK_ID + " INTEGER NOT NULL,\n " +
            TABLE_GOODS_COLUMN_GOODS + " TEXT,\n " +
            TABLE_GOODS_COLUMN_QUANTITY + " INTEGER);";

    public GermesDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_APP_USERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_TASKS);
        sqLiteDatabase.execSQL(CREATE_TABLE_STATUSES_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_GOODS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(GermesDBOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_APP_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_TASKS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_STATUSES_HISTORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GOODS_COLUMN_QUANTITY);

        onCreate(sqLiteDatabase);
    }
}
