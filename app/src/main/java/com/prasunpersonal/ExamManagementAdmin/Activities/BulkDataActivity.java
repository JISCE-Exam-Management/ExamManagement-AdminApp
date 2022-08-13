package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Adapters.ExamAdapter;
import com.prasunpersonal.ExamManagementAdmin.Adapters.StudentAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
import com.prasunpersonal.ExamManagementAdmin.Models.Paper;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityBulkDataBinding;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class BulkDataActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    public static final int CATEGORY_EXAM = 1;
    public static final int CATEGORY_STUDENT = 2;
    public static final int TYPE_UPDATE = 3;
    public static final int TYPE_INSERT = 4;

    private static ActivityBulkDataBinding binding;
    private static int category;
    private static int type;
    private static List<Student> students;
    private static List<Exam> exams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBulkDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.bulkActivityToolbar);

        category = getIntent().getIntExtra("CATEGORY", CATEGORY_EXAM);
        type = getIntent().getIntExtra("TYPE", TYPE_INSERT);

        students = new ArrayList<>();
        exams = new ArrayList<>();

        binding.bulkDatas.setLayoutManager(new LinearLayoutManager(this));
        binding.bulkDatas.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        if (category == CATEGORY_STUDENT) {
            binding.bulkDatas.setAdapter(new StudentAdapter(students, (student, position) -> {
            }));
        } else if (category == CATEGORY_EXAM) {
            binding.bulkDatas.setAdapter(new ExamAdapter(exams, (exam, position) -> {
            }));
        }

        binding.bulkActivityToolbar.setNavigationOnClickListener(v -> {
            if ((category == CATEGORY_EXAM && !exams.isEmpty()) || (category == CATEGORY_STUDENT && !students.isEmpty())) {
                showDialog();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        try {
            new CSVFetcher().execute((FileInputStream) getContentResolver().openInputStream(getIntent().getParcelableExtra("URI")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bulk_activity_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.uploadData) {
            uploadData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if ((category == CATEGORY_EXAM && !exams.isEmpty()) || (category == CATEGORY_STUDENT && !students.isEmpty())) {
            showDialog();
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    private void uploadData() {
        binding.bulkUploadProgress.setVisibility(View.VISIBLE);
        binding.bulkUploadProgress.setProgress(0);
        if (category == CATEGORY_STUDENT) {
            int n = 50;
            String url;
            if (type == TYPE_INSERT) url = API.ADD_MULTIPLE_STUDENTS;
            else url = "";
            binding.bulkUploadProgress.setMax(students.size());
            for (int i = 0; i < students.size(); i += n) {
                Map<String, List<Student>> body = new HashMap<>();
                body.put("students", students.subList(i, Math.min(i + n, students.size())));
                QUEUE.add(new JsonObjectRequest(Request.Method.POST, url, null, studentsResponse -> {
                    binding.bulkUploadProgress.setProgressCompat(Math.min(binding.bulkUploadProgress.getProgress() + n, binding.bulkUploadProgress.getProgress() + students.size()), true);
                    if (binding.bulkUploadProgress.getProgress() == binding.bulkUploadProgress.getMax()) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }, error -> {
                    Log.d(TAG, "uploadData: "+API.parseVolleyError(error));
                    binding.bulkUploadProgress.setProgressCompat(Math.min(binding.bulkUploadProgress.getProgress() + n, binding.bulkUploadProgress.getProgress() + students.size()), true);
                    if (binding.bulkUploadProgress.getProgress() == binding.bulkUploadProgress.getMax()) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                    }
                }).setRetryPolicy(new DefaultRetryPolicy());
            }
        } else {
            int n = 75;
            String url;
            if (type == TYPE_INSERT) url = API.ADD_MULTIPLE_EXAMS;
            else url = "";
            binding.bulkUploadProgress.setMax(exams.size());
            for (int i = 0; i < exams.size(); i += n) {
                Map<String, List<Exam>> body = new HashMap<>();
                body.put("exams", exams.subList(i, Math.min(i + n, exams.size())));
                QUEUE.add(new JsonObjectRequest(Request.Method.POST, url, null, studentsResponse -> {
                    binding.bulkUploadProgress.setProgressCompat(Math.min(binding.bulkUploadProgress.getProgress() + n, binding.bulkUploadProgress.getProgress() + exams.size()), true);
                    if (binding.bulkUploadProgress.getProgress() == binding.bulkUploadProgress.getMax()) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }, error -> {
                    Log.d(TAG, "uploadData: "+API.parseVolleyError(error));
                    binding.bulkUploadProgress.setProgressCompat(Math.min(binding.bulkUploadProgress.getProgress() + n, binding.bulkUploadProgress.getProgress() + exams.size()), true);
                    if (binding.bulkUploadProgress.getProgress() == binding.bulkUploadProgress.getMax()) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                    }
                }).setRetryPolicy(new DefaultRetryPolicy());
            }
        }
    }

    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_delete)
                .setTitle("Discard Changes?")
                .setMessage("Your changes are not saved! This operation will discard all the changes!")
                .setPositiveButton("Upload", (dialog1, which) -> {
                    uploadData();
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

    private static long getTime(String date, String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.getDefault()).parse(String.format("%s %s", date, time)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static class CSVFetcher extends AsyncTask<InputStream, Integer, Void> {
        private final String TAG = this.getClass().getSimpleName();

        public CSVFetcher() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            exams.clear();
            students.clear();
            binding.bulkUploadProgress.setVisibility(View.VISIBLE);
            binding.bulkUploadProgress.setMax(100);
            binding.bulkUploadProgress.setProgress(0);
        }

        @Override
        protected Void doInBackground(InputStream... inputStreams) {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(inputStreams[0]);
                XSSFSheet sheet = workbook.getSheetAt(0);
                if (category == CATEGORY_EXAM) {
                    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                        XSSFRow row = sheet.getRow(i);
                        if (row == null) continue;
                        exams.add(new Exam(
                                (row.getCell(0) == null) ? null : row.getCell(0).getStringCellValue(),
                                (row.getCell(1) == null) ? null : new Gson().fromJson(row.getCell(1).getStringCellValue(), Paper.class),
                                (row.getCell(2) == null) ? null : row.getCell(2).getStringCellValue(),
                                (row.getCell(3) == null) ? null : row.getCell(3).getStringCellValue(),
                                (row.getCell(4) == null) ? null : row.getCell(4).getStringCellValue(),
                                (row.getCell(5) == null) ? null : row.getCell(5).getStringCellValue(),
                                (row.getCell(6) == null) ? null : row.getCell(6).getStringCellValue(),
                                (row.getCell(7) == null || row.getCell(8) == null) ? 0 : getTime(row.getCell(7).getStringCellValue(), row.getCell(8).getStringCellValue()),
                                (row.getCell(7) == null || row.getCell(9) == null) ? 0 : getTime(row.getCell(7).getStringCellValue(), row.getCell(9).getStringCellValue()),
                                (row.getCell(7) == null || row.getCell(10) == null) ? 0 : getTime(row.getCell(7).getStringCellValue(), row.getCell(10).getStringCellValue()),
                                (row.getCell(7) == null || row.getCell(11) == null) ? 0 : getTime(row.getCell(7).getStringCellValue(), row.getCell(11).getStringCellValue())
                        ));
                        publishProgress(i * 100 / sheet.getPhysicalNumberOfRows());
                    }
                } else {
                    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                        XSSFRow row = sheet.getRow(i);
                        if (row == null) continue;
                        students.add(new Student(
                                (row.getCell(0) == null) ? null : row.getCell(0).getStringCellValue(),
                                (row.getCell(1) == null) ? null : row.getCell(1).getStringCellValue(),
                                (row.getCell(2) == null) ? null : row.getCell(2).getStringCellValue(),
                                (row.getCell(3) == null) ? null : row.getCell(3).getStringCellValue(),
                                (row.getCell(4) == null) ? 0 : Long.parseLong(row.getCell(4).getStringCellValue()),
                                (row.getCell(5) == null) ? 0 : Long.parseLong(row.getCell(5).getStringCellValue()),
                                (row.getCell(6) == null) ? 0 : Integer.parseInt(row.getCell(6).getStringCellValue()),
                                row.getCell(7) != null && Boolean.parseBoolean(row.getCell(7).getStringCellValue()),
                                (row.getCell(8) == null) ? null : row.getCell(8).getStringCellValue(),
                                (row.getCell(9) == null) ? null : row.getCell(9).getStringCellValue(),
                                (row.getCell(10) == null) ? null : row.getCell(10).getStringCellValue(),
                                (row.getCell(11) == null) ? null : row.getCell(11).getStringCellValue(),
                                (row.getCell(12) == null) ? null : row.getCell(12).getStringCellValue(),
                                (row.getCell(13) == null) ? new ArrayList<>() : new Gson().fromJson(row.getCell(13).getStringCellValue(), new TypeToken<List<Paper>>() {
                                }.getType()),
                                (row.getCell(14) == null) ? new ArrayList<>() : new Gson().fromJson(row.getCell(14).getStringCellValue(), new TypeToken<List<Paper>>() {
                                }.getType())
                        ));
                    }
                }
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            binding.bulkUploadProgress.setProgressCompat(values[0], true);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(Void unused) {
            binding.bulkUploadProgress.setVisibility(View.GONE);
            Objects.requireNonNull(binding.bulkDatas.getAdapter()).notifyDataSetChanged();
        }
    }
}