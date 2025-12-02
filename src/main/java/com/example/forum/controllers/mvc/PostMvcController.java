package com.example.forum.controllers.mvc;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.CommentMapper;
import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.dto.CommentDto;
import com.example.forum.services.CommentService;
import com.example.forum.services.PostService;
import com.example.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostMvcController {
    private final PostService postService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;

    public PostMvcController(PostService postService, UserService userService, AuthenticationHelper authenticationHelper, CommentMapper commentMapper, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
    }
    @GetMapping
    public String showAllPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "PostsView";
    }
    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable int id, Model model) {
        try {
            Post post = postService.getPostByIdWithComments(id);
            model.addAttribute("post", post);
            model.addAttribute("comment", new CommentDto());
            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
    @PostMapping("/{id}/comment")
    public String addCommentToPost(@PathVariable int id, @Valid @ModelAttribute("comment") CommentDto commentDto,
                                   BindingResult bindingResult, HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            try {
                Post post = postService.getPostByIdWithComments(id);
                model.addAttribute("post", post);
                model.addAttribute("comment", commentDto);
            } catch (EntityNotFoundException e) {
                model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
                model.addAttribute("error", e.getMessage());
                return "ErrorView";
            }
            return "PostView";
        }
        try {
            Comment comment = commentMapper.fromDto(commentDto);
            Comment addedComment = commentService.addCommentToPost(id, comment, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


}
