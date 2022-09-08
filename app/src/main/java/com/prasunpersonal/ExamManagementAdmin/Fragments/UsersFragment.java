package com.prasunpersonal.ExamManagementAdmin.Fragments;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Adapters.ExamAdapter;
import com.prasunpersonal.ExamManagementAdmin.Adapters.UserAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.User;
import com.prasunpersonal.ExamManagementAdmin.R;
import com.prasunpersonal.ExamManagementAdmin.databinding.FragmentUsersBinding;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class UsersFragment extends Fragment {
    FragmentUsersBinding binding;

    public UsersFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        updateUi();
        binding.usersRefresher.setOnRefreshListener(this::updateUi);
        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.homeAddNew).setTitle("Insert Exams");
        MenuItem searchItem = menu.findItem(R.id.homeSearch);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.setGroupVisible(R.id.hidden, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.setGroupVisible(R.id.hidden, true);
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
                if (binding.allUsers.getAdapter() != null)
                    ((UserAdapter) binding.allUsers.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }

    private void updateUi() {
        binding.usersRefresher.setRefreshing(true);
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, API.ALL_USERS, null, response -> {
            List<User> users = new Gson().fromJson(response.toString(), new TypeToken<List<User>>(){}.getType());
            binding.allUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.allUsers.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
            binding.allUsers.setAdapter(new UserAdapter(users, (user, position) -> {
                QUEUE.add(new JsonObjectRequest(Request.Method.PATCH, String.format("%s?user=%s",API.UPDATE_USER, user.get_id()), null, userResponse-> {
                    User newUser = new Gson().fromJson(userResponse.toString(), User.class);
                    users.set(position, newUser);
                    Objects.requireNonNull(binding.allUsers.getAdapter()).notifyItemChanged(position);
                }, error -> {
                    user.setVerified(false);
                    Toast.makeText(getContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
                    @Override
                    public byte[] getBody() {
                        user.setVerified(true);
                        return new Gson().toJson(user).getBytes(StandardCharsets.UTF_8);
                    }
                }).setRetryPolicy(new DefaultRetryPolicy());
            }));
            binding.usersRefresher.setRefreshing(false);
        }, error -> {
            Toast.makeText(getContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            binding.usersRefresher.setRefreshing(false);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }

}