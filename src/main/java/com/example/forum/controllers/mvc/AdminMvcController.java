// java
package com.example.forum.controllers.mvc;

import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.UserMapper;
import com.example.forum.models.User;
import com.example.forum.models.dto.UserDto;
import com.example.forum.services.CommentService;
import com.example.forum.services.PostService;
import com.example.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    public AdminMvcController(UserService userService,
                              PostService postService,
                              CommentService commentService,
                              AuthenticationHelper authenticationHelper,
                              UserMapper userMapper) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public String showUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false, name = "firstName") String firstName,
            Model model,
            HttpSession session) {

        User requester = authenticationHelper.tryGetCurrentUser(session);
        authenticationHelper.requireAdmin(requester);

        List<UserDto> users;
        boolean hasQuery = (username != null && !username.isBlank())
                || (email != null && !email.isBlank())
                || (firstName != null && !firstName.isBlank());

        if (hasQuery) {
            users = userService.searchUsers(username, email, firstName, requester);
        } else {
            users = userService.get(requester)
                    .stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }

        model.addAttribute("users", users);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("firstName", firstName);
        model.addAttribute("currentUser", requester);

        return "AdminUsersView";
    }

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable int id,
                            @RequestParam(required = false) String username,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false, name = "firstName") String firstName,
                            HttpSession session) {
        User requester = authenticationHelper.tryGetCurrentUser(session);
        authenticationHelper.requireAdmin(requester);
        userService.blockUser(id, requester);
        return redirectWithParams(username, email, firstName);
    }

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable int id,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false, name = "firstName") String firstName,
                              HttpSession session) {
        User requester = authenticationHelper.tryGetCurrentUser(session);
        authenticationHelper.requireAdmin(requester);
        userService.unblockUser(id, requester);
        return redirectWithParams(username, email, firstName);
    }

    @PostMapping("/users/{id}/promote")
    public String promoteUser(@PathVariable int id,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false, name = "firstName") String firstName,
                              HttpSession session) {
        User requester = authenticationHelper.tryGetCurrentUser(session);
        authenticationHelper.requireAdmin(requester);
        userService.promoteUser(id, requester);
        return redirectWithParams(username, email, firstName);
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable int id,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false, name = "firstName") String firstName,
                             HttpSession session) {
        User requester = authenticationHelper.tryGetCurrentUser(session);
        authenticationHelper.requireAdmin(requester);
        postService.deletePostById(id, requester);
        return redirectWithParams(username, email, firstName);
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable int id,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false, name = "firstName") String firstName,
                                HttpSession session) {
        User requester = authenticationHelper.tryGetCurrentUser(session);
        authenticationHelper.requireAdmin(requester);
        commentService.deleteComment(id, requester);
        return redirectWithParams(username, email, firstName);
    }

    @PostMapping("/comments/delete")
    public String deleteCommentByParam(@RequestParam(name = "commentId") int commentId,
                                       @RequestParam(required = false) String username,
                                       @RequestParam(required = false) String email,
                                       @RequestParam(required = false, name = "firstName") String firstName,
                                       HttpSession session) {
        User requester = authenticationHelper.tryGetCurrentUser(session);
        authenticationHelper.requireAdmin(requester);
        commentService.deleteComment(commentId, requester);
        return redirectWithParams(username, email, firstName);
    }

    private String redirectWithParams(String username, String email, String firstName) {
        StringBuilder sb = new StringBuilder("redirect:/admin/users");
        String sep = "?";
        if (username != null && !username.isBlank()) {
            sb.append(sep).append("username=").append(urlEncode(username));
            sep = "&";
        }
        if (email != null && !email.isBlank()) {
            sb.append(sep).append("email=").append(urlEncode(email));
            sep = "&";
        }
        if (firstName != null && !firstName.isBlank()) {
            sb.append(sep).append("firstName=").append(urlEncode(firstName));
        }
        return sb.toString();
    }

    private String urlEncode(String s) {
        return s == null ? "" : s.replace(" ", "%20");
    }
}
