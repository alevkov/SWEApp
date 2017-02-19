package com.example.lexlevi.sweapp.Controllers;

import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Common.URLs;

import retrofit2.http.*;
import retrofit2.Call;

import java.util.List;

public interface ChatServerAPI {

    // Users
    @POST(URLs.kUserSignup)
    Call<User> createUser(@Body User user);
    @POST(URLs.kUserLogin)
    Call<User> loginUser(@Body User user);

    // Courses
    @GET(URLs.kGetCourses)
    Call<List<Course>> getAllCourses();

    // Groups
    @GET(URLs.kGetAllUsersForGroup)
    Call<List<User>> groupUsersList(@Path("id") int groupId);
    @GET(URLs.kGetAllGroupsForCourse)
    Call<List<Group>> groupListForCourse(@Path("code") String courseCode,
                                         @Query("semester") String courseSemester,
                                         @Query("academicYear") int courseYear);
    @GET(URLs.kGetAllGroupsForUser)
    Call<List<Group>> groupListForUser(@Path("id") int userId);
    @POST(URLs.kCreateGroupForUser)
    Call<Group> createGroupForUser(@Path("id") int userId);
}
