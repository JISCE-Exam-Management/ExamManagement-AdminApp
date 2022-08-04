package com.prasunpersonal.ExamManagementAdmin.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.databinding.StudentBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> implements Filterable {
    private final setOnClickListener listener;
    private final ArrayList<Student> students;
    private final ArrayList<Student> allStudents;

    public StudentAdapter(ArrayList<Student> students, setOnClickListener listener) {
        this.students = students;
        this.allStudents = new ArrayList<>(students);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentViewHolder(StudentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.binding.studentItemName.setText(student.getName());
        holder.binding.studentItemRoll.setText(String.format(Locale.getDefault(), "%d - %d", student.getUnivReg(), student.getUnivRoll()));
        holder.itemView.setOnClickListener(v -> listener.OnClickListener(student, position));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public interface setOnClickListener {
        void OnClickListener(Student student, int position);
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
        StudentBinding binding;

        public StudentViewHolder(@NonNull StudentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
