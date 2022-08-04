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
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentCoursesBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CoursesFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    public CoursesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentCoursesBinding binding = FragmentCoursesBinding.inflate(inflater, container, false);
        assert getParentFragment() != null;
        CourseStructureViewModel viewModel = new ViewModelProvider(getParentFragment()).get(CourseStructureViewModel.class);
        binding.allCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel.setSelectedCourse(null);
        viewModel.getSelectedDegree().observe(getViewLifecycleOwner(), degree -> {
            if (degree != null) {
                Log.d(TAG, "onCreateView: "+degree.getCourses());
                binding.addCourseArea.setVisibility(View.VISIBLE);
                binding.allCourses.setAdapter(new CourseStructureItemAdapter<>(degree.getCourses(), (course, position) -> viewModel.setSelectedCourse(course)));
            } else {
                binding.addCourseArea.setVisibility(View.GONE);
                viewModel.setSelectedCourse(null);
            }
        });
        binding.addCourse.setOnClickListener(v -> {
            if (binding.courseName.getText().toString().trim().isEmpty()) {
                binding.courseName.setError("Course name is required!");
                return;
            }
            binding.addCourse.setVisibility(View.GONE);
            binding.addCourseProgress.setVisibility(View.VISIBLE);
            binding.courseName.setEnabled(false);
            Course course = new Course(binding.courseName.getText().toString().trim());
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.ADD_COURSE, null, response -> {
                if (viewModel.getSelectedDegree().getValue() != null) {
                    Degree selectedDegree = viewModel.getSelectedDegree().getValue();
                    selectedDegree.getCourses().clear();
                    selectedDegree.getCourses().addAll(new Gson().fromJson(response.toString(), Degree.class).getCourses());
                    Objects.requireNonNull(binding.allCourses.getAdapter()).notifyDataSetChanged();
                }
                binding.addCourse.setVisibility(View.VISIBLE);
                binding.addCourseProgress.setVisibility(View.GONE);
                binding.courseName.setEnabled(true);
                binding.courseName.getText().clear();
            }, error -> {
                Log.d(TAG, "Add Course Error: ", error);
                binding.addCourse.setVisibility(View.VISIBLE);
                binding.addCourseProgress.setVisibility(View.GONE);
                binding.courseName.setEnabled(true);
            }) {
                @Override
                public byte[] getBody() {
                    Map<String, String> object = new HashMap<>();
                    object.put("degree", Objects.requireNonNull(viewModel.getSelectedDegree().getValue()).get_id());
                    object.put("course", new Gson().toJson(course));
                    return new JSONObject(object).toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });
        return binding.getRoot();
    }
}