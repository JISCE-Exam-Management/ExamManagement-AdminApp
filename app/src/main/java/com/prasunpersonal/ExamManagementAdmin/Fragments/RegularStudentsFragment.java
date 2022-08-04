package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Activities.StudentDetailsActivity;
import com.prasunpersonal.ExamManagementAdmin.Adapters.StudentAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.ExamDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentRegularStudentsBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegularStudentsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    public RegularStudentsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentRegularStudentsBinding binding = FragmentRegularStudentsBinding.inflate(inflater, container, false);
        ExamDetailsViewModel viewModel = new ViewModelProvider(requireActivity()).get(ExamDetailsViewModel.class);
        viewModel.getSetSelectedExam().observe(getViewLifecycleOwner(), exam -> {
            QUEUE.add(new JsonArrayRequest(Request.Method.POST, API.EXAM_CANDIDATES, null, response -> {
                ArrayList<Student> students = new Gson().fromJson(response.toString(), new TypeToken<List<Student>>(){}.getType());
                try {
                    binding.regularCandidates.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.regularCandidates.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
                    binding.regularCandidates.setAdapter(new StudentAdapter(students, (student, position) -> {
                        startActivity(new Intent(requireContext(), StudentDetailsActivity.class).putExtra("STUDENT_ID", student.get_id()));
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> Log.d(TAG, "onCreate: ", error)) {
                @Override
                public byte[] getBody() {
                    Map<String, List<String>> body = new HashMap<>();
                    body.put("candidates", new ArrayList<>(exam.getRegularCandidates().keySet()));
                    Log.d(TAG, "getBody: "+new JSONObject(body));
                    return new JSONObject(body).toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });
        return binding.getRoot();
    }
}