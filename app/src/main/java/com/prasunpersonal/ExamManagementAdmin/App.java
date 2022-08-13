package com.prasunpersonal.ExamManagementAdmin;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.prasunpersonal.ExamManagementAdmin.Models.Admin;

public class App extends Application {
    private final String TAG = this.getClass().getSimpleName();
    public static RequestQueue QUEUE;
    public static Admin ME;
    public static SharedPreferences PREFERENCES;

    @Override
    public void onCreate() {
        super.onCreate();
        QUEUE = Volley.newRequestQueue(this);
        PREFERENCES = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }
}
