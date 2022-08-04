package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementAdmin.Adapters.PagerAdapter;
import com.prasunpersonal.ExamManagementAdmin.Fragments.BacklogStudentsFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.RegularStudentsFragment;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.ExamDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExamDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.examDetailsToolbar);
        viewModel = new ViewModelProvider(this).get(ExamDetailsViewModel.class);
        binding.examDetailsToolbar.setNavigationOnClickListener(v -> finish());
        examId = getIntent().getStringExtra("EXAM_ID");

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new RegularStudentsFragment());
        fragments.add(new BacklogStudentsFragment());

        binding.candidatesTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.candidatesViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.candidatesViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.candidatesTab.selectTab(binding.candidatesTab.getTabAt(position));
            }
        });

        binding.candidatesViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
        binding.candidatesViewpager.setOffscreenPageLimit(fragments.size());

        binding.examDetailsRefresh.setOnRefreshListener(this::updateUi);
        updateUi();
    }

    private void updateUi() {
        binding.examDetailsRefresh.setRefreshing(true);
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s/%s", API.GET_EXAMS_BY_ID, examId), null, response -> {
            Exam exam = new Gson().fromJson(response.toString(), Exam.class);
            viewModel.setSetSelectedExam(exam);
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