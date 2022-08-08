package com.prasunpersonal.ExamManagementAdmin.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prasunpersonal.ExamManagementAdmin.Models.Hall;
import com.prasunpersonal.ExamManagementAdmin.databinding.HallsBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public class HallAdapter extends RecyclerView.Adapter<HallAdapter.HallViewHolder> implements Filterable {
    private final setOnClickListener listener;
    private final ArrayList<Hall> halls;
    private final ArrayList<Hall> allHalls;

    public HallAdapter(ArrayList<Hall> halls, setOnClickListener listener) {
        this.halls = halls;
        this.allHalls = new ArrayList<>(halls);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HallViewHolder(HallsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HallViewHolder holder, int position) {
        Hall hall = halls.get(position);
        holder.binding.hallItemName.setText(hall.getName());
        holder.binding.attendanceCount.setText((hall.getUpdatedBy() == null) ? "Attendance not updated till now!" : String.format(Locale.getDefault(), "%d of %d students are present.", hall.getCandidates().values().stream().filter(present -> present).count(), hall.getCandidates().size()));
        holder.itemView.setOnClickListener(v -> listener.OnClickListener(hall, position));
    }

    @Override
    public int getItemCount() {
        return halls.size();
    }

    public interface setOnClickListener {
        void OnClickListener(Hall hall, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Hall> tmp = new ArrayList<>();
                if (constraint.toString().trim().isEmpty()) {
                    tmp.addAll(allHalls);
                } else {
                    tmp.addAll(allHalls.stream().filter(student -> student.getName().toLowerCase(Locale.ROOT).contains(constraint.toString().trim().toLowerCase(Locale.ROOT))).collect(Collectors.toList()));
                }
                FilterResults results = new FilterResults();
                results.values = tmp;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                halls.clear();
                halls.addAll((Collection<? extends Hall>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class HallViewHolder extends RecyclerView.ViewHolder {
        HallsBinding binding;

        public HallViewHolder(@NonNull HallsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
