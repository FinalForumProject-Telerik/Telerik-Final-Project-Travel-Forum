package com.example.forum.services;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityDuplicateException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.UserMapper;
import com.example.forum.models.dto.UserDto;
import com.example.forum.repositories.UserRepository;
import com.example.forum.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> get(User user) {
        if (!user.isAdmin()){
            throw new AuthorizationException("Not authorized");
        }
        return userRepository.get();
    }


    @Override
    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public void create(User user) {

        boolean userExists = true;

        try{
            userRepository.getByUsername(user.getUsername());
        } catch(EntityNotFoundException e){
            userExists = false;
        }

        if(userExists){
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }

        userRepository.create(user);
    }

    @Override
    public void blockUser(int id, User requester) throws  EntityNotFoundException, AuthorizationException {
        if (!requester.isAdmin()){
            throw new AuthorizationException("Not authorized");
        }
        User user = userRepository.get(id);
        user.setBlocked(true);
        userRepository.update(user);
    }

    @Override
    public void unblockUser(int id, User requester) {
        if (!requester.isAdmin()){
            throw new AuthorizationException("Not authorized");
        }
        User user = userRepository.get(id);
        user.setBlocked(false);
        userRepository.update(user);
    }

    @Override
    public List<UserDto> getUsers() {
        //authenticationHelper.requireAdmin(requester);
        return userRepository.get().stream().map(userMapper :: toDto).collect(Collectors.toList());
    }


    @Override
    public User get(int id, User user) {
        if (!user.isAdmin()){
            throw new AuthorizationException("Not authorized");
        }
        return userRepository.get(id);
    }

    @Override
    public List<UserDto> searchUsers(String username, String email, String firstName, User requester) {
        List<User> result = userRepository.search(username, email, firstName, requester);
        return result.stream().map(userMapper :: toDto).collect(Collectors.toList());
    }

    @Override
    public void promoteUser(int id, User requester) {
        if (!requester.isAdmin()) {
            throw new AuthorizationException("Not authorized");
        }
        User user = userRepository.get(id);
        user.setAdmin(true);
        userRepository.update(user);
    }
}
