package com.prasunpersonal.ExamManagementAdmin.Helpers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prasunpersonal.ExamManagementAdmin.Models.Exam;

public class ExamDetailsViewModel extends ViewModel {
    private final MutableLiveData<Exam> setSelectedExam = new MutableLiveData<>();

    public void setSetSelectedExam(Exam exam) {
        setSelectedExam.setValue(exam);
    }

    public MutableLiveData<Exam> getSetSelectedExam() {
        return setSelectedExam;
    }
}
