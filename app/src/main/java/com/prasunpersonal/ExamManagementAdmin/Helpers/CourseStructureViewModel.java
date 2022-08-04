package com.prasunpersonal.ExamManagementAdmin.Helpers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prasunpersonal.ExamManagementAdmin.Models.Course;
import com.prasunpersonal.ExamManagementAdmin.Models.Degree;
import com.prasunpersonal.ExamManagementAdmin.Models.Regulation;
import com.prasunpersonal.ExamManagementAdmin.Models.Semester;
import com.prasunpersonal.ExamManagementAdmin.Models.Stream;

import java.util.List;

public class CourseStructureViewModel extends ViewModel {
    private final MutableLiveData<List<Degree>> allDegrees = new MutableLiveData<>();
    private final MutableLiveData<Degree> selectedDegree = new MutableLiveData<>();
    private final MutableLiveData<Course> selectedCourse = new MutableLiveData<>();
    private final MutableLiveData<Stream> selectedStream = new MutableLiveData<>();
    private final MutableLiveData<Regulation> selectedRegulation = new MutableLiveData<>();
    private final MutableLiveData<Semester> selectedSemester = new MutableLiveData<>();

    public void setAllDegree(List<Degree> degrees) {
        allDegrees.setValue(degrees);
    }

    public void setSelectedDegree(Degree degree) {
        selectedDegree.setValue(degree);
    }

    public void setSelectedCourse(Course course) {
        selectedCourse.setValue(course);
    }

    public void setSelectedStream(Stream stream) {
        selectedStream.setValue(stream);
    }

    public void setSelectedRegulation(Regulation regulation) {
        selectedRegulation.setValue(regulation);
    }

    public void setSelectedSemester(Semester semester) {
        selectedSemester.setValue(semester);
    }

    public LiveData<List<Degree>> getAllDegrees() {
        return allDegrees;
    }

    public LiveData<Degree> getSelectedDegree() {
        return selectedDegree;
    }

    public LiveData<Course> getSelectedCourse() {
        return selectedCourse;
    }

    public LiveData<Stream> getSelectedStream() {
        return selectedStream;
    }

    public LiveData<Regulation> getSelectedRegulation() {
        return selectedRegulation;
    }

    public LiveData<Semester> getSelectedSemester() {
        return selectedSemester;
    }
}
