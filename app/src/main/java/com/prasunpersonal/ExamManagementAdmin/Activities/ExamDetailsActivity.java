package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.prasunpersonal.ExamManagementAdmin.Adapters.PagerAdapter;
import com.prasunpersonal.ExamManagementAdmin.Fragments.CandidatesFragment;
import com.prasunpersonal.ExamManagementAdmin.Fragments.HallsFragment;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Helpers.ExamDetailsViewModel;
import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
import com.prasunpersonal.ExamManagementAdmin.Models.Hall;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityExamDetailsBinding;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExamDetailsActivity extends AppCompatActivity {
    ActivityExamDetailsBinding binding;
    private final String TAG = this.getClass().getSimpleName();
    private String examId;
    private ExamDetailsViewModel viewModel;
    private Exam exam;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            updateUi();
        }
    });

    private final ActivityResultLauncher<Intent> permissionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (result.getResultCode() == RESULT_OK) {
                if (Environment.isExternalStorageManager()) {
                    try {
                        downloadAttendance();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
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
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.exam_details_menu, menu);
        menu.findItem(R.id.addNewHall).setVisible(exam != null && System.currentTimeMillis() < exam.getExamStartingTime());
        menu.findItem(R.id.downloadAttendance).setVisible(exam != null && System.currentTimeMillis() > exam.getExamEndingTime());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addNewHall) {
            launcher.launch(new Intent(this, ManageHallActivity.class).putExtra("EXAM_ID", examId));
        } else if (item.getItemId() == R.id.downloadAttendance) {
            try {
                checkAndRequestPermission();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    private void updateUi() {
        binding.examDetailsRefresh.setRefreshing(true);
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?exam=%s", API.EXAM_BY_ID, examId), null, response -> {
            exam = new Gson().fromJson(response.toString(), Exam.class);
            invalidateOptionsMenu();
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

    private void downloadAttendance() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        for (Hall hall : exam.getHalls()) {
            XSSFSheet sheet = workbook.createSheet(hall.getName());
            XSSFRow row;
            int n = 0;
            row = sheet.createRow(n++);
            row.createCell(0).setCellValue("Roll Number");
            row.createCell(1).setCellValue("Attendance");
            for (Map.Entry<String, Boolean> entry : hall.getCandidates().entrySet()) {
                row = sheet.createRow(n++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(String.valueOf(entry.getValue()));
            }
        }
        File file = new File(String.format("%s/%s/", Environment.getExternalStorageDirectory(), getString(R.string.app_name)));
        if (file.mkdirs()) Log.d(TAG, "downloadAttendance: Directory Created");;
        workbook.write(new FileOutputStream(new File(file, String.format("%s.xlsx", exam.getName().replace(" ", "-")))));
        workbook.close();
        Toast.makeText(this, "Downloaded Successfully", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_folder);
        builder.setTitle("Storage Permission");
        builder.setMessage("This app needs all files access permission. You can enable it in app settings.");
        builder.setCancelable(false);
        builder.setPositiveButton("Settings", (dialog, which) -> {
            dialog.cancel();
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                permissionLauncher.launch(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                permissionLauncher.launch(intent);
            }
        });
        builder.setNegativeButton("Exit", (dialog, which) -> {
            dialog.cancel();
            finish();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.light_gray)));
        alertDialog.show();
    }

    private void checkAndRequestPermission() throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                downloadAttendance();
            } else {
                showSettingsDialog();
            }
        } else {
            Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    try {
                        downloadAttendance();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).onSameThread().check();
        }
    }
}