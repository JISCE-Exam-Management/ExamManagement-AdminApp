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
import com.prasunpersonal.ExamManagementAdmin.Models.Paper;
import com.prasunpersonal.ExamManagementAdmin.Models.Regulation;
import com.prasunpersonal.ExamManagementAdmin.Models.Stream;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentPapersBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PapersFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    public PapersFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentPapersBinding binding = FragmentPapersBinding.inflate(inflater, container, false);
        assert getParentFragment() != null;
        CourseStructureViewModel viewModel = new ViewModelProvider(getParentFragment()).get(CourseStructureViewModel.class);
        binding.allPapers.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel.getSelectedSemester().observe(getViewLifecycleOwner(), semester -> {
            if (semester != null) {
                binding.addPapersArea.setVisibility(View.VISIBLE);
                binding.allPapers.setAdapter(new CourseStructureItemAdapter<>(semester.getPapers(), (paper, position) -> {}));
            } else {
                binding.addPapersArea.setVisibility(View.GONE);
            }
        });

        binding.addPaper.setOnClickListener(v -> {
            if (binding.paperName.getText().toString().trim().isEmpty()) {
                binding.paperName.setError("Paper name is required!");
                return;
            }
            if (binding.paperCode.getText().toString().trim().isEmpty()) {
                binding.paperCode.setError("Paper code is required!");
                return;
            }
            binding.addPaper.setVisibility(View.GONE);
            binding.addPaperProgress.setVisibility(View.VISIBLE);
            binding.paperName.setEnabled(false);
            binding.paperCode.setEnabled(false);
            Paper paper = new Paper(binding.paperCode.getText().toString().trim(), binding.paperName.getText().toString().trim());
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.ADD_PAPER, null, response -> {
                if (viewModel.getSelectedSemester().getValue() != null) {
                    Degree degree = new Gson().fromJson(response.toString(), Degree.class);
                    Course course = degree.getCourses().get(degree.getCourses().indexOf(viewModel.getSelectedCourse().getValue()));
                    Stream stream = course.getStreams().get(course.getStreams().indexOf(viewModel.getSelectedStream().getValue()));
                    Regulation regulation = stream.getRegulations().get(stream.getRegulations().indexOf(viewModel.getSelectedRegulation().getValue()));
                    viewModel.getSelectedSemester().getValue().getPapers().clear();
                    viewModel.getSelectedSemester().getValue().getPapers().addAll(regulation.getSemesters().get(regulation.getSemesters().indexOf(viewModel.getSelectedSemester().getValue())).getPapers());
                    Objects.requireNonNull(binding.allPapers.getAdapter()).notifyDataSetChanged();
                }
                binding.addPaper.setVisibility(View.VISIBLE);
                binding.addPaperProgress.setVisibility(View.GONE);
                binding.paperName.setEnabled(true);
                binding.paperCode.setEnabled(true);
                binding.paperName.getText().clear();
                binding.paperCode.getText().clear();
            }, error -> {
                Log.d(TAG, "onCreate: ", error);
                binding.addPaper.setVisibility(View.VISIBLE);
                binding.addPaperProgress.setVisibility(View.GONE);
                binding.paperName.setEnabled(true);
                binding.paperCode.setEnabled(true);
            }) {
                @Override
                public byte[] getBody() {
                    Map<String, String> object = new HashMap<>();
                    object.put("degree", Objects.requireNonNull(viewModel.getSelectedDegree().getValue()).get_id());
                    object.put("course", Objects.requireNonNull(viewModel.getSelectedCourse().getValue()).get_id());
                    object.put("stream", Objects.requireNonNull(viewModel.getSelectedStream().getValue()).get_id());
                    object.put("regulation", Objects.requireNonNull(viewModel.getSelectedRegulation().getValue()).get_id());
                    object.put("semester", Objects.requireNonNull(viewModel.getSelectedSemester().getValue()).get_id());
                    object.put("paper", new Gson().toJson(paper));
                    return new JSONObject(object).toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

        return binding.getRoot();
    }
}