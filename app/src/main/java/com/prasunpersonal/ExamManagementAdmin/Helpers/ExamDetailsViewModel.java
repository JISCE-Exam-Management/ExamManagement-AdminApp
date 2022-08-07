package com.prasunpersonal.ExamManagementAdmin.Helpers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prasunpersonal.ExamManagementAdmin.Models.Exam;
import com.prasunpersonal.ExamManagementAdmin.Models.Hall;

public class ExamDetailsViewModel extends ViewModel {
    private final MutableLiveData<Exam> setSelectedExam = new MutableLiveData<>();
    private final MutableLiveData<Hall> setSelectedHall = new MutableLiveData<>();

    public void setSetSelectedExam(Exam exam) {
        setSelectedExam.setValue(exam);
    }

    public void setSetSelectedHall(Hall hall) {
        setSelectedHall.setValue(hall);
    }

    public MutableLiveData<Exam> getSetSelectedExam() {
        return setSelectedExam;
    }

    public MutableLiveData<Hall> getSetSelectedHall() {
        return setSelectedHall;
    }
}
