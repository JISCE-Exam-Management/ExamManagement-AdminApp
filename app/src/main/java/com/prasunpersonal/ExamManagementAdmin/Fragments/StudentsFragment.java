package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static android.app.Activity.RESULT_OK;
import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Activities.BulkDataActivity;
import com.prasunpersonal.ExamManagementAdmin.Activities.StudentDetailsActivity;
import com.prasunpersonal.ExamManagementAdmin.Adapters.StudentAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentStudentsBinding;

import java.util.ArrayList;
import java.util.List;

public class StudentsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentStudentsBinding binding;

    private final ActivityResultLauncher<Intent> launcher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            updateUi();
        }
    });

    private final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            launcher2.launch(new Intent(requireContext(), BulkDataActivity.class).putExtra("CATEGORY", BulkDataActivity.CATEGORY_STUDENT).putExtra("URI", result));
        }
    });

    public StudentsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStudentsBinding.inflate(inflater, container, false);
        updateUi();
        binding.studentsRefresher.setOnRefreshListener(this::updateUi);
        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.homeAddNew).setTitle("Insert Students");
        MenuItem searchItem = menu.findItem(R.id.homeSearch);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.setGroupVisible(R.id.hidden, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.setGroupVisible(R.id.hidden, true);
                return true;
            }
        });
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search student by name or roll number");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (binding.allStudents.getAdapter() != null)
                    ((StudentAdapter) binding.allStudents.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.homeAddNew) {
            launcher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
        return super.onOptionsItemSelected(item);
    }

    void updateUi() {
        binding.studentsRefresher.setRefreshing(true);
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, API.ALL_STUDENTS, null, response -> {
            ArrayList<Student> students = new Gson().fromJson(response.toString(), new TypeToken<List<Student>>(){}.getType());
            try {
                binding.allStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.allStudents.setAdapter(new StudentAdapter(students, (student, position) -> startActivity(new Intent(requireContext(), StudentDetailsActivity.class).putExtra("STUDENT_ID", student.get_id()))));
                binding.allStudents.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
            } catch (Exception e) {
                e.printStackTrace();
            }
            binding.studentsRefresher.setRefreshing(false);
        }, error -> {
            binding.studentsRefresher.setRefreshing(false);
            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: ", error);
        }));
    }
}