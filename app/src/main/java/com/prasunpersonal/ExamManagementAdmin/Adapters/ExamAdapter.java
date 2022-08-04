package com.prasunpersonal.ExamManagementAdmin.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
import com.prasunpersonal.ExamManagementAdmin.databinding.ExamBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> implements Filterable {
    private final setOnClickListener listener;
    private final ArrayList<Exam> exams;
    private final ArrayList<Exam> allExams;

    public ExamAdapter(ArrayList<Exam> exams, setOnClickListener listener) {
        this.exams = exams;
        this.allExams = new ArrayList<>(exams);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExamViewHolder(ExamBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = exams.get(position);
        holder.binding.examCategory.setText(String.format("%s / %s / %s / %s / %s / %s", exam.getDegree(), exam.getCourse(), exam.getStream(), exam.getRegulation(), exam.getSemester(), exam.getPaper().getCode()));
        holder.binding.examItemName.setText(exam.getName());
        holder.binding.examItemDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date(exam.getExamStartingTime())));
        holder.binding.examItemTime.setText(String.format("%s - %s", new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date(exam.getExamStartingTime())), new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date(exam.getExamEndingTime()))));
        holder.binding.examItemPaper.setText(exam.getPaper().toString());
        holder.itemView.setOnClickListener(v -> listener.OnClickListener(exam, position));
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    public interface setOnClickListener {
        void OnClickListener(Exam exam, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Exam> tmp = new ArrayList<>();
                if (constraint.toString().trim().isEmpty()) {
                    tmp.addAll(allExams);
                } else {
                    tmp.addAll(allExams.stream().filter(exam -> exam.getName().toLowerCase(Locale.ROOT).contains(constraint.toString().trim().toLowerCase(Locale.ROOT))).collect(Collectors.toList()));
                }
                FilterResults results = new FilterResults();
                results.values = tmp;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                exams.clear();
                exams.addAll((Collection<? extends Exam>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        ExamBinding binding;

        public ExamViewHolder(@NonNull ExamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
