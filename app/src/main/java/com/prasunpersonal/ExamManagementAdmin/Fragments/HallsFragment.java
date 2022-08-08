package com.prasunpersonal.ExamManagementAdmin.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prasunpersonal.ExamManagementAdmin.Adapters.HallAdapter;
import com.prasunpersonal.ExamManagementAdmin.Adapters.StudentAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.ExamDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentHallsBinding;

public class HallsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentHallsBinding binding;

    public HallsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHallsBinding.inflate(inflater, container, false);
        ExamDetailsViewModel viewModel = new ViewModelProvider(requireActivity()).get(ExamDetailsViewModel.class);
        binding.allHals.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel.setSetSelectedHall(null);
        viewModel.getSetSelectedExam().observe(getViewLifecycleOwner(), exam -> {
            if (exam != null) {
                binding.allHals.setAdapter(new HallAdapter(exam.getHalls(), (hall, position) -> {
                    viewModel.setSetSelectedHall(hall);
                }));
            } else {
                viewModel.setSetSelectedHall(null);
            }
        });
        viewModel.getSetSelectedHall().observe(getViewLifecycleOwner(), hall -> {
            if (binding.allHals.getAdapter() != null) binding.allHals.getAdapter().notifyDataSetChanged();
        });
        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(R.id.examDetailsSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search halls by name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (binding.allHals.getAdapter() != null)
                    ((HallAdapter) binding.allHals.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }
}