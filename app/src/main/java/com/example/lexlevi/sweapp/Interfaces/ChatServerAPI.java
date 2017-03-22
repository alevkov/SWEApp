package com.example.lexlevi.sweapp.Interfaces;

import com.example.lexlevi.sweapp.Models.Chat;
import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.Message;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Common.URLs;

import retrofit2.http.*;
import retrofit2.Call;

import java.util.List;

public interface ChatServerAPI {

    // Users
    @GET(URLs.kGetUsers)
    Call<List<User>> getUsers();
    @GET(URLs.kGetUserForId)
    Call<User> getUserForId(@Path("id") String id);
    @POST(URLs.kUserSignup)
    Call<User> createUser(@Body User user);
    @POST(URLs.kUserLogin)
    Call<User> loginUser(@Body User user);

    // Courses
    @GET(URLs.kGetCourses)
    Call<List<Course>> getAllCourses();

    // Groups
    @GET(URLs.kGetGroupForId)
    Call<Group> getGroupForId(@Path("id") String id);
    @GET(URLs.kGetAllUsersForGroup)
    Call<List<User>> getGroupUsersList(@Path("id") String id);
    @GET(URLs.kGetAllGroupsForCourse)
    Call<List<Group>> getGroupListForCourse(@Path("code") String courseCode,
                                         @Query("semester") String courseSemester,
                                         @Query("academicYear") int courseYear);
    @GET(URLs.kGetAllGroupsForUser)
    Call<List<Group>> getGroupListForUser(@Path("id") String userId);
    @POST(URLs.kCreateGroupForUser)
    Call<Group> createGroupForUser(@Path("id") String userId,
                                   @Body Group group);

    // Chats + Messages
    @GET(URLs.kGetChatForId)
    Call<Chat> getChatForID(@Path("id") String id);
    @GET(URLs.kGetChatsForGroup)
    Call<List<Chat>> getChatListForGroup(@Path("groupId") String groupId);
    @GET(URLs.kGetMessagesForChat)
    Call<List<Message>> getMessagesForChat(@Path("id") String id);
    @GET(URLs.kGetUsersForChat)
    Call<List<User>> getUsersForChat(@Path("id") String id);
    @POST(URLs.kCreateNewChatForGroup)
    Call<Chat> createChatForGroup(@Body Chat chat,
                                  @Path("groupId") String groupId);
    @PATCH(URLs.kUpdateChatForId)
    Call<Chat> updateChatForId(@Path("id") String id);
    @DELETE(URLs.kDeleteChatForId)
    Call<Chat> deleteChatForId(@Path("id") String id);
    @POST(URLs.kCreateNewMessageForChat)
    Call<Message> createNewMessageForChat(@Path("id") String chatId,
                                          @Body Message message);
}
