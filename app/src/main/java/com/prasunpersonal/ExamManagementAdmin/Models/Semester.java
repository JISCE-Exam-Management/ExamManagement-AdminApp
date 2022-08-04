package com.prasunpersonal.ExamManagementAdmin.Models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Objects;

@Parcel
public class Semester {
    private String _id, semesterName;
    private ArrayList<Paper> papers;

    public Semester() {}

    public Semester(String semesterName) {
        this.semesterName = semesterName;
        this.papers = new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public ArrayList<Paper> getPapers() {
        return papers;
    }

    public void setPapers(ArrayList<Paper> papers) {
        this.papers = papers;
    }

    public void addPaper(Paper paper) {
        this.papers.add(paper);
        this.papers.sort((p1, p2) -> p1.getCode().compareToIgnoreCase(p2.getCode()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Semester)) return false;
        Semester semester = (Semester) o;
        return get_id().equals(semester.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }

}
