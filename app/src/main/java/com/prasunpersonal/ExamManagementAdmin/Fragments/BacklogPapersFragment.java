package com.prasunpersonal.ExamManagementAdmin.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prasunpersonal.ExamManagementAdmin.Adapters.CourseStructureItemAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.StudentDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentBacklogPapersBinding;

public class BacklogPapersFragment extends Fragment {

    public BacklogPapersFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentBacklogPapersBinding binding = FragmentBacklogPapersBinding.inflate(inflater, container, false);
        StudentDetailsViewModel viewModel = new ViewModelProvider(requireActivity()).get(StudentDetailsViewModel.class);
        viewModel.getSetSelectedStudent().observe(getViewLifecycleOwner(), student -> {
            binding.allBacklogPapers.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.allBacklogPapers.setAdapter(new CourseStructureItemAdapter<>(student.getBacklogPapers(), (item, position) -> {}));
        });
        return binding.getRoot();
    }
}