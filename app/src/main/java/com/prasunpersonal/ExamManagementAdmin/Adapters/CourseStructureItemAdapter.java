package com.prasunpersonal.ExamManagementAdmin.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prasunpersonal.ExamManagementAdmin.Models.Course;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.Models.Paper;
import com.prasunpersonal.ExamManagementAdmin.Models.Regulation;
import com.prasunpersonal.ExamManagementAdmin.Models.Semester;
import com.prasunpersonal.ExamManagementAdmin.Models.Stream;
import com.prasunpersonal.ExamManagementAdmin.databinding.CourseStructureItemBinding;

import java.util.List;
import java.util.Locale;

public class CourseStructureItemAdapter<T> extends RecyclerView.Adapter<CourseStructureItemAdapter.DegreeViewHolder> {
    private final setOnClickListener<T> listener;
    private final List<T> items;

    public CourseStructureItemAdapter(List<T> items, setOnClickListener<T> listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DegreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DegreeViewHolder(CourseStructureItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DegreeViewHolder holder, int position) {
        T item = items.get(position);
        if (item instanceof Degree) {
            holder.binding.titleText.setText(((Degree) item).getDegreeName());
            holder.binding.subtitleText.setText(String.format(Locale.getDefault(), "Contains %d courses", ((Degree) item).getCourses().size()));
        } else if (item instanceof Course) {
            holder.binding.titleText.setText(((Course) item).getCourseName());
            holder.binding.subtitleText.setText(String.format(Locale.getDefault(), "Contains %d streams", ((Course) item).getStreams().size()));
        } else if (item instanceof Stream) {
            holder.binding.titleText.setText(((Stream) item).getStreamName());
            holder.binding.subtitleText.setText(String.format(Locale.getDefault(), "Contains %d Regulations", ((Stream) item).getRegulations().size()));
        } else if (item instanceof Regulation) {
            holder.binding.titleText.setText(((Regulation) item).getRegulationName());
            holder.binding.subtitleText.setText(String.format(Locale.getDefault(), "Contains %d Semesters", ((Regulation) item).getSemesters().size()));
        } else if (item instanceof Semester) {
            holder.binding.titleText.setText(((Semester) item).getSemesterName());
            holder.binding.subtitleText.setText(String.format(Locale.getDefault(), "Contains %d Papers", ((Semester) item).getPapers().size()));
        } else if (item instanceof Paper) {
            holder.binding.titleText.setText(((Paper) item).getName());
            holder.binding.subtitleText.setText(((Paper) item).getCode());
        }
        holder.itemView.setOnClickListener(v -> listener.OnClickListener(item, position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface setOnClickListener<T> {
        void OnClickListener(T item, int position);
    }


    public static class DegreeViewHolder extends RecyclerView.ViewHolder {
        CourseStructureItemBinding binding;

        public DegreeViewHolder(@NonNull CourseStructureItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
