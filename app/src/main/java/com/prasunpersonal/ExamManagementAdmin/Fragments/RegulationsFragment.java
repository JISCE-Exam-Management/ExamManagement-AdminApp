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
import com.prasunpersonal.ExamManagementAdmin.Models.Regulation;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentRegulationsBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegulationsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    public RegulationsFragment() {}

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentRegulationsBinding binding = FragmentRegulationsBinding.inflate(inflater, container, false);
        assert getParentFragment() != null;
        CourseStructureViewModel viewModel = new ViewModelProvider(getParentFragment()).get(CourseStructureViewModel.class);
        binding.allRegulations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.allRegulations.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        viewModel.setSelectedRegulation(null);
        viewModel.getSelectedStream().observe(getViewLifecycleOwner(), stream -> {
            if (stream != null) {
                binding.addRegulationArea.setVisibility(View.VISIBLE);
                binding.allRegulations.setAdapter(new CourseStructureItemAdapter<>(stream.getRegulations(), (regulation, position) -> viewModel.setSelectedRegulation(regulation)));
            } else {
                binding.addRegulationArea.setVisibility(View.GONE);
                viewModel.setSelectedRegulation(null);
            }
        });

        binding.addRegulation.setOnClickListener(v -> {
            if (binding.regulationName.getText().toString().trim().isEmpty()) {
                binding.regulationName.setError("Regulation name is required!");
                return;
            }
            binding.addRegulation.setVisibility(View.GONE);
            binding.addRegulationProgress.setVisibility(View.VISIBLE);
            binding.regulationName.setEnabled(false);
            Regulation regulation = new Regulation(binding.regulationName.getText().toString().trim());

            Map<String, String> params = new HashMap<>();
            params.put("degree", Objects.requireNonNull(viewModel.getSelectedDegree().getValue()).get_id());
            params.put("course", Objects.requireNonNull(viewModel.getSelectedCourse().getValue()).get_id());
            params.put("stream", Objects.requireNonNull(viewModel.getSelectedStream().getValue()).get_id());
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s%s",API.ADD_REGULATION, API.getQuery(params)), null, response -> {
                if (viewModel.getSelectedStream().getValue() != null) {
                    Degree degree = new Gson().fromJson(response.toString(), Degree.class);
                    Course course = degree.getCourses().get(degree.getCourses().indexOf(viewModel.getSelectedCourse().getValue()));
                    viewModel.getSelectedStream().getValue().getRegulations().clear();
                    viewModel.getSelectedStream().getValue().getRegulations().addAll(course.getStreams().get(course.getStreams().indexOf(viewModel.getSelectedStream().getValue())).getRegulations());
                    Objects.requireNonNull(binding.allRegulations.getAdapter()).notifyDataSetChanged();
                }
                binding.addRegulation.setVisibility(View.VISIBLE);
                binding.addRegulationProgress.setVisibility(View.GONE);
                binding.regulationName.setEnabled(true);
                binding.regulationName.getText().clear();
            }, error -> {
                Log.d(TAG, "onCreate: ", error);
                binding.addRegulation.setVisibility(View.VISIBLE);
                binding.addRegulationProgress.setVisibility(View.GONE);
                binding.regulationName.setEnabled(true);
            }) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(regulation).getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

        return binding.getRoot();
    }
}