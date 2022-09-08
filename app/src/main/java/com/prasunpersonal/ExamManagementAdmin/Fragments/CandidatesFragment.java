package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static com.prasunpersonal.ExamManagementAdmin.App.ME;
import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Adapters.AttendanceAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.ExamDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentCandidatesBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CandidatesFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentCandidatesBinding binding;
    private CountDownTimer timer;

    public CandidatesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCandidatesBinding.inflate(inflater, container, false);
        ExamDetailsViewModel viewModel = new ViewModelProvider(requireActivity()).get(ExamDetailsViewModel.class);

        binding.allCandidates.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.allCandidates.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        viewModel.getSetSelectedHall().observe(getViewLifecycleOwner(), hall -> {
            binding.allCandidates.setAdapter(null);
            if (hall != null) {
                QUEUE.add(new JsonArrayRequest(Request.Method.POST, API.STUDENTS_IN, null, response -> {
                    ArrayList<Student> students = new Gson().fromJson(response.toString(), new TypeToken<List<Student>>() {}.getType());
                    Exam exam = viewModel.getSetSelectedExam().getValue();
                    assert exam != null;
                    long currentTime = System.currentTimeMillis();
                    binding.allCandidates.setAdapter(new AttendanceAdapter(hall, students, (currentTime >= exam.getExamStartingTime() && currentTime <= exam.getExamEndingTime()), (student, present, position) -> hall.getCandidates().put(student.get_id(), present)));
                    if (currentTime >= exam.getExamStartingTime() && currentTime <= exam.getExamEndingTime()) {
                        binding.updateAttendanceArea.setVisibility(View.VISIBLE);
                        timer = new CountDownTimer(exam.getExamEndingTime() - System.currentTimeMillis(), 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                try {
                                    binding.attendanceTimer.setText(convertMillisToTime(millisUntilFinished));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFinish() {
                                try {
                                    Toast.makeText(getContext(), "Attendance submission time up", Toast.LENGTH_SHORT).show();
                                    binding.updateAttendanceArea.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        binding.updateAttendanceArea.setVisibility(View.GONE);
                    }

                    binding.submitAttendance.setOnClickListener(v -> {
                        if (hall.getCandidates().containsValue(null)) {
                            Toast.makeText(requireContext(), "Please select attendance for every candidates!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        binding.submitAttendance.setEnabled(false);
                        binding.addSemesterProgress.setVisibility(View.VISIBLE);

                        hall.setUpdatedBy(ME.get_id());
                        hall.setUpdatedTime(System.currentTimeMillis());

                        QUEUE.add(new JsonObjectRequest(Request.Method.PATCH, String.format("%s?exam=%s", API.UPDATE_HALL, exam.get_id()), null, newHall -> {
                            if (viewModel.getSetSelectedHall().getValue() != null)
                                viewModel.getSetSelectedHall().getValue().setCandidates(hall.getCandidates());
                            binding.submitAttendance.setEnabled(true);
                            binding.addSemesterProgress.setVisibility(View.GONE);
                        }, error -> {
                            Log.d(TAG, "onCreateView: ", error);
                            binding.submitAttendance.setEnabled(true);
                            binding.addSemesterProgress.setVisibility(View.GONE);
                        }) {
                            @Override
                            public byte[] getBody() {
                                return new Gson().toJson(hall).getBytes(StandardCharsets.UTF_8);
                            }
                        });
                    });
                }, error -> {
                    Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCreate: ", error);
                }) {
                    @Override
                    public byte[] getBody() {
                        Map<String, List<String>> body = new HashMap<>();
                        body.put("students", new ArrayList<>(Objects.requireNonNull(viewModel.getSetSelectedHall().getValue()).getCandidates().keySet()));
                        return new JSONObject(body).toString().getBytes(StandardCharsets.UTF_8);
                    }
                }).setRetryPolicy(new DefaultRetryPolicy());
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(R.id.examDetailsSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search candidates by name or roll no");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (binding.allCandidates.getAdapter() != null)
                    ((AttendanceAdapter) binding.allCandidates.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }

    private String convertMillisToTime(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}