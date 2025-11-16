package com.example.forum.services;

import com.example.forum.exceptions.EntityDuplicateException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.repositories.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> get() {
        return userRepository.get();
    }

    @Override
    public User get(int id) {
        return userRepository.get(id);
    }

    @Override
    public User get(String username) {
        return userRepository.get(username);
    }

    @Override
    public void create(User user) {

        boolean userExists = true;

        try{
            userRepository.get(user.getUsername());
        } catch(EntityNotFoundException e){
            userExists = false;
        }

        if(userExists){
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }

        userRepository.create(user);
    }
}
