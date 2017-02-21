package com.example.lexlevi.sweapp.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("participants")
    @Expose
    private List<Object> participants = null;
    @SerializedName("creator")
    @Expose
    private Object creator;
    @SerializedName("courses")
    @Expose
    private List<Course> courses = null;
    @SerializedName("chats")
    @Expose
    private List<Object> chats = null;
    @SerializedName("semester")
    @Expose
    private String semester;
    @SerializedName("academicYear")
    @Expose
    private Integer academicYear;
    @SerializedName("isPrivate")
    @Expose
    private Boolean isPrivate;
    @SerializedName("days")
    @Expose
    private List<String> days;

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public List<Object> getChats() {
        return chats;
    }

    public void setMajor(List<Object> chats) {
        this.chats = chats;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public Object getCreator() {
        return creator;
    }

    public void setCreator(Object creator) {
        this.creator = creator;
    }

    public List<Object> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Object> participants) {
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
