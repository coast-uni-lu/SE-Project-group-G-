package com.group_g.demo.dto;

public class QuizStartRequest {
    private String nickname;
    private String email;
    //getters
    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }
    //setters
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
