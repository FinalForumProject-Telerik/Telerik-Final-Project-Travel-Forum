package com.example.forum.controllers.mvc;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityDuplicateException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.UserMapper;
import com.example.forum.models.User;
import com.example.forum.models.dto.LoginDto;
import com.example.forum.models.dto.RegisterDto;
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
            authenticationHelper.verifyAuthentication(loginDto.getEmail(), loginDto.getPassword());
            session.setAttribute("currentUser", loginDto.getEmail());
            return "redirect:/";
        } catch (
                AuthorizationException e) {
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

}