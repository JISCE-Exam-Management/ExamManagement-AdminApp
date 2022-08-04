package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Activities.ExamDetailsActivity;
import com.prasunpersonal.ExamManagementAdmin.Adapters.ExamAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.Course;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
import com.prasunpersonal.ExamManagementAdmin.Models.Paper;
import com.prasunpersonal.ExamManagementAdmin.Models.Regulation;
import com.prasunpersonal.ExamManagementAdmin.Models.Semester;
import com.prasunpersonal.ExamManagementAdmin.Models.Stream;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.ExamRegisterBinding;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentExamsBinding;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExamsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentExamsBinding binding;

    public ExamsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExamsBinding.inflate(inflater, container, false);
        updateUi();
        binding.examRefresher.setOnRefreshListener(this::updateUi);
        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.setGroupVisible(R.id.examsMenuGroup, true);
        menu.setGroupVisible(R.id.studentsMenuGroup, false);
        menu.setGroupVisible(R.id.coursesMenuGroup, false);
        MenuItem searchItem = menu.findItem(R.id.searchExam);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.findItem(R.id.addNewExam).setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.findItem(R.id.addNewExam).setVisible(true);
                return true;
            }
        });
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search exam by name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (binding.allExams.getAdapter() != null)
                    ((ExamAdapter) binding.allExams.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addSingleExam) {
            addNewExam();
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayAdapter<String> getArrayAdapter(ArrayList<String> items) {
        return new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_activated_1, items) {
            @Override
            public boolean isEnabled(int position) {
                return position > 0;
            }
        };
    }

    private void addNewExam() {
        ExamRegisterBinding examRegister = ExamRegisterBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(examRegister.getRoot()).setCancelable(false).create();
        dialog.getWindow().setBackgroundDrawable(ActivityCompat.getDrawable(requireContext(), R.drawable.dialog_bg));

        examRegister.examDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(Objects.requireNonNull(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).parse(examRegister.examDate.getText().toString())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                examRegister.examDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
        examRegister.examStartingTime.setOnClickListener(v -> {
            int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), m = Calendar.getInstance().get(Calendar.MINUTE);
            try {
                String str = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).parse(examRegister.examStartingTime.getText().toString())));
                String[] arr = str.split(":");
                h = Integer.parseInt(arr[0]);
                m = Integer.parseInt(arr[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                try {
                    examRegister.examStartingTime.setText(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(hourOfDay + ":" + minute))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, h, m, false).show();
        });
        examRegister.examEndingTime.setOnClickListener(v -> {
            int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), m = Calendar.getInstance().get(Calendar.MINUTE);
            try {
                String str = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).parse(examRegister.examEndingTime.getText().toString())));
                String[] arr = str.split(":");
                h = Integer.parseInt(arr[0]);
                m = Integer.parseInt(arr[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                try {
                    examRegister.examEndingTime.setText(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(hourOfDay + ":" + minute))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, h, m, false).show();
        });
        examRegister.attendanceStartingTime.setOnClickListener(v -> {
            int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), m = Calendar.getInstance().get(Calendar.MINUTE);
            try {
                String str = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).parse(examRegister.attendanceStartingTime.getText().toString())));
                String[] arr = str.split(":");
                h = Integer.parseInt(arr[0]);
                m = Integer.parseInt(arr[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                try {
                    examRegister.attendanceStartingTime.setText(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(hourOfDay + ":" + minute))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, h, m, false).show();
        });
        examRegister.attendanceEndingTime.setOnClickListener(v -> {
            int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), m = Calendar.getInstance().get(Calendar.MINUTE);
            try {
                String str = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).parse(examRegister.attendanceEndingTime.getText().toString())));
                String[] arr = str.split(":");
                h = Integer.parseInt(arr[0]);
                m = Integer.parseInt(arr[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                try {
                    examRegister.attendanceEndingTime.setText(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(hourOfDay + ":" + minute))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, h, m, false).show();
        });

        examRegister.btnNext.setOnClickListener(v -> {
            if (examRegister.examName.getText().toString().trim().isEmpty()) {
                examRegister.examName.setError("Exam name is required!");
                return;
            }
            if (examRegister.examDate.getText().toString().trim().isEmpty()) {
                examRegister.examDate.setError("Exam date is required!");
                return;
            }
            if (examRegister.examStartingTime.getText().toString().trim().isEmpty()) {
                examRegister.examStartingTime.setError("Starting time is required!");
                return;
            }
            if (examRegister.examEndingTime.getText().toString().trim().isEmpty()) {
                examRegister.examEndingTime.setError("Ending time is required!");
                return;
            }

            long est = 0, eet = 0, ast = 0, aet = 0;
            try {
                est = Objects.requireNonNull(new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).parse(String.format("%s %s", examRegister.examDate.getText(), examRegister.examStartingTime.getText()))).getTime();
                eet = Objects.requireNonNull(new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).parse(String.format("%s %s", examRegister.examDate.getText(), examRegister.examEndingTime.getText()))).getTime();
                ast = Objects.requireNonNull(new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).parse(String.format("%s %s", examRegister.examDate.getText(), examRegister.attendanceStartingTime.getText()))).getTime();
                aet = Objects.requireNonNull(new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).parse(String.format("%s %s", examRegister.examDate.getText(), examRegister.attendanceEndingTime.getText()))).getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (eet <= est) {
                Toast.makeText(requireContext(), "Exam Ending time must be grater than than Exam Starting time!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (aet <= ast || ast < est || aet > eet) {
                Toast.makeText(requireContext(), "Enter a valid attendance time!", Toast.LENGTH_SHORT).show();
                return;
            }

            examRegister.btnNext.setEnabled(false);
            examRegister.registerExamProgress.setVisibility(View.VISIBLE);

            long finalAst = ast; long finalAet = aet; long finalEst = est; long finalEet = eet;
            QUEUE.add(new JsonArrayRequest(Request.Method.GET, API.ALL_DEGREES, null, degreesArray -> {
                ArrayList<Degree> degrees = new Gson().fromJson(degreesArray.toString(), new TypeToken<List<Degree>>() {
                }.getType());
                ArrayList<String> degreeNames = new ArrayList<>();
                ArrayList<String> courseNames = new ArrayList<>();
                ArrayList<String> streamNames = new ArrayList<>();
                ArrayList<String> regulationNames = new ArrayList<>();
                ArrayList<String> semesterNames = new ArrayList<>();
                ArrayList<String> paperNames = new ArrayList<>();

                examRegister.btnNext.setVisibility(View.GONE);
                examRegister.examDetails.setVisibility(View.GONE);
                examRegister.btnSave.setVisibility(View.VISIBLE);
                examRegister.academicDetails.setVisibility(View.VISIBLE);
                examRegister.registerExamProgress.setVisibility(View.GONE);

                degreeNames.add("Select Degree");
                degreeNames.addAll(degrees.stream().map(Degree::getDegreeName).sorted(String::compareToIgnoreCase).collect(Collectors.toList()));

                examRegister.degrees.setAdapter(getArrayAdapter(degreeNames));
                examRegister.degrees.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        courseNames.clear();
                        courseNames.add("Select Course");
                        if (position > 0) {
                            courseNames.addAll(degrees.get(position - 1)
                                    .getCourses()
                                    .stream().map(Course::getCourseName).collect(Collectors.toList()));
                            examRegister.courses.setEnabled(true);
                        } else {
                            examRegister.courses.setEnabled(false);
                        }
                        Log.d(TAG, "addNewExam: "+courseNames);
                        Log.d(TAG, "addNewExam: "+degrees.get(0).getCourses().stream().map(Course::getCourseName).collect(Collectors.toList()));
                        ((ArrayAdapter<?>) examRegister.courses.getAdapter()).notifyDataSetChanged();
                        examRegister.courses.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                examRegister.courses.setAdapter(getArrayAdapter(courseNames));
                examRegister.courses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        streamNames.clear();
                        streamNames.add("Select Stream");
                        if (position > 0) {
                            streamNames.addAll(degrees.get(examRegister.degrees.getSelectedItemPosition() - 1)
                                    .getCourses().get(position - 1)
                                    .getStreams()
                                    .stream().map(Stream::getStreamName).collect(Collectors.toList()));
                            examRegister.streams.setEnabled(true);
                        } else {
                            examRegister.streams.setEnabled(false);
                        }
                        ((ArrayAdapter<?>) examRegister.streams.getAdapter()).notifyDataSetChanged();
                        examRegister.streams.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                examRegister.streams.setAdapter(getArrayAdapter(streamNames));
                examRegister.streams.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        regulationNames.clear();
                        regulationNames.add("Select Regulation");
                        if (position > 0) {
                            regulationNames.addAll(degrees.get(examRegister.degrees.getSelectedItemPosition() - 1)
                                    .getCourses().get(examRegister.courses.getSelectedItemPosition() - 1)
                                    .getStreams().get(position - 1)
                                    .getRegulations()
                                    .stream().map(Regulation::getRegulationName).sorted(String::compareToIgnoreCase).collect(Collectors.toList()));
                            examRegister.regulations.setEnabled(true);
                        } else {
                            examRegister.regulations.setEnabled(false);
                        }
                        ((ArrayAdapter<?>) examRegister.regulations.getAdapter()).notifyDataSetChanged();
                        examRegister.regulations.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                examRegister.regulations.setAdapter(getArrayAdapter(regulationNames));
                examRegister.regulations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        semesterNames.clear();
                        semesterNames.add("Select Semester");
                        if (position > 0) {
                            semesterNames.addAll(degrees.get(examRegister.degrees.getSelectedItemPosition() - 1)
                                    .getCourses().get(examRegister.courses.getSelectedItemPosition() - 1)
                                    .getStreams().get(examRegister.streams.getSelectedItemPosition() - 1)
                                    .getRegulations().get(position - 1)
                                    .getSemesters()
                                    .stream().map(Semester::getSemesterName).sorted(String::compareToIgnoreCase).collect(Collectors.toList()));
                            examRegister.semesters.setEnabled(true);
                        } else {
                            examRegister.semesters.setEnabled(false);
                        }
                        ((ArrayAdapter<?>) examRegister.semesters.getAdapter()).notifyDataSetChanged();
                        examRegister.semesters.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                examRegister.semesters.setAdapter(getArrayAdapter(semesterNames));
                examRegister.semesters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        paperNames.clear();
                        paperNames.add("Select Paper");
                        if (position > 0) {
                            paperNames.addAll(degrees.get(examRegister.degrees.getSelectedItemPosition() - 1)
                                    .getCourses().get(examRegister.courses.getSelectedItemPosition() - 1)
                                    .getStreams().get(examRegister.streams.getSelectedItemPosition() - 1)
                                    .getRegulations().get(examRegister.regulations.getSelectedItemPosition() - 1)
                                    .getSemesters().get(position - 1)
                                    .getPapers()
                                    .stream().map(Paper::toString).sorted(String::compareToIgnoreCase).collect(Collectors.toList()));
                            examRegister.papers.setEnabled(true);
                        } else {
                            examRegister.papers.setEnabled(false);
                        }
                        ((ArrayAdapter<?>) examRegister.papers.getAdapter()).notifyDataSetChanged();
                        examRegister.papers.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                examRegister.papers.setAdapter(getArrayAdapter(paperNames));
                examRegister.papers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        examRegister.btnSave.setEnabled(position > 0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                examRegister.btnSave.setOnClickListener(v1 -> {
                    examRegister.btnSave.setEnabled(false);
                    examRegister.registerExamProgress.setVisibility(View.VISIBLE);

                    Paper paper = degrees.get(examRegister.degrees.getSelectedItemPosition() - 1)
                            .getCourses().get(examRegister.courses.getSelectedItemPosition() - 1)
                            .getStreams().get(examRegister.streams.getSelectedItemPosition() - 1)
                            .getRegulations().get(examRegister.regulations.getSelectedItemPosition() - 1)
                            .getSemesters().get(examRegister.semesters.getSelectedItemPosition() - 1)
                            .getPapers().get(examRegister.papers.getSelectedItemPosition() - 1);

                    Exam exam = new Exam(
                            examRegister.examName.getText().toString().trim(),
                            paper,
                            examRegister.degrees.getAdapter().getItem(examRegister.degrees.getSelectedItemPosition()).toString(),
                            examRegister.courses.getAdapter().getItem(examRegister.courses.getSelectedItemPosition()).toString(),
                            examRegister.streams.getAdapter().getItem(examRegister.streams.getSelectedItemPosition()).toString(),
                            examRegister.regulations.getAdapter().getItem(examRegister.regulations.getSelectedItemPosition()).toString(),
                            examRegister.semesters.getAdapter().getItem(examRegister.semesters.getSelectedItemPosition()).toString(),
                            finalEst,
                            finalEet,
                            finalAst,
                            finalAet);

                    QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.ADD_EXAM, null, examResponse -> {
                        Exam newExam = new Gson().fromJson(examResponse.toString(), Exam.class);
                        startActivity(new Intent(requireContext(), ExamDetailsActivity.class).putExtra("EXAM_ID", newExam.get_id()));
                        updateUi();
                        dialog.dismiss();
                    }, error -> {
                        examRegister.btnSave.setEnabled(true);
                        examRegister.registerExamProgress.setVisibility(View.GONE);
                        Log.d(TAG, "onCreate: ", error);
                    }) {
                        @Override
                        public byte[] getBody() {
                            return new Gson().toJson(exam).getBytes(StandardCharsets.UTF_8);
                        }
                    });
                });
            }, error -> {
                examRegister.btnNext.setEnabled(true);
                Log.d(TAG, "onCreate: ", error);
            }));
        });

        examRegister.btnCancel.setOnClickListener(v1 -> dialog.dismiss());

        dialog.show();
    }

    private void updateUi() {
        binding.examRefresher.setRefreshing(true);
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, API.ALL_EXAMS, null, response -> {
            ArrayList<Exam> exams = new Gson().fromJson(response.toString(), new TypeToken<List<Exam>>() {
            }.getType());
            try {
                binding.allExams.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.allExams.setAdapter(new ExamAdapter(exams, (exam, position) -> startActivity(new Intent(requireContext(), ExamDetailsActivity.class).putExtra("EXAM_ID", exam.get_id()))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            binding.examRefresher.setRefreshing(false);
        }, error -> {
            Log.d(TAG, "onCreate: ", error);
            binding.examRefresher.setRefreshing(false);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}