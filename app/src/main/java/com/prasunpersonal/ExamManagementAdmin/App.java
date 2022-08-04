package com.prasunpersonal.ExamManagementAdmin;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.prasunpersonal.ExamManagementAdmin.Models.Admin;

public class App extends Application {
    private final String TAG = this.getClass().getSimpleName();
    public static RequestQueue QUEUE;
    public static Admin ME;

    @Override
    public void onCreate() {
        super.onCreate();
        QUEUE = Volley.newRequestQueue(this);
    }
}
