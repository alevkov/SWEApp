package com.example.lexlevi.sweapp.Common;

public class URLs {

    // Base
    public static final String HEROKU = "https://uniteam-api.herokuapp.com/";
    public static final String LOCAL = "http://150.130.3.184:3000/";
    public static final String BASE_API = HEROKU;

    // User Endpoints
    public static final String kGetUsers = "users";
    public static final String kGetUserForId = "users/{id}";
    public static final String kInviteUserToGroup = "users/invite/{userId}/from/{sender}";
    public static final String kUserLogin = "users/login";
    public static final String kUserSignup = "users/new";
    public static final String kUserMatch = "/users/{id}/match";

    // Course Endpoints
    public static final String kGetCourses = "courses";

    // Group Endpoints
    public static final String kGetGroupForId = "groups/{id}";
    public static final String kGetAllUsersForGroup = "groups/{id}/users";
    public static final String kGetAllGroupsForCourse = "groups/course/{code}";
    public static final String kGetAllGroupsForUser = "groups/user/{id}";
    public static final String kCreateGroupForUser = "groups/user/{id}";

    // Chat Endpoints
    public static final String kGetChatForId = "chats/{id}";
    public static final String kGetChatsForGroup = "chats/group/{groupId}";
    public static final String kGetMessagesForChat = "chats/{id}/messages";
    public static final String kGetUsersForChat = "chats/{id}/users";
    public static final String kCreateNewChatForGroup = "chats/group/{groupId}/new";
    public static final String kUpdateChatForId = "chats/{id}/update";
    public static final String kDeleteChatForId = "chats/{id}/delete";
    public static final String kCreateNewMessageForChat = "chats/{id}/messages/new";
}
