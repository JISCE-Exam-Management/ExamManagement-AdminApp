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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    private static ActivityBulkDataBinding binding;
    private static int category;
    private static List<Student> students;
    private static List<Exam> exams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBulkDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.bulkActivityToolbar);

        category = getIntent().getIntExtra("CATEGORY", CATEGORY_EXAM);

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
            new CSVFetcher().execute(getContentResolver().openInputStream(getIntent().getParcelableExtra("URI")));
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
        if (category == CATEGORY_STUDENT) {
            Map<String, List<Student>> body = new HashMap<>();
            body.put("students", students);
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.UPSERT_STUDENTS, null, studentsResponse -> {
                setResult(RESULT_OK);
                finish();
            }, error -> {
                Log.d(TAG, "uploadData: " + API.parseVolleyError(error));
                Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                binding.bulkUploadProgress.setVisibility(View.GONE);
            }) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        } else {
            Map<String, List<Exam>> body = new HashMap<>();
            body.put("exams", exams);
            QUEUE.add(new JsonArrayRequest(Request.Method.POST, API.INSERT_EXAMS, null, studentsResponse -> {
                setResult(RESULT_OK);
                finish();
            }, error -> {
                Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                binding.bulkUploadProgress.setVisibility(View.GONE);
            }) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
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
                }).setNeutralButton("Cancel", (dialog1, which) -> dialog1.dismiss()).create();

        dialog.setOnShowListener(dialog12 -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(R.color.success_color));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getColor(R.color.error_color));
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(getColor(android.R.color.darker_gray));
        });

        dialog.show();
    }

    private static long getTime(String date, String time) {
        try {
            return Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.getDefault()).parse(String.format("%s %s", date, time))).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static class CSVFetcher extends AsyncTask<InputStream, Integer, Void> {

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
                        try {
                            exams.add(new Exam(
                                    row.getCell(0).getStringCellValue(),
                                    new Gson().fromJson(row.getCell(1).getStringCellValue(), Paper.class),
                                    row.getCell(2).getStringCellValue(),
                                    row.getCell(3).getStringCellValue(),
                                    row.getCell(4).getStringCellValue(),
                                    row.getCell(5).getStringCellValue(),
                                    row.getCell(6).getStringCellValue(),
                                    getTime(row.getCell(7).getStringCellValue(), row.getCell(8).getStringCellValue()),
                                    getTime(row.getCell(7).getStringCellValue(), row.getCell(9).getStringCellValue()),
                                    getTime(row.getCell(7).getStringCellValue(), row.getCell(10).getStringCellValue()),
                                    getTime(row.getCell(7).getStringCellValue(), row.getCell(11).getStringCellValue())
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        publishProgress(i * 100 / sheet.getPhysicalNumberOfRows());
                    }
                } else {
                    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                        XSSFRow row = sheet.getRow(i);
                        try {
                            students.add(new Student(
                                    row.getCell(0).getStringCellValue(),
                                    row.getCell(1).getStringCellValue(),
                                    row.getCell(2).getStringCellValue(),
                                    row.getCell(3).getStringCellValue(),
                                    Long.parseLong(row.getCell(4).getStringCellValue()),
                                    Long.parseLong(row.getCell(5).getStringCellValue()),
                                    Integer.parseInt(row.getCell(6).getStringCellValue()),
                                    Boolean.parseBoolean(row.getCell(7).getStringCellValue()),
                                    row.getCell(8).getStringCellValue(),
                                    row.getCell(9).getStringCellValue(),
                                    row.getCell(10).getStringCellValue(),
                                    row.getCell(11).getStringCellValue(),
                                    row.getCell(12).getStringCellValue(),
                                    new Gson().fromJson(row.getCell(13).getStringCellValue(), new TypeToken<List<Paper>>() {}.getType()),
                                    new Gson().fromJson(row.getCell(14).getStringCellValue(), new TypeToken<List<Paper>>() {}.getType())
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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