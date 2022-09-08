package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Adapters.PagerAdapter;
import com.prasunpersonal.ExamManagementAdmin.Fragments.CoursesFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.DegreesFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.PapersFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.RegulationsFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.SemestersFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.StreamsFragment;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.CourseStructureViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityCourseStructureBinding;

import java.util.ArrayList;
import java.util.List;

public class CourseStructureActivity extends AppCompatActivity {
    ActivityCourseStructureBinding binding;
    private CourseStructureViewModel viewModel;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseStructureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.courseStructureToolbar);
        binding.courseStructureToolbar.setTitle("Degrees");
        binding.courseStructureToolbar.setSubtitle(">>");

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new DegreesFragment());
        fragments.add(new CoursesFragment());
        fragments.add(new StreamsFragment());
        fragments.add(new RegulationsFragment());
        fragments.add(new SemestersFragment());
        fragments.add(new PapersFragment());

        viewModel = new ViewModelProvider(this).get(CourseStructureViewModel.class);
        updateUi();
        binding.courseStructureRefresher.setOnRefreshListener(this::updateUi);

        binding.courseStructureToolbar.setNavigationOnClickListener(v -> {
            if (binding.coursesStructureViewpager.getCurrentItem() == 0) {
                finish();
            } else {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem() - 1);
                binding.courseStructureToolbar.setSubtitle(binding.courseStructureToolbar.getSubtitle().toString().substring(0, Math.max(0, binding.courseStructureToolbar.getSubtitle().toString().lastIndexOf(" / "))));
            }
        });

        viewModel.getSelectedDegree().observe(this, degree -> {
            if (degree != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructureToolbar.setSubtitle(String.format("%s / %s", binding.courseStructureToolbar.getSubtitle(), degree.getDegreeName()));
            }
        });
        viewModel.getSelectedCourse().observe(this, course -> {
            if (course != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructureToolbar.setSubtitle(String.format("%s / %s", binding.courseStructureToolbar.getSubtitle(), course.getCourseName()));
            }
        });
        viewModel.getSelectedStream().observe(this, stream -> {
            if (stream != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructureToolbar.setSubtitle(String.format("%s / %s", binding.courseStructureToolbar.getSubtitle(), stream.getStreamName()));
            }
        });
        viewModel.getSelectedRegulation().observe(this, regulation -> {
            if (regulation != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructureToolbar.setSubtitle(String.format("%s / %s", binding.courseStructureToolbar.getSubtitle(), regulation.getRegulationName()));
            }
        });
        viewModel.getSelectedSemester().observe(this, semester -> {
            if (semester != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructureToolbar.setSubtitle(String.format("%s / %s", binding.courseStructureToolbar.getSubtitle(), semester.getSemesterName()));
            }
        });

        binding.coursesStructureViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
        binding.coursesStructureViewpager.setUserInputEnabled(false);
        binding.coursesStructureViewpager.setOffscreenPageLimit(fragments.size());
    }

    void updateUi() {
        binding.courseStructureRefresher.setRefreshing(true);
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, API.ALL_DEGREES, null, response -> {
            ArrayList<Degree> degrees = new Gson().fromJson(response.toString(), new TypeToken<List<Degree>>(){}.getType());
            binding.coursesStructureViewpager.setCurrentItem(0);
            viewModel.setAllDegree(degrees);
            binding.courseStructureRefresher.setRefreshing(false);
        }, error -> {
            Log.d(TAG, "", error);
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            binding.courseStructureRefresher.setRefreshing(false);
        }));
    }

    @Override
    public void onBackPressed() {
        if (binding.coursesStructureViewpager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()-1);
            binding.courseStructureToolbar.setSubtitle(binding.courseStructureToolbar.getSubtitle().toString().substring(0, Math.max(0, binding.courseStructureToolbar.getSubtitle().toString().lastIndexOf(" / "))));
        }
    }
}