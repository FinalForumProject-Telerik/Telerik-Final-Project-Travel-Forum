package com.example.forum.controllers.rest;

import com.example.forum.exceptions.EntityDuplicateException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.UserMapper;
import com.example.forum.models.User;
import com.example.forum.models.dto.UserDto;
import com.example.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserRestController {
    private final UserService usersService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;


    public UserRestController(UserService usersService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.usersService = usersService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {

        User user = userMapper.fromDto(userDto);
        try{
            return usersService.createUser(user);
        }catch (EntityDuplicateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
    @GetMapping("/search")
    public List<UserDto> searchUsers(
            //toDo authentication
            //toDo authorization admin only
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false, name = "firstName") String firstName
    ) {
        return usersService.searchUsers(username, email, firstName);
    }
    @PostMapping("/{id}/block")
    public void blockUser(@PathVariable int id) {
        //toDo authentication
        //toDo authorization admin only
        try {
            usersService.blockUser(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @DeleteMapping("/{id}/block")
    public void unblockUser(@PathVariable int id) {
        //toDo authentication
        //toDo authorization admin only
        try {
            usersService.unblockUser(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @GetMapping
    public List<UserDto> getAllUsers() {
        //toDo authentication
        //toDo authorization admin only
        return usersService.getAllUsers();
    }
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        //toDo authentication
        //toDo authorization admin only
        try {
            return usersService.getUserById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
