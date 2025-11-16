package com.example.forum.services;

import org.apache.catalina.User;

import java.util.List;

public interface UserService {

    List<User> get();

    User get(int id);

    User get (String username);

    void create (User user);
}
