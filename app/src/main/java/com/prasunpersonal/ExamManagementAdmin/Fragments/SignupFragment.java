package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;
import static com.prasunpersonal.ExamManagementAdmin.App.ME;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementAdmin.Activities.HomeActivity;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.Admin;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentSignupBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class SignupFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentSignupBinding binding;

    public SignupFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);

        binding.signup.setOnClickListener(v -> {
            if (binding.name.getText().toString().trim().isEmpty()) {
                binding.name.setError("Name is required!");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).matches()) {
                binding.email.setError("A valid email is required!");
                return;
            }
            if (binding.pass1.getText().toString().trim().length() < 6) {
                binding.pass1.setError("Enter a valid password of minimum length 6!");
                return;
            }
            if (!binding.pass2.getText().toString().trim().equals(binding.pass1.getText().toString().trim())) {
                binding.pass2.setError("Passwords doesn't match!");
                return;
            }

            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.SIGNUP, null, response -> {
                ME = new Gson().fromJson(response.toString(), Admin.class);
                if (binding.remember.isChecked()) {
                    requireActivity().getSharedPreferences("AUTHENTICATION", MODE_PRIVATE).edit().putString("EMAIL", ME.getEmail()).putString("PASSWORD", ME.getPassword()).apply();
                }
                startActivity(new Intent(requireContext(), HomeActivity.class));
                requireActivity().finish();
            }, error -> {

            }) {
                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("name", binding.name.getText().toString().trim());
                        body.put("email", binding.email.getText().toString().trim());
                        body.put("password", binding.pass1.getText().toString().trim());
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