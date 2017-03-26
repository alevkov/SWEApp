package com.example.lexlevi.sweapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser, Serializable {

    @SerializedName("semester")
    @Expose
    private String semester;
    @SerializedName("academicYear")
    @Expose
    private Integer academicYear;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("major")
    @Expose
    private String major;
    @SerializedName("contribution")
    @Expose
    private Integer contribution;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("days")
    @Expose
    private List<String> days = null;
    @SerializedName("courses")
    @Expose
    private List<Course> courses = null;
    @SerializedName("groups")
    @Expose
    private List<String> groups = null; // id

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getContribution() {
        return contribution;
    }

    public void setContribution(Integer contribution) {
        this.contribution = contribution;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<String> getGroups() { return groups; }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    @Override
    public String getName() {
        return this.userName;
    }

    @Override
    public String getAvatar() {
        return null;
    }
}