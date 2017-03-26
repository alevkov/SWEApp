package com.example.lexlevi.sweapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lexlevi on 4/1/17.
 */

public class Classmate implements Serializable {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("matches")
    @Expose
    private Integer matches;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getMatches() {
        return this.matches;
    }

    public void setMatches(Integer matches) {
        this.matches = matches;
    }
}
