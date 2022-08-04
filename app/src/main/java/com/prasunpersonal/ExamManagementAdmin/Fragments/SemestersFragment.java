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
import com.prasunpersonal.ExamManagementAdmin.Models.Course;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.Models.Semester;
import com.prasunpersonal.ExamManagementAdmin.Models.Stream;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentSemestersBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SemestersFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    public SemestersFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSemestersBinding binding = FragmentSemestersBinding.inflate(inflater, container, false);
        assert getParentFragment() != null;
        CourseStructureViewModel viewModel = new ViewModelProvider(getParentFragment()).get(CourseStructureViewModel.class);
        binding.allSemesters.setLayoutManager(new LinearLayoutManager(requireContext()));
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
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.ADD_SEMESTER, null, response -> {
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
                    Map<String, String> object = new HashMap<>();
                    object.put("degree", Objects.requireNonNull(viewModel.getSelectedDegree().getValue()).get_id());
                    object.put("course", Objects.requireNonNull(viewModel.getSelectedCourse().getValue()).get_id());
                    object.put("stream", Objects.requireNonNull(viewModel.getSelectedStream().getValue()).get_id());
                    object.put("regulation", Objects.requireNonNull(viewModel.getSelectedRegulation().getValue()).get_id());
                    object.put("semester", new Gson().toJson(semester));
                    return new JSONObject(object).toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

        return binding.getRoot();
    }
}