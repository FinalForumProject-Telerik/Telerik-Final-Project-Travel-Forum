package com.example.forum.services;

import com.example.forum.models.User;
import com.example.forum.models.dto.UserDto;

import java.util.List;

public interface UserService {

    List<User> get(User user);

    User get(int id, User user);

    User getByEmail(String email);

    User getByUsername (String username);

    void create (User user);

    void blockUser(int id, User requester);

    void unblockUser(int id, User requester);

    List<UserDto> getUsers();

    void promoteUser(int id, User requester);

    List<UserDto> searchUsers(String username, String email, String firstName, User requester);
}
