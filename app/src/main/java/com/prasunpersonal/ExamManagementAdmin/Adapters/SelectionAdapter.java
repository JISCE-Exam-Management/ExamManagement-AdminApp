package com.prasunpersonal.ExamManagementAdmin.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prasunpersonal.ExamManagementAdmin.Models.Hall;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;
import com.prasunpersonal.ExamManagementAdmin.databinding.StudentSelectionBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder> implements Filterable {
    private final List<Student> students;
    private final List<Student> allStudents;
    private final setOnSelectionChangeListener listener;

    public SelectionAdapter(List<Student> students, setOnSelectionChangeListener listener) {
        this.students = students;
        this.allStudents = new ArrayList<>(students);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectionViewHolder(StudentSelectionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder holder, int position) {
        Student student = students.get(position);
        holder.binding.studentBase.studentItemName.setText(student.getName());
        holder.binding.studentBase.studentItemReg.setText(String.valueOf(student.getUnivReg()));
        holder.binding.studentBase.studentItemRoll.setText(String.valueOf(student.getUnivRoll()));

        holder.binding.selectStudent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.OnSelectionChange(student, isChecked, position);
        });

        holder.itemView.setOnClickListener(v -> holder.binding.selectStudent.setChecked(!holder.binding.selectStudent.isChecked()));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public interface setOnSelectionChangeListener {
        void OnSelectionChange(Student student, boolean selected, int position);
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


    public static class SelectionViewHolder extends RecyclerView.ViewHolder {
        StudentSelectionBinding binding;

        public SelectionViewHolder(@NonNull StudentSelectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
