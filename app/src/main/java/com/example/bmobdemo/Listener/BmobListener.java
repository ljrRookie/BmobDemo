package com.example.bmobdemo.Listener;


import com.example.bmobdemo.javabean.Person;

/**
 * Created by user on 2017/3/9.
 */

public interface BmobListener {
    void deleteData(Person person);
    void UpDateData(String name);
}
