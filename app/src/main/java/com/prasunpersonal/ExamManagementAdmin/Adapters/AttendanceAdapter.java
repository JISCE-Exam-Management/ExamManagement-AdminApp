package com.prasunpersonal.ExamManagementAdmin.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prasunpersonal.ExamManagementAdmin.Models.Hall;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.StudentAttendanceBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.StudentViewHolder> implements Filterable {
    private final List<Student> students;
    private final List<Student> allStudents;
    private Hall hall;
    private final setOnAttendanceGivenListener listener;

    public AttendanceAdapter(Hall hall, List<Student> students,  setOnAttendanceGivenListener listener) {
        this.hall = hall;
        this.students = students;
        this.allStudents = new ArrayList<>(students);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentViewHolder(StudentAttendanceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.binding.studentBase.studentItemName.setText(student.getName());
        holder.binding.studentBase.studentItemReg.setText(String.valueOf(student.getUnivReg()));
        holder.binding.studentBase.studentItemRoll.setText(String.valueOf(student.getUnivRoll()));

        if (Boolean.TRUE.equals(hall.getCandidates().get(student.get_id()))) {
            holder.binding.presentBtn.setChecked(true);
        } else if (Boolean.FALSE.equals(hall.getCandidates().get(student.get_id()))){
            holder.binding.absentBtn.setChecked(true);
        }

        holder.binding.attendanceGroup.setOnCheckedChangeListener((group, checkedId) -> {
            listener.OnAttendanceGiven(student, checkedId == R.id.presentBtn, position);
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public interface setOnAttendanceGivenListener {
        void OnAttendanceGiven(Student student, boolean present, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Student> tmp = new ArrayList<>();
                if (constraint.toString().trim().isEmpty()) {
                    tmp.addAll(allStudents);
                } else {
                    tmp.addAll(allStudents.stream().filter(student -> student.getName().toLowerCase(Locale.ROOT).contains(constraint.toString().trim().toLowerCase(Locale.ROOT)) || String.valueOf(student.getUnivRoll()).contains(constraint.toString().trim())).collect(Collectors.toList()));
                }
                FilterResults results = new FilterResults();
                results.values = tmp;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                students.clear();
                students.addAll((Collection<? extends Student>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        StudentAttendanceBinding binding;

        public StudentViewHolder(@NonNull StudentAttendanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
