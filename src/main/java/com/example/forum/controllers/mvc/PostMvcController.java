package com.example.forum.controllers.mvc;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.CommentMapper;
import com.example.forum.helpers.PostMapper;
import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.dto.CommentDto;
import com.example.forum.models.dto.PostDto;
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

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostMvcController {
    private final PostService postService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final PostMapper postMapper;

    public PostMvcController(PostService postService, UserService userService, AuthenticationHelper authenticationHelper, CommentMapper commentMapper, CommentService commentService, PostMapper postMapper) {
        this.postService = postService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
        this.postMapper = postMapper;
    }
    @GetMapping
    public String showAllPosts(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "false") boolean ownPosts,
            HttpSession session,
            Model model) {

        int pageSize = 10;
        User currentUser = null;

        // Try to get current user if logged in
        try {
            currentUser = authenticationHelper.tryGetCurrentUser(session);
            model.addAttribute("currentUser", currentUser);
        } catch (AuthorizationException e) {
            // User not logged in
        }

        List<Post> posts;
        long totalPosts;
        int totalPages;

        // If ownPosts is true and user is logged in, filter by user's posts
        if (ownPosts && currentUser != null) {
            totalPosts = postService.getTotalUserPosts(currentUser.getId());
            totalPages = (int) Math.ceil((double) totalPosts / pageSize);
            posts = postService.getUserPostsPaged(currentUser.getId(), sortBy, order, page, pageSize);
        } else {
            totalPosts = postService.getTotalPosts();
            totalPages = (int) Math.ceil((double) totalPosts / pageSize);
            posts = postService.getAllPostsPaged(sortBy, order, page, pageSize);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("ownPosts", ownPosts);

        return "PostsView";
    }
    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable int id, Model model, HttpSession session) {
        try {
            Post post = postService.getPostByIdWithComments(id);
            model.addAttribute("post", post);
            model.addAttribute("comment", new CommentDto());

            // Add current user to model if logged in
            try {
                User currentUser = authenticationHelper.tryGetCurrentUser(session);
                model.addAttribute("currentUser", currentUser);
            } catch (AuthorizationException e) {
                // User not logged in, continue without currentUser
            }

            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/new")
    public String showCreatePost(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/user/login";
        }
        PostDto postDto = new PostDto();

        model.addAttribute("post", postDto);
        return "PostCreateView";
    }

    @PostMapping("/new")
    public String createPost(@Valid @ModelAttribute("post") PostDto postDto,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/user/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", postDto);
            return "PostCreateView";
        }

        Post post = postMapper.fromDto(postDto);
        postService.createPost(post, user);
        
        return "redirect:/posts/" + post.getId();
    }



    @PostMapping("/{id}/comment")
    public String addCommentToPost(@PathVariable int id, @Valid @ModelAttribute("comment") CommentDto commentDto,
                                   BindingResult bindingResult, HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/user/login";
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
    @GetMapping("/{id}/edit")
    public String showEditPost(@PathVariable int id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/user/login";
        }

        try {
            Post post = postService.getPostById(id);

            model.addAttribute("post", postMapper.toDto(post));

        } catch (EntityNotFoundException | AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
        return "PostEditView";
    }
    @PostMapping("/{id}/edit")
    public String EditPost(@PathVariable int id, Model model, HttpSession session, @Valid @ModelAttribute("post") PostDto post,
                           BindingResult bindingResult){
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
            if (bindingResult.hasErrors()) {
                model.addAttribute("post", post);
                return "PostEditView";
            }
            Post updatedPost = postMapper.fromDto(post);

            postService.updatePost(updatedPost, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException | AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable int id, HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
            postService.deletePostById(id, user);
            return "redirect:/posts";
        } catch (EntityNotFoundException | AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }
    @PostMapping("/{id}/like")
    public String likePost(@PathVariable int id, HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/user/login";
        }

        try {
            postService.incrementPostLikes(id, user);
            return "redirect:/posts/" + id;
        } catch (AuthorizationException e) {
            // User already liked this post - show error on the same page
            Post post = postService.getPostByIdWithComments(id);
            model.addAttribute("post", post);
            model.addAttribute("comment", new CommentDto());
            model.addAttribute("currentUser", user);
            model.addAttribute("likeError", "You have already liked this post");
            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


}
