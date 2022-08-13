package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.ME;
import static com.prasunpersonal.ExamManagementAdmin.App.PREFERENCES;
import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.Admin;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (PREFERENCES.contains("EMAIL") && PREFERENCES.contains("PASSWORD")) {
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.ADMIN_LOGIN, null, response -> {
                ME = new Gson().fromJson(response.toString(), Admin.class);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }, error -> {
                Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("email", PREFERENCES.getString("EMAIL", ""));
                        body.put("password", PREFERENCES.getString("PASSWORD", ""));
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