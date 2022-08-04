package com.prasunpersonal.ExamManagementAdmin.Models;

import androidx.annotation.NonNull;

import org.parceler.Parcel;

@Parcel
public class Paper {
    private String code, name;

    public Paper() {}

    public Paper(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s - %s", this.code, this.name);
    }
}
