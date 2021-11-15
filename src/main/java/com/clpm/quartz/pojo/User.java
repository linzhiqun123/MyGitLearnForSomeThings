package com.clpm.quartz.pojo;

import lombok.Data;

@Data
public class User {

    private String userName;
    private String passWord;

    public String getPassWord() {
        return passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
