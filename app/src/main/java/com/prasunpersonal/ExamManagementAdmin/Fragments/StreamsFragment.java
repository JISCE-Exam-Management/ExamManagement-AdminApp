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
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.Models.Stream;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentStreamsBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StreamsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    public StreamsFragment() {}

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentStreamsBinding binding = FragmentStreamsBinding.inflate(inflater, container, false);
        assert getParentFragment() != null;
        CourseStructureViewModel viewModel = new ViewModelProvider(getParentFragment()).get(CourseStructureViewModel.class);
        binding.allStreams.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.allStreams.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        viewModel.setSelectedStream(null);
        viewModel.getSelectedCourse().observe(getViewLifecycleOwner(), course -> {
            if (course != null) {
                binding.addStreamArea.setVisibility(View.VISIBLE);
                binding.allStreams.setAdapter(new CourseStructureItemAdapter<>(course.getStreams(), (stream, position) -> viewModel.setSelectedStream(stream)));
            } else {
                binding.addStreamArea.setVisibility(View.GONE);
                viewModel.setSelectedStream(null);
            }
        });
        binding.addStream.setOnClickListener(v -> {
            if (binding.streamName.getText().toString().trim().isEmpty()) {
                binding.streamName.setError("Stream name is required!");
                return;
            }
            binding.addStream.setVisibility(View.GONE);
            binding.addStreamProgress.setVisibility(View.VISIBLE);
            binding.streamName.setEnabled(false);
            Stream stream = new Stream(binding.streamName.getText().toString().trim());

            Map<String, String> params = new HashMap<>();
            params.put("degree", Objects.requireNonNull(viewModel.getSelectedDegree().getValue()).get_id());
            params.put("course", Objects.requireNonNull(viewModel.getSelectedCourse().getValue()).get_id());
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s%s",API.ADD_STREAM, API.getQuery(params)), null, response -> {
                if (viewModel.getSelectedCourse().getValue() != null) {
                    Degree degree = new Gson().fromJson(response.toString(), Degree.class);
                    viewModel.getSelectedCourse().getValue().getStreams().clear();
                    viewModel.getSelectedCourse().getValue().getStreams().addAll(degree.getCourses().get(degree.getCourses().indexOf(viewModel.getSelectedCourse().getValue())).getStreams());
                    Objects.requireNonNull(binding.allStreams.getAdapter()).notifyDataSetChanged();
                }
                binding.addStream.setVisibility(View.VISIBLE);
                binding.addStreamProgress.setVisibility(View.GONE);
                binding.streamName.setEnabled(true);
                binding.streamName.getText().clear();
            }, error -> {
                Log.d(TAG, "onCreate: ", error);
                binding.addStream.setVisibility(View.VISIBLE);
                binding.addStreamProgress.setVisibility(View.GONE);
                binding.streamName.setEnabled(true);
            }) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(stream).getBytes(StandardCharsets.UTF_8);
                }

                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("degree", Objects.requireNonNull(viewModel.getSelectedDegree().getValue()).get_id());
                    params.put("course", Objects.requireNonNull(viewModel.getSelectedCourse().getValue()).get_id());
                    return params;
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

        return binding.getRoot();
    }
}