package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.prasunpersonal.ExamManagementAdmin.App.ME;
import static com.prasunpersonal.ExamManagementAdmin.App.PREFERENCES;
import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementAdmin.Activities.HomeActivity;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.Admin;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class LoginFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentLoginBinding binding;

    public LoginFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.login.setOnClickListener(v -> {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).matches()) {
                binding.email.setError("A valid email is required!");
                return;
            }
            if (binding.pass.getText().toString().trim().length() < 6) {
                binding.pass.setError("Enter a valid password of minimum length 6!");
                return;
            }

            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.ADMIN_LOGIN, null, response -> {
                ME = new Gson().fromJson(response.toString(), Admin.class);
                if (binding.remember.isChecked()) {
                    PREFERENCES.edit().putString("EMAIL", ME.getEmail()).putString("PASSWORD", ME.getPassword()).apply();
                }
                startActivity(new Intent(requireContext(), HomeActivity.class));
                requireActivity().finish();
            }, error -> {
                Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("email", binding.email.getText().toString().trim());
                        body.put("password", binding.pass.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return body.toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

        return binding.getRoot();
    }
}