package com.prasunpersonal.ExamManagementAdmin.Helpers;

public class API {
    private static final String BASE_URL = "https://exammanagement-server.herokuapp.com";

    public static final String SIGNUP = String.format("%s/admin/signup", BASE_URL);
    public static final String LOGIN = String.format("%s/admin/login", BASE_URL);

    public static final String ALL_DEGREES = String.format("%s/degrees/all", BASE_URL);
    public static final String ADD_DEGREE = String.format("%s/degrees/add/degree", BASE_URL);
    public static final String ADD_COURSE = String.format("%s/degrees/add/course", BASE_URL);
    public static final String ADD_STREAM = String.format("%s/degrees/add/stream", BASE_URL);
    public static final String ADD_REGULATION = String.format("%s/degrees/add/regulation", BASE_URL);
    public static final String ADD_SEMESTER = String.format("%s/degrees/add/semester", BASE_URL);
    public static final String ADD_PAPER = String.format("%s/degrees/add/paper", BASE_URL);

    public static final String ALL_STUDENTS = String.format("%s/students/all", BASE_URL);
    public static final String GET_STUDENT_BY_ID = String.format("%s/students", BASE_URL);
    public static final String ADD_SINGLE_STUDENT = String.format("%s/students/add/single", BASE_URL);
    public static final String ADD_MULTIPLE_STUDENTS = String.format("%s/students/add/multiple", BASE_URL);
    public static final String UPDATE_STUDENT = String.format("%s/students/update", BASE_URL);
    public static final String DELETE_STUDENT = String.format("%s/students/delete", BASE_URL);

    public static final String ALL_EXAMS = String.format("%s/exams/all", BASE_URL);
    public static final String GET_EXAMS_BY_ID = String.format("%s/exams", BASE_URL);
    public static final String ADD_EXAM = String.format("%s/exams/add", BASE_URL);
    public static final String UPDATE_EXAM = String.format("%s/exams/update", BASE_URL);
    public static final String DELETE_EXAM = String.format("%s/exams/delete", BASE_URL);
    public static final String EXAM_CANDIDATES = String.format("%s/exams/candidates", BASE_URL);
}