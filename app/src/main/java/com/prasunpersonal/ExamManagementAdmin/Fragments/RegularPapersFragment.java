package com.prasunpersonal.ExamManagementAdmin.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prasunpersonal.ExamManagementAdmin.Adapters.CourseStructureItemAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.StudentDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Paper;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentRegularPapersBinding;

public class RegularPapersFragment extends Fragment {

    public RegularPapersFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentRegularPapersBinding binding = FragmentRegularPapersBinding.inflate(inflater, container, false);
        StudentDetailsViewModel viewModel = new ViewModelProvider(requireActivity()).get(StudentDetailsViewModel.class);
        viewModel.getSetSelectedStudent().observe(getViewLifecycleOwner(), student -> {
            binding.allRegularPapers.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.allRegularPapers.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
            binding.allRegularPapers.setAdapter(new CourseStructureItemAdapter<>(student.getRegularPapers(), (item, position) -> {}));
        });
        return binding.getRoot();
    }
}