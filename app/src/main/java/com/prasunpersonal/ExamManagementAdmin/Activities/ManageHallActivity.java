package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Adapters.SelectionAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.Hall;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityManageHallBinding;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageHallActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private ActivityManageHallBinding binding;
    private Map<String, Boolean> candidates;
    private String examId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageHallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.studentSelectionToolbar);

        examId = getIntent().getStringExtra("EXAM_ID");

        candidates = new HashMap<>();

        binding.selectableStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.selectableStudents.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        QUEUE.add(new JsonArrayRequest(Request.Method.GET, String.format("%s?exam=%s", API.EXAM_CANDIDATES, examId), null, studentsResponse -> {
            ArrayList<Student> students = new Gson().fromJson(studentsResponse.toString(), new TypeToken<List<Student>>() {}.getType());
            binding.selectableStudents.setAdapter(new SelectionAdapter(students, candidates, (student, selected, position) -> {
                if (selected) {
                    candidates.put(student.get_id(), null);
                } else {
                    candidates.remove(student.get_id());
                }
                binding.candidatesCount.setText(String.valueOf(candidates.size()));
            }));
        }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show())).setRetryPolicy(new DefaultRetryPolicy());

        binding.studentSelectionToolbar.setNavigationOnClickListener(v -> {
            if (candidates.isEmpty() && binding.hallName.getText().toString().isEmpty()) {
                setResult(RESULT_CANCELED);
                finish();
            } else {
                showDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (candidates.isEmpty() && binding.hallName.getText().toString().isEmpty()) {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        } else {
            showDialog();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.done) {
            if (binding.hallName.getText().toString().trim().isEmpty()) {
                binding.hallName.setError("Hall name is required!");
            } else if (candidates.isEmpty()) {
                Toast.makeText(this, "Please select some candidates!", Toast.LENGTH_LONG).show();
            } else {
                saveHall();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveHall() {
        QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?exam=%s", API.ADD_HALL, examId), null, studentsResponse -> {
            setResult(RESULT_OK);
            finish();
        }, error -> {
            Log.d(TAG, "onCreate: ", error);
        }) {
            @Override
            public byte[] getBody() {
                return new GsonBuilder().serializeNulls().create().toJson(new Hall(binding.hallName.getText().toString().trim(), candidates)).getBytes(StandardCharsets.UTF_8);
            }
        }).setRetryPolicy(new DefaultRetryPolicy());
    }

    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_delete)
                .setTitle("Discard Changes?")
                .setMessage("New hall details is not saved! This operation will discard all the changes!")
                .setPositiveButton("Save", (dialog1, which) -> {
                    saveHall();
                    dialog1.dismiss();
                }).setNegativeButton("Discard", (dialog1, which) -> {
                    dialog1.dismiss();
                    setResult(RESULT_CANCELED);
                    finish();
                }).setNeutralButton("Cancel", (dialog1, which) -> {
                    dialog1.dismiss();
                }).create();
        dialog.setOnShowListener(dialog12 -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(R.color.success_color));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getColor(R.color.error_color));
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(getColor(android.R.color.darker_gray));
        });
        dialog.show();
    }
}