package com.prasunpersonal.ExamManagementAdmin.Models;

import static com.prasunpersonal.ExamManagementAdmin.App.ME;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Map;

@Parcel
public class Exam {
    private String _id, name, degree, course, stream, regulation, semester;
    private long examStartingTime, examEndingTime, attendanceStartingTime, attendanceEndingTime;
    private ArrayList<Hall> halls;
    private Paper paper;

    public Exam() {}

    public Exam(String name, Paper paper, String degree, String course, String stream, String regulation, String semester, long examStartingTime, long examEndingTime, long attendanceStartingTime, long attendanceEndingTime) {
        this.name = name;
        this.paper = paper;
        this.degree = degree;
        this.course = course;
        this.stream = stream;
        this.regulation = regulation;
        this.semester = semester;
        this.examStartingTime = examStartingTime;
        this.examEndingTime = examEndingTime;
        this.attendanceStartingTime = attendanceStartingTime;
        this.attendanceEndingTime = attendanceEndingTime;
        halls = new ArrayList<>();

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public long getExamStartingTime() {
        return examStartingTime;
    }

    public void setExamStartingTime(long examStartingTime) {
        this.examStartingTime = examStartingTime;
    }

    public long getExamEndingTime() {
        return examEndingTime;
    }

    public void setExamEndingTime(long examEndingTime) {
        this.examEndingTime = examEndingTime;
    }

    public long getAttendanceStartingTime() {
        return attendanceStartingTime;
    }

    public void setAttendanceStartingTime(long attendanceStartingTime) {
        this.attendanceStartingTime = attendanceStartingTime;
    }

    public long getAttendanceEndingTime() {
        return attendanceEndingTime;
    }

    public void setAttendanceEndingTime(long attendanceEndingTime) {
        this.attendanceEndingTime = attendanceEndingTime;
    }

    public ArrayList<Hall> getHalls() {
        return halls;
    }

    public void setHalls(ArrayList<Hall> halls) {
        this.halls = halls;
    }
}
