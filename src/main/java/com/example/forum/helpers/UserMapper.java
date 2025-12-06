package com.example.forum.helpers;

import com.example.forum.models.User;
import com.example.forum.models.dto.RegisterDto;
import com.example.forum.models.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User fromDto(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public User fromDto(RegisterDto registerDto) {
        if (registerDto == null) return null;
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        return user;
    }

    public UserDto toDto(User user) {
        if (user == null) return null;
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setBlocked(user.isBlocked());
        userDto.setAdmin(user.isAdmin());
        return userDto;
    }
}
