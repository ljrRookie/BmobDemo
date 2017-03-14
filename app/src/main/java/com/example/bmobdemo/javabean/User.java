package com.example.bmobdemo.javabean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;


/**
 * Created by user on 2017/3/13.
 */

public class User extends BmobUser {
    private BmobFile header;
    private String userName;
    private String passWord;
    private boolean isBoy;

    public User() {
    }

    public User(BmobFile header,boolean isBoy, String passWord, String userName) {
        this.header = header;
        this.isBoy = isBoy;
        this.passWord = passWord;
        this.userName = userName;
    }

    public BmobFile getHeader() {
        return header;
    }

    public void setHeader(BmobFile header) {
        this.header = header;
    }

    public boolean isBoy(boolean isBoy) {
        return this.isBoy;
    }

    public void setBoy(boolean boy) {
        isBoy = boy;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
