package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.ME;
import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.Admin;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityMainBinding binding;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("AUTHENTICATION", MODE_PRIVATE);

        if (preferences.contains("EMAIL") && preferences.contains("PASSWORD")) {
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.LOGIN, null, response -> {
                ME = new Gson().fromJson(response.toString(), Admin.class);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }, error -> {
                Log.d(TAG, "onCreate: ", error);
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("email", preferences.getString("EMAIL", ""));
                        body.put("password", preferences.getString("PASSWORD", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return body.toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(this, AuthenticationActivity.class));
                finish();
            }, 2000);
        }
    }
}