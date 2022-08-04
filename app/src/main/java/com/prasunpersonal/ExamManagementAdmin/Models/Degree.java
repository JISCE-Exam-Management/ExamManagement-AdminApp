package com.prasunpersonal.ExamManagementAdmin.Models;

import java.util.ArrayList;
import java.util.Objects;

import org.parceler.Parcel;

@Parcel
public class Degree {
    private String _id, degreeName;
    private ArrayList<Course> courses;

    public Degree() {}

    public Degree(String degreeName) {
        this.degreeName = degreeName;
        this.courses = new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
        this.courses.sort((c1, c2) -> c1.getCourseName().compareToIgnoreCase(c2.getCourseName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Degree)) return false;
        Degree degree = (Degree) o;
        return get_id().equals(degree.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }
}



