package com.example.forum.controllers.mvc;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityDuplicateException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.UserMapper;
import com.example.forum.models.User;
import com.example.forum.models.dto.LoginDto;
import com.example.forum.models.dto.RegisterDto;
import com.example.forum.models.dto.UpdateUserDto;
import com.example.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserMvcController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/test-error")
    public String testErrorPage() {
        // This will be caught by GlobalExceptionHandler
        throw new EntityNotFoundException("User", 999);
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new LoginDto());
        return "LoginView";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("user") LoginDto loginDto,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "LoginView";
        }
        try {
            User authenticated = authenticationHelper.verifyAuthentication(
                    loginDto.getEmail(),
                    loginDto.getPassword()
            );
            session.setAttribute("currentUser", authenticated);
            return "redirect:/";
        } catch (AuthorizationException e) {
            bindingResult.rejectValue("email", "auth_error", e.getMessage());
            bindingResult.rejectValue("password", "auth_error", e.getMessage());
            return "LoginView";
        }
    }


    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("register", new RegisterDto());
        return "RegisterView";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") RegisterDto register,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "RegisterView";
        }

        if (!register.getPassword().equals(register.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
            return "RegisterView";
        }

        try {
            User user = userMapper.fromDto(register);
            userService.create(user);
            return "redirect:/user/login";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("username", "username_error", e.getMessage());
            return "RegisterView";
        }
    }
    @GetMapping("/view")
    public String viewProfile(Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            model.addAttribute("user", currentUser);
            return "UserProfileView";
        } catch (AuthorizationException e) {
            return "redirect:/user/login";
        }
    }
    @GetMapping("/edit")
    public String showEditProfile(Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            UpdateUserDto updateUserDto = userMapper.toUpdateUserDto(currentUser);
            model.addAttribute("user", updateUserDto);
            return "UserEditProfileView";
        } catch (AuthorizationException e) {
            return "redirect:/user/login";
        }
    }
    @PostMapping("/edit")
    public String editProfile(@Valid @ModelAttribute("user") UpdateUserDto updateUserDto,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);

            if (bindingResult.hasErrors()) {
                model.addAttribute("user", updateUserDto);
                return "UserEditProfileView";
            }

            // Validate password confirmation only if password is provided
            if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isEmpty()) {
                // Validate password length
                if (updateUserDto.getPassword().length() < 4 || updateUserDto.getPassword().length() > 32) {
                    bindingResult.rejectValue("password", "password_error", "Password must be between 4 and 32 symbols");
                    model.addAttribute("user", updateUserDto);
                    return "UserEditProfileView";
                }

                // Validate password confirmation matches
                if (!updateUserDto.getPassword().equals(updateUserDto.getPasswordConfirm())) {
                    bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
                    model.addAttribute("user", updateUserDto);
                    return "UserEditProfileView";
                }
            }

            User updatedUser = userMapper.fromDto(updateUserDto);
            updatedUser.setUsername(currentUser.getUsername());
            updatedUser.setId(currentUser.getId());
            userService.updateUserProfile(updatedUser, currentUser);

            session.removeAttribute("currentUser");

            return "redirect:/user/login";
        } catch (AuthorizationException e) {
            return "redirect:/user/login";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("email", "email_error", e.getMessage());
            model.addAttribute("user", updateUserDto);
            return "UserEditProfileView";
        }
    }

}