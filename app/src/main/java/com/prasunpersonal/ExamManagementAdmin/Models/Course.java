package com.prasunpersonal.ExamManagementAdmin.Models;

import java.util.ArrayList;
import java.util.Objects;

import org.parceler.Parcel;

@Parcel
public class Course {
    private String _id, courseName;
    private ArrayList<Stream> streams;

    public Course() {}

    public Course(String courseName) {
        this.courseName = courseName;
        this.streams = new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public ArrayList<Stream> getStreams() {
        return streams;
    }

    public void setStreams(ArrayList<Stream> streams) {
        this.streams = streams;
    }

    public void addStream(Stream regulation) {
        this.streams.add(regulation);
        this.streams.sort((s1, s2) -> s1.getStreamName().compareToIgnoreCase(s2.getStreamName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return get_id().equals(course.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }

}
