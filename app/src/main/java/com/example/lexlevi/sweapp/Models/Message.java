package com.example.lexlevi.sweapp.Models;

import java.util.Date;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("author")
    @Expose
    private String author; // id
    @SerializedName("chat")
    @Expose
    private String chat; // id
    @SerializedName("updatedAt")
    @Expose
    private Date updatedAt;
    @SerializedName("createdAt")
    @Expose
    private Date createdAt;

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
