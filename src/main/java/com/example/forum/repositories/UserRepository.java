package com.example.forum.repositories;

import com.example.forum.models.User;

import java.util.List;

public interface UserRepository {

    List<com.example.forum.models.User> get();

    com.example.forum.models.User get (int id);

    com.example.forum.models.User get (String username);

    void create (User user);

    void update (User user);

    List<User> search(String username, String email, String firstName, User requester);
}
