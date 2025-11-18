package com.example.forum.services;

import com.example.forum.models.User;
import com.example.forum.models.dto.UserDto;

import java.util.List;

public interface UserService {

    List<User> get();

    User get(int id);

    User getByEmail(String email);

    User getByUsername (String username);

    void create (User user);

    void blockUser(int id, User requester);

    void unblockUser(int id, User requester);

    List<UserDto> getUsers();

    UserDto getUserDto(int id);

    List<UserDto> searchUsers(String username, String email, String firstName, User requester);
}
