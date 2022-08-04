package com.prasunpersonal.ExamManagementAdmin.Models;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Student {
    private String _id, name, collegeId, degree, course, stream, regulation, semester;
    private long univRoll, univReg;
    private int admissionYear;
    private boolean isLateral;
    private ArrayList<Paper> regularPapers;
    private ArrayList<Paper> backlogPapers;

    public Student() {}

    public Student(String name, String collegeId, long univRoll, long univReg, int admissionYear, String degree, String course, String stream, String regulation, String semester, boolean isLateral, ArrayList<Paper> regularPapers) {
        this._id = String.format("%s_%s", univReg, univRoll);
        this.name = name;
        this.collegeId = collegeId;
        this.univRoll = univRoll;
        this.univReg = univReg;
        this.admissionYear = admissionYear;
        this.degree = degree;
        this.course = course;
        this.stream = stream;
        this.regulation = regulation;
        this.semester = semester;
        this.isLateral = isLateral;
        this.regularPapers = regularPapers;
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

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public long getUnivRoll() {
        return univRoll;
    }

    public void setUnivRoll(long univRoll) {
        this.univRoll = univRoll;
    }

    public long getUnivReg() {
        return univReg;
    }

    public void setUnivReg(long univReg) {
        this.univReg = univReg;
    }

    public int getAdmissionYear() {
        return admissionYear;
    }

    public void setAdmissionYear(int admissionYear) {
        this.admissionYear = admissionYear;
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

    public boolean isLateral() {
        return isLateral;
    }

    public void setLateral(boolean lateral) {
        isLateral = lateral;
    }

    public ArrayList<Paper> getRegularPapers() {
        return regularPapers;
    }

    public void setRegularPapers(ArrayList<Paper> regularPapers) {
        this.regularPapers = regularPapers;
    }

    public ArrayList<Paper> getBacklogPapers() {
        return backlogPapers;
    }

    public void setBacklogPapers(ArrayList<Paper> backlogPapers) {
        this.backlogPapers = backlogPapers;
    }
}
