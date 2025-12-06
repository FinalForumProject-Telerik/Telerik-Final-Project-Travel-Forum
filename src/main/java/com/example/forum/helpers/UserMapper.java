package com.example.forum.helpers;

import com.example.forum.models.User;
import com.example.forum.models.dto.RegisterDto;
import com.example.forum.models.dto.UpdateUserDto;
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

    public User fromDto(UpdateUserDto updateUserDto) {
        if (updateUserDto == null) return null;
        User user = new User();
        user.setUsername(updateUserDto.getUsername());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
        // Only set password if it's provided and not empty
        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isEmpty()) {
            user.setPassword(updateUserDto.getPassword());
        }
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

    public RegisterDto toRegisterDto(User user) {
        if (user == null) return null;
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername(user.getUsername());
        registerDto.setFirstName(user.getFirstName());
        registerDto.setLastName(user.getLastName());
        registerDto.setEmail(user.getEmail());

        return registerDto;
    }

    public UpdateUserDto toUpdateUserDto(User user) {
        if (user == null) return null;
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setUsername(user.getUsername());
        updateUserDto.setFirstName(user.getFirstName());
        updateUserDto.setLastName(user.getLastName());
        updateUserDto.setEmail(user.getEmail());
        // Note: Password is not set as we don't want to expose the hashed password
        // User will need to enter a new password if they want to change it
        return updateUserDto;
    }
}
