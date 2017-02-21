package com.example.lexlevi.sweapp.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chat {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("participants")
    @Expose
    private List<Object> participants = null;
    @SerializedName("group")
    @Expose
    private Object group;
    @SerializedName("isGroupMessage")
    @Expose
    private Boolean isGroupMessage;

    public Boolean getIsGroupMessage() {
        return isGroupMessage;
    }

    public void setIsGroupMessage(Boolean isGroupMessage) {
        this.isGroupMessage = isGroupMessage;
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

    public Object getGroup() {
        return group;
    }

    public void setGroup(Object group) {
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

