package com.example.bmobdemo.javabean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by user on 2017/3/6.
 */

public class Person extends BmobObject {
    private String name;
    private String studentNumber;
    private boolean isBoy;
    private String address;
    private BmobFile header;
    private String phoneNum;

    public Person() {
    }
    public Person(String name, String studentNumber, boolean isBoy,String  address, BmobFile header,String phoneNum) {
        this.name = name;
        this.isBoy = isBoy;
        this.studentNumber = studentNumber;
        this.address = address;
        this.header = header;
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public boolean isBoy() {
        return isBoy;
    }

    public void setBoy(boolean boy) {
        isBoy = boy;
    }

    public BmobFile getHeader() {
        return header;
    }

    public void setHeader(BmobFile header) {
        this.header = header;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
