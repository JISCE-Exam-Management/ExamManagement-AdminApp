package com.prasunpersonal.ExamManagementAdmin.Models;

import java.util.ArrayList;
import java.util.Objects;

import org.parceler.Parcel;

@Parcel
public class Regulation {
    private String _id, regulationName;
    private ArrayList<Semester> semesters;

    public Regulation() {}

    public Regulation(String regulationName) {
        this.regulationName = regulationName;
        this.semesters = new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRegulationName() {
        return regulationName;
    }

    public void setRegulationName(String regulationName) {
        this.regulationName = regulationName;
    }

    public ArrayList<Semester> getSemesters() {
        return semesters;
    }

    public void setSemesters(ArrayList<Semester> semesters) {
        this.semesters = semesters;
    }

    public void addSemester(Semester semester) {
        this.semesters.add(semester);
        this.semesters.sort((s1, s2) -> s1.getSemesterName().compareToIgnoreCase(s2.getSemesterName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Regulation)) return false;
        Regulation regulation = (Regulation) o;
        return get_id().equals(regulation.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }
}
