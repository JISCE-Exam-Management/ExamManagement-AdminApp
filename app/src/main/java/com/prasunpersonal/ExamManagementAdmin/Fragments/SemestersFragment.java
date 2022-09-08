package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.annotation.SuppressLint;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementAdmin.Adapters.CourseStructureItemAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.CourseStructureViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Course;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.Models.Semester;
import com.prasunpersonal.ExamManagementAdmin.Models.Stream;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentSemestersBinding;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SemestersFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    public SemestersFragment() {}

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSemestersBinding binding = FragmentSemestersBinding.inflate(inflater, container, false);
        CourseStructureViewModel viewModel = new ViewModelProvider(requireActivity()).get(CourseStructureViewModel.class);
        binding.allSemesters.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.allSemesters.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        viewModel.setSelectedSemester(null);
        viewModel.getSelectedRegulation().observe(getViewLifecycleOwner(), regulation -> {
            if (regulation != null) {
                binding.addSemesterArea.setVisibility(View.VISIBLE);
                binding.allSemesters.setAdapter(new CourseStructureItemAdapter<>(regulation.getSemesters(), (semester, position) -> viewModel.setSelectedSemester(semester)));
            } else {
                binding.addSemesterArea.setVisibility(View.GONE);
                viewModel.setSelectedSemester(null);
            }
        });

        binding.addSemester.setOnClickListener(v -> {
            if (binding.semesterName.getText().toString().trim().isEmpty()) {
                binding.semesterName.setError("Semester name is required!");
                return;
            }
            binding.addSemester.setVisibility(View.GONE);
            binding.addSemesterProgress.setVisibility(View.VISIBLE);
            binding.semesterName.setEnabled(false);
            Semester semester = new Semester(binding.semesterName.getText().toString().trim());

            Map<String, String> params = new HashMap<>();
            params.put("degree", Objects.requireNonNull(viewModel.getSelectedDegree().getValue()).get_id());
            params.put("course", Objects.requireNonNull(viewModel.getSelectedCourse().getValue()).get_id());
            params.put("stream", Objects.requireNonNull(viewModel.getSelectedStream().getValue()).get_id());
            params.put("regulation", Objects.requireNonNull(viewModel.getSelectedRegulation().getValue()).get_id());
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s%s",API.ADD_SEMESTER, API.getQuery(params)), null, response -> {
                if (viewModel.getSelectedRegulation().getValue() != null) {
                    Degree degree = new Gson().fromJson(response.toString(), Degree.class);
                    Course course = degree.getCourses().get(degree.getCourses().indexOf(viewModel.getSelectedCourse().getValue()));
                    Stream stream = course.getStreams().get(course.getStreams().indexOf(viewModel.getSelectedStream().getValue()));
                    viewModel.getSelectedRegulation().getValue().getSemesters().clear();
                    viewModel.getSelectedRegulation().getValue().getSemesters().addAll(stream.getRegulations().get(stream.getRegulations().indexOf(viewModel.getSelectedRegulation().getValue())).getSemesters());
                    Objects.requireNonNull(binding.allSemesters.getAdapter()).notifyDataSetChanged();
                }
                binding.addSemester.setVisibility(View.VISIBLE);
                binding.addSemesterProgress.setVisibility(View.GONE);
                binding.semesterName.setEnabled(true);
                binding.semesterName.getText().clear();
            }, error -> {
                Log.d(TAG, "onCreate: ", error);
                binding.addSemester.setVisibility(View.VISIBLE);
                binding.addSemesterProgress.setVisibility(View.GONE);
                binding.semesterName.setEnabled(true);
            }) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(semester).getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

        return binding.getRoot();
    }
}