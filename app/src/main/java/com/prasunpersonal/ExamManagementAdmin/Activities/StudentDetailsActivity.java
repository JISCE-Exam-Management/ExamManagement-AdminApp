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
import com.prasunpersonal.ExamManagementAdmin.Fragments.BacklogPapersFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.RegularPapersFragment;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.StudentDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityStudentDetailsBinding;

import java.util.ArrayList;

public class StudentDetailsActivity extends AppCompatActivity {
    ActivityStudentDetailsBinding binding;
    private final String TAG = this.getClass().getSimpleName();
    private String studentId;
    private StudentDetailsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.studentDetailsToolbar);
        binding.studentDetailsToolbar.setNavigationOnClickListener(v -> finish());
        studentId = getIntent().getStringExtra("STUDENT_ID");
        viewModel = new ViewModelProvider(this).get(StudentDetailsViewModel.class);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new RegularPapersFragment());
        fragments.add(new BacklogPapersFragment());

        binding.papersTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.papersViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.papersViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.papersTab.selectTab(binding.papersTab.getTabAt(position));
            }
        });

        binding.papersViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
        binding.papersViewpager.setOffscreenPageLimit(fragments.size());

        binding.studentDetailsRefresh.setOnRefreshListener(this::updateUi);
        updateUi();
    }

    private void updateUi() {
        binding.studentDetailsRefresh.setRefreshing(true);
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?student=%s", API.GET_STUDENT_BY_ID, studentId), null, response -> {
            Student student = new Gson().fromJson(response.toString(), Student.class);
            viewModel.setSetSelectedStudent(student);
            binding.studentCategory.setText(String.format("%s / %s / %s / %s / %s", student.getDegree(), student.getCourse(), student.getStream(), student.getRegulation(), student.getSemester()));
            binding.studentName.setText(student.getName());
            binding.studentCollegeId.setText(student.getCollegeId());
            binding.studentReg.setText(String.valueOf(student.getUnivReg()));
            binding.studentRoll.setText(String.valueOf(student.getUnivRoll()));
            binding.lateralStatus.setText(student.isLateral() ? "Lateral" : "Non Lateral");
            binding.studentDetailsRefresh.setRefreshing(false);
        }, error -> {
            Log.d(TAG, "onCreate: ", error);
            binding.studentDetailsRefresh.setRefreshing(false);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}