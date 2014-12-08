package com.l_0k.germes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by knyazev_o on 10.11.2014.
 */
public class GermesApp {

    private final String SETTING_FILE_NAME = "SettingsGermes";
    private SharedPreferences sharedPreferences;

    public static String Identifier;//login name

    public void ReadSettings(Context context){
        sharedPreferences  = context.getSharedPreferences(SETTING_FILE_NAME, context.MODE_PRIVATE);
        Identifier = sharedPreferences.getString("Identifier", "");
    };

    public void SaveSettings(Context context){
        sharedPreferences = context.getSharedPreferences(SETTING_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Identifier", Identifier);
        editor.commit();
    }

}
