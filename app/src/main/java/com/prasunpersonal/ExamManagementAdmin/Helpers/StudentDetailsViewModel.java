package com.prasunpersonal.ExamManagementAdmin.Helpers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
import com.prasunpersonal.ExamManagementAdmin.Models.Student;

public class StudentDetailsViewModel extends ViewModel {
    private final MutableLiveData<Student> setSelectedStudent = new MutableLiveData<>();

    public void setSetSelectedStudent(Student student) {
        setSelectedStudent.setValue(student);
    }

    public MutableLiveData<Student> getSetSelectedStudent() {
        return setSelectedStudent;
    }
}
