package com.prasunpersonal.ExamManagementAdmin.Activities;

import static com.prasunpersonal.ExamManagementAdmin.App.QUEUE;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementAdmin.Adapters.UserAdapter;
import com.prasunpersonal.ExamManagementAdmin.Helpers.API;
import com.prasunpersonal.ExamManagementAdmin.Models.User;
import com.prasunpersonal.ExamManagementAdmin.databinding.ActivityUsersBinding;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class UsersActivity extends AppCompatActivity {
    ActivityUsersBinding binding;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.usersToolbar);

        binding.usersToolbar.setNavigationOnClickListener(v -> finish());

        updateUi();
        binding.usersRefresher.setOnRefreshListener(this::updateUi);
    }

    private void updateUi() {
        binding.usersRefresher.setRefreshing(true);
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, API.ALL_USERS, null, response -> {
            List<User> users = new Gson().fromJson(response.toString(), new TypeToken<List<User>>(){}.getType());
            binding.allUsers.setLayoutManager(new LinearLayoutManager(this));
            binding.allUsers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            binding.allUsers.setAdapter(new UserAdapter(users, (user, position) -> {
                QUEUE.add(new JsonObjectRequest(Request.Method.PATCH, String.format("%s?user=%s",API.UPDATE_USER, user.get_id()), null, userResponse-> {
                    User newUser = new Gson().fromJson(userResponse.toString(), User.class);
                    users.set(position, newUser);
                    Objects.requireNonNull(binding.allUsers.getAdapter()).notifyItemChanged(position);
                }, error -> {
                    user.setVerified(false);
                    Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            binding.usersRefresher.setRefreshing(false);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}