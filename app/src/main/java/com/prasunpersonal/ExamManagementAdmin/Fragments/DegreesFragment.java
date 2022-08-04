package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementAdmin.Adapters.CourseStructureItemAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.CourseStructureViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentDegreesBinding;

import java.nio.charset.StandardCharsets;

public class DegreesFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    public DegreesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDegreesBinding binding = FragmentDegreesBinding.inflate(inflater, container, false);
        assert getParentFragment() != null;
        CourseStructureViewModel viewModel = new ViewModelProvider(getParentFragment()).get(CourseStructureViewModel.class);
        binding.allDegrees.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel.setSelectedDegree(null);
        viewModel.getAllDegrees().observe(getViewLifecycleOwner(), degrees -> {
            if (degrees != null) {
                binding.addDegreeArea.setVisibility(View.VISIBLE);
                binding.allDegrees.setAdapter(new CourseStructureItemAdapter<>(degrees, (degree, position) -> viewModel.setSelectedDegree(degree)));
            } else {
                binding.addDegreeArea.setVisibility(View.GONE);
                viewModel.setSelectedDegree(null);
            }
        });
        binding.addDegree.setOnClickListener(v -> {
            if (binding.degreeName.getText().toString().trim().isEmpty()) {
                binding.degreeName.setError("Degree name is required");
                return;
            }
            binding.addDegree.setVisibility(View.GONE);
            binding.addDegreeProgress.setVisibility(View.VISIBLE);
            binding.degreeName.setEnabled(false);
            Degree degree = new Degree(binding.degreeName.getText().toString().trim());
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.ADD_DEGREE, null, response -> {
                if (viewModel.getAllDegrees().getValue() != null) viewModel.getAllDegrees().getValue().add(new Gson().fromJson(response.toString(), Degree.class));
                binding.addDegree.setVisibility(View.VISIBLE);
                binding.addDegreeProgress.setVisibility(View.GONE);
                binding.degreeName.setEnabled(true);
                binding.degreeName.getText().clear();
            }, error -> {
                Log.d(TAG, "onCreate: ", error);
                binding.addDegree.setVisibility(View.VISIBLE);
                binding.addDegreeProgress.setVisibility(View.GONE);
                binding.degreeName.setEnabled(true);
            }) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(degree).getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });
        return binding.getRoot();
    }
}