package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Adapters.PagerAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.CourseStructureViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentCoursesStructureBinding;

import java.util.ArrayList;
import java.util.List;

public class CoursesStructureFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentCoursesStructureBinding binding;
    private CourseStructureViewModel viewModel;

    public CoursesStructureFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesStructureBinding.inflate(inflater, container, false);

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

        binding.coursesStructureViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position > 0) binding.courseStructureTop.setVisibility(View.VISIBLE);
                else binding.courseStructureTop.setVisibility(View.GONE);
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()-1);
            binding.courseStructurePath.setText(binding.courseStructurePath.getText().toString().substring(0, Math.max(0, binding.courseStructurePath.getText().toString().lastIndexOf(" / "))));
        });

        viewModel.getSelectedDegree().observe(getViewLifecycleOwner(), degree -> {
            if (degree != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructurePath.setText(degree.getDegreeName());
            }
        });
        viewModel.getSelectedCourse().observe(getViewLifecycleOwner(), course -> {
            if (course != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructurePath.setText(String.format("%s / %s", binding.courseStructurePath.getText().toString(), course.getCourseName()));
            }
        });
        viewModel.getSelectedStream().observe(getViewLifecycleOwner(), stream -> {
            if (stream != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructurePath.setText(String.format("%s / %s", binding.courseStructurePath.getText().toString(), stream.getStreamName()));
            }
        });
        viewModel.getSelectedRegulation().observe(getViewLifecycleOwner(), regulation -> {
            if (regulation != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructurePath.setText(String.format("%s / %s", binding.courseStructurePath.getText().toString(), regulation.getRegulationName()));
            }
        });
        viewModel.getSelectedSemester().observe(getViewLifecycleOwner(), semester -> {
            if (semester != null) {
                binding.coursesStructureViewpager.setCurrentItem(binding.coursesStructureViewpager.getCurrentItem()+1);
                binding.courseStructurePath.setText(String.format("%s / %s", binding.courseStructurePath.getText().toString(), semester.getSemesterName()));
            }
        });

        binding.coursesStructureViewpager.setAdapter(new PagerAdapter(getChildFragmentManager(), getLifecycle(), fragments));
        binding.coursesStructureViewpager.setUserInputEnabled(false);
        binding.coursesStructureViewpager.setOffscreenPageLimit(fragments.size());

        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.setGroupVisible(R.id.examMenuGroup, false);
        menu.setGroupVisible(R.id.studentsMenuGroup, false);
        menu.setGroupVisible(R.id.coursesMenuGroup, true);
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
            binding.courseStructureRefresher.setRefreshing(false);
        }));
    }
}