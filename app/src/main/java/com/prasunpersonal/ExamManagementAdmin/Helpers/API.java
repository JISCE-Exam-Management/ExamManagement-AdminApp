package com.prasunpersonal.ExamManagementAdmin.Helpers;

import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class API {
    private static final String BASE_URL = "https://exammanagement-server.herokuapp.com";

    public static final String ADMIN_SIGNUP = String.format("%s/admin/signup", BASE_URL);
    public static final String ADMIN_LOGIN = String.format("%s/admin/login", BASE_URL);

    public static final String ALL_DEGREES = String.format("%s/degrees/all", BASE_URL);
    public static final String DEGREE_BY_ID = String.format("%s/degrees", BASE_URL);
    public static final String ADD_DEGREE = String.format("%s/degrees/add/degree", BASE_URL);
    public static final String ADD_COURSE = String.format("%s/degrees/add/course", BASE_URL);
    public static final String ADD_STREAM = String.format("%s/degrees/add/stream", BASE_URL);
    public static final String ADD_REGULATION = String.format("%s/degrees/add/regulation", BASE_URL);
    public static final String ADD_SEMESTER = String.format("%s/degrees/add/semester", BASE_URL);
    public static final String ADD_PAPER = String.format("%s/degrees/add/paper", BASE_URL);
    public static final String UPDATE_DEGREE = String.format("%s/degrees/update", BASE_URL);
    public static final String DELETE_DEGREE = String.format("%s/degrees/delete", BASE_URL);

    public static final String ALL_STUDENTS = String.format("%s/students/all", BASE_URL);
    public static final String STUDENTS_IN = String.format("%s/students/in", BASE_URL);
    public static final String STUDENT_BY_ID = String.format("%s/students", BASE_URL);
    public static final String INSERT_STUDENTS = String.format("%s/students/insert", BASE_URL);
    public static final String UPSERT_STUDENTS = String.format("%s/students/upsert", BASE_URL);
    public static final String UPDATE_STUDENT = String.format("%s/students/update", BASE_URL);
    public static final String DELETE_STUDENT = String.format("%s/students/delete", BASE_URL);

    public static final String ALL_EXAMS = String.format("%s/exams/all", BASE_URL);
    public static final String UPCOMING_EXAMS = String.format("%s/exams/upcoming", BASE_URL);
    public static final String ONGOING_EXAMS = String.format("%s/exams/ongoing", BASE_URL);
    public static final String COMPLETED_EXAMS = String.format("%s/exams/completed", BASE_URL);
    public static final String EXAM_BY_ID = String.format("%s/exams", BASE_URL);
    public static final String EXAM_CANDIDATES = String.format("%s/exams/candidates", BASE_URL);
    public static final String INSERT_EXAMS = String.format("%s/exams/insert", BASE_URL);
    public static final String UPSERT_EXAMS = String.format("%s/exams/upsert", BASE_URL);
    public static final String UPDATE_EXAM = String.format("%s/exams/update", BASE_URL);
    public static final String DELETE_EXAM = String.format("%s/exams/delete", BASE_URL);
    public static final String ADD_HALL = String.format("%s/exams/hall/add", BASE_URL);
    public static final String UPDATE_HALL = String.format("%s/exams/hall/update", BASE_URL);
    public static final String DELETE_HALL = String.format("%s/exams/hall/delete", BASE_URL);

    public static final String ALL_USERS = String.format("%s/user/all", BASE_URL);
    public static final String USER_BY_ID = String.format("%s/user", BASE_URL);
    public static final String USER_SIGNUP = String.format("%s/user/signup", BASE_URL);
    public static final String USER_LOGIN = String.format("%s/user/login", BASE_URL);
    public static final String UPDATE_USER = String.format("%s/user/update", BASE_URL);
    public static final String DELETE_USER = String.format("%s/user/delete", BASE_URL);

    public static String getQuery(Map<String, String> params) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            queryBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return queryBuilder.deleteCharAt(queryBuilder.length() - 1).toString();
    }

    //            return new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8)).getJSONArray("errors").getJSONObject(0).getString("message");
    public static String parseVolleyError(VolleyError error) {
        try {
            return new String(error.networkResponse.data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}