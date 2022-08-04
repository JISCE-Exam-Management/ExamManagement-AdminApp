package com.prasunpersonal.ExamManagementAdmin.Models;

import static com.prasunpersonal.ExamManagementAdmin.App.ME;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Parcel
public class Exam {
    private String _id, name, degree, course, stream, regulation, semester, updatedBy, updateTime;
    private long startingTime, endingTime;
    private Map<String, Boolean> regularCandidates, backlogCandidates;
    private Paper paper;

    public Exam() {}

    public Exam(String name, Paper paper, String degree, String course, String stream, String regulation, String semester, long startingTime, long endingTime) {
        this.name = name;
        this.paper = paper;
        this.degree = degree;
        this.course = course;
        this.stream = stream;
        this.regulation = regulation;
        this.semester = semester;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.updatedBy = ME.get_id();
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

    public long getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(long startingTime) {
        this.startingTime = startingTime;
    }

    public long getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(long endingTime) {
        this.endingTime = endingTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Map<String, Boolean> getRegularCandidates() {
        return regularCandidates;
    }

    public void setRegularCandidates(Map<String, Boolean> regularCandidates) {
        this.regularCandidates = regularCandidates;
    }

    public Map<String, Boolean> getBacklogCandidates() {
        return backlogCandidates;
    }

    public void setBacklogCandidates(Map<String, Boolean> backlogCandidates) {
        this.backlogCandidates = backlogCandidates;
    }
}
