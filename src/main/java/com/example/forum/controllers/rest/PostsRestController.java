package com.example.forum.controllers.rest;

import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.PostMapper;
import com.example.forum.models.dto.PostDto;
import com.example.forum.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostsRestController {
    private final PostService postsService;
    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;

    public PostsRestController(PostService postsService, AuthenticationHelper authenticationHelper, PostMapper postMapper) {
        this.postsService = postsService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
    }

    @GetMapping
    public List<PostDto> getAllPosts() {
        return postsService.getAllPosts().stream()
                .map(postMapper::toDto)
                .toList();
    }
    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable int id) {
        try {
            return postMapper.toDto(postsService.getPostById(id));
        }catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @PostMapping
    public PostDto createPost(@Valid @RequestBody PostDto postDto) {
        //toDo authentication
        Post post = postMapper.fromDto(postDto);
        return postMapper.toDto(postsService.createPost(post));

    }
    @PutMapping("/{id}")
    public PostDto updatePost(@PathVariable int id, @Valid @RequestBody PostDto postDto) {
        //toDo authentication
        //toDo authorization only own posts
        Post post = postMapper.fromDto(postDto);
        try {
            return postMapper.toDto(postsService.updatePost(id, post));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable int id) {
        //toDo authentication
        //toDo authorization only own posts
        try {
            postsService.deletePost(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    //toDo like other peoples Posts
    //toDo search Posts by title, content, author by Admin
}
