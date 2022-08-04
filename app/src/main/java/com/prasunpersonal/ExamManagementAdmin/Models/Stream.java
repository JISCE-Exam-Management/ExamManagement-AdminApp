package com.prasunpersonal.ExamManagementAdmin.Models;

import java.util.ArrayList;
import java.util.Objects;

import org.parceler.Parcel;

@Parcel
public class Stream {
    private String _id, streamName;
    private ArrayList<Regulation> regulations;

    public Stream() {}

    public Stream(String streamName) {
        this.streamName = streamName;
        this.regulations = new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public ArrayList<Regulation> getRegulations() {
        return regulations;
    }

    public void setRegulations(ArrayList<Regulation> regulations) {
        this.regulations = regulations;
    }

    public void addRegulation(Regulation regulation) {
        this.regulations.add(regulation);
        this.regulations.sort((r1, r2) -> r1.getRegulationName().compareToIgnoreCase(r2.getRegulationName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stream)) return false;
        Stream stream = (Stream) o;
        return get_id().equals(stream.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }

}
