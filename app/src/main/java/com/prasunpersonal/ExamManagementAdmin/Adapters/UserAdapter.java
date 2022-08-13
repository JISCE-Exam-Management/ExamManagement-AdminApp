package com.prasunpersonal.ExamManagementAdmin.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prasunpersonal.ExamManagementAdmin.Models.User;
import com.prasunpersonal.ExamManagementAdmin.databinding.UserBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {
    private final setOnVerifyListener listener;
    private final List<User> users;
    private final List<User> allUsers;

    public UserAdapter(List<User> users, setOnVerifyListener listener) {
        this.users = users;
        this.allUsers = new ArrayList<>(users);
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(UserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.binding.userName.setText(user.getName());
        holder.binding.userEmail.setText(String.valueOf(user.getEmail()));
        if (user.isVerified()) {
            holder.binding.verified.setVisibility(View.VISIBLE);
            holder.binding.btnVerify.setVisibility(View.GONE);
        } else {
            holder.binding.verified.setVisibility(View.GONE);
            holder.binding.btnVerify.setVisibility(View.VISIBLE);
        }
        holder.binding.btnVerify.setOnClickListener(v -> listener.OnVerifyListener(user, position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface setOnVerifyListener {
        void OnVerifyListener(User user, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<User> tmp = new ArrayList<>();
                if (constraint.toString().trim().isEmpty()) {
                    tmp.addAll(allUsers);
                } else {
                    tmp.addAll(allUsers.stream().filter(user -> user.getName().toLowerCase(Locale.ROOT).contains(constraint.toString().trim().toLowerCase(Locale.ROOT))).collect(Collectors.toList()));
                }
                FilterResults results = new FilterResults();
                results.values = tmp;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                users.clear();
                users.addAll((Collection<? extends User>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        UserBinding binding;

        public UserViewHolder(@NonNull UserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
