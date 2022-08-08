package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementAdmin.Adapters.PagerAdapter;
import com.prasunpersonal.ExamManagementAdmin.Fragments.CandidatesFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.HallsFragment;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.ExamDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityExamDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExamDetailsActivity extends AppCompatActivity {
    ActivityExamDetailsBinding binding;
    private final String TAG = this.getClass().getSimpleName();
    private String examId;
    private ExamDetailsViewModel viewModel;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            updateUi();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExamDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.examDetailsToolbar);
        viewModel = new ViewModelProvider(this).get(ExamDetailsViewModel.class);
        examId = getIntent().getStringExtra("EXAM_ID");

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new HallsFragment());
        fragments.add(new CandidatesFragment());

        binding.hallViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
        binding.hallViewpager.setUserInputEnabled(false);

        viewModel.getSetSelectedHall().observe(this, hall -> {
            if (hall != null) {
                binding.hallViewpager.setCurrentItem(1);
            }
        });

        binding.examDetailsToolbar.setNavigationOnClickListener(v -> {
            if (binding.hallViewpager.getCurrentItem() == 0) {
                finish();
            } else {
                binding.hallViewpager.setCurrentItem(0);
            }
        });

        binding.examDetailsRefresh.setOnRefreshListener(this::updateUi);
        updateUi();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_details_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addNewHall) {
            launcher.launch(new Intent(this, ManageHallActivity.class).putExtra("EXAM_ID", examId));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.hallViewpager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            binding.hallViewpager.setCurrentItem(0);
        }
    }

    private void updateUi() {
        binding.examDetailsRefresh.setRefreshing(true);
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?exam=%s", API.GET_EXAM_BY_ID, examId), null, response -> {
            Exam exam = new Gson().fromJson(response.toString(), Exam.class);
            viewModel.setSetSelectedExam(exam);
            binding.hallViewpager.setCurrentItem(0);
            binding.examCategory.setText(String.format("%s / %s / %s / %s / %s / %s", exam.getDegree(), exam.getCourse(), exam.getStream(), exam.getRegulation(), exam.getSemester(), exam.getPaper().getCode()));
            binding.examItemName.setText(exam.getName());
            binding.examItemDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date(exam.getExamStartingTime())));
            binding.examItemTime.setText(String.format("%s - %s", new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date(exam.getExamStartingTime())), new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date(exam.getExamEndingTime()))));
            binding.examItemPaper.setText(exam.getPaper().toString());
            binding.examDetailsRefresh.setRefreshing(false);
        }, error -> {
            Log.d(TAG, "onCreate: ", error);
            binding.examDetailsRefresh.setRefreshing(false);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}