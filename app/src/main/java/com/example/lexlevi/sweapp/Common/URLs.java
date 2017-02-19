package com.example.lexlevi.sweapp.Common;

public class URLs {

    // Base
    public static final String BASE_API = "http://10.12.101.58:3000/";

    // User Endpoints
    public static final String kGetUsers = "users";
    public static final String kUserLogin = "users/login";
    public static final String kUserSignup = "users/new";

    // Course Endpoints
    public static final String kGetCourses = "courses";

    // Group Endpoints
    public static final String kGetAllUsersForGroup = "groups/{id}/users";
    public static final String kGetAllGroupsForCourse = "groups/course/{code}";
    public static final String kGetAllGroupsForUser = "groups/user/{id}";
    public static final String kCreateGroupForUser = "groups/user/{id}";
}
