package com.example.forum.controllers.rest;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityDuplicateException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.UserMapper;
import com.example.forum.models.User;
import com.example.forum.models.dto.UserDto;
import com.example.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
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
            //toDo authorization admin only
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false, name = "firstName") String firstName,
            @RequestHeader HttpHeaders headers
    ) {
        try{
            User user = authenticationHelper.tryGetUser(headers);
            return usersService.searchUsers(username, email, firstName, user);
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

    }
    @PostMapping("/{id}/block")
    public void blockUser(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        //toDo authentication
        //toDo authorization admin only
        try {
            User user = authenticationHelper.tryGetUser(headers);
            usersService.blockUser(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @DeleteMapping("/{id}/block")
    public void unblockUser(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        //toDo authorization admin only
        try {
            User user = authenticationHelper.tryGetUser(headers);
            usersService.unblockUser(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @GetMapping
    public List<UserDto> getAllUsers(@RequestHeader HttpHeaders headers) {

        //toDo authorization admin only
        try{
            User user = authenticationHelper.tryGetUser(headers);
            return usersService.getAllUsers(user);
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

    }
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id, @RequestHeader HttpHeaders headers) {

        //toDo authorization admin only
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return usersService.getUserById(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
