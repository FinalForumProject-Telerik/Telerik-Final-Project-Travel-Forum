package com.example.forum.helpers;


import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.models.User;
import com.example.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";

    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        try {
            String userInfo = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            String userEmail = getUserEmail(userInfo);
            String password = getPassword(userInfo);
            User user = userService.getByEmail(userEmail);

            if (!user.getPassword().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }

            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    private String getUserEmail(String userInfo) {
        int firstSpace = userInfo.indexOf(" ");
        if (firstSpace == -1) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userInfo.substring(0, firstSpace);
    }

    private String getPassword(String userInfo) {
        int firstSpace = userInfo.indexOf(" ");
        if (firstSpace == -1) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userInfo.substring(firstSpace + 1);
    }

    public User tryGetCurrentUser(HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");

        if (currentUser == null) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userService.getByEmail(currentUser);
    }

    public User verifyAuthentication(String email, String password) {
        try {
            User user = userService.getByEmail(email);
            if (!user.getPassword().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            if (user.isBlocked()) {
                throw new AuthorizationException("User is blocked.");
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    public void requireAdmin(User requester) throws AuthorizationException {
        if (requester == null || !requester.isAdmin()) {
            throw new AuthorizationException("User is not authorized to perform this action.");
        }
    }
}
