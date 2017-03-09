package com.example.lexlevi.sweapp.Models;

import java.util.Date;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

public class Message implements IMessage {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("author")
    @Expose
    private User author;
    @SerializedName("chat")
    @Expose
    private String chat;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getBody() {
        return "@" + getAuthor().getName() +
                System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                body;
    }

    public String getRawBody() {
        return this.body;
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

    @Override
    public IUser getUser() {
        return getAuthor();
    }

    @Override
    public String getText() {
        return this.getBody();
    }
}
