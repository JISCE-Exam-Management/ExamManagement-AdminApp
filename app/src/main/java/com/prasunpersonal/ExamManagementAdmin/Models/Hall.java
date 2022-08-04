package com.prasunpersonal.ExamManagementAdmin.Models;

import org.parceler.Parcel;

import java.util.Map;

@Parcel
public class Hall {
    private String name;
    private Map<String, Boolean> candidates;

    public Hall() { }

    public Hall(String name, Map<String, Boolean> candidates) {
        this.name = name;
        this.candidates = candidates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<String, Boolean> candidates) {
        this.candidates = candidates;
    }
}
