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
    private List<String> participants = null; // id
    @SerializedName("creator")
    @Expose
    private String creator; // id
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("courses")
    @Expose
    private List<Course> courses = null;
    @SerializedName("chats")
    @Expose
    private List<String> chats = null; // id
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public List<String> getChats() {
        return chats;
    }

    public void setMajor(List<String> chats) {
        this.chats = chats;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
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

    @Override
    public String toString() {
        return this.name;
    }
}
