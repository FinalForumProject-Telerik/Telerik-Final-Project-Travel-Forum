package com.example.forum.controllers.rest;

import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.CommentMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class CommentsRestController {
    private final CommentsService commentsService;
    private final AuthenticationHelper authenticationHelper;
    private final CommentMapper commentMapper;

    public CommentsRestController(CommentsService commentsService, AuthenticationHelper authenticationHelper, CommentMapper commentMapper) {
        this.commentsService = commentsService;
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable int postId) {
        //toDo authentication
        try {
            return commentsService.getCommentsByPostId(postId).stream()
                    .map(commentMapper::toDto)
                    .toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/posts/{postId}/comments")
    public CommentDto addCommentToPost(@PathVariable int postId, @Valid @RequestBody CommentDto commentDto) {
        //toDo authentication
        Comment comment = commentMapper.fromDto(commentDto);
        try {
            return commentMapper.toDto(commentsService.addCommentToPost(postId, comment));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable int commentId) {
        //toDo authentication
        try {
            return commentMapper.toDto(commentsService.getCommentById(commentId));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/comments/{commentId}")
    public CommentDto updateComment(@PathVariable int commentId, @Valid @RequestBody CommentDto commentDto) {
        //toDo authentication
        //toDo authorization only own comments
        Comment comment = commentMapper.fromDto(commentDto);
        try {
            return commentMapper.toDto(commentsService.updateComment(commentId, comment));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable int commentId) {
        //toDo authentication
        //toDo authorization only own comments
        try {
            commentsService.deleteComment(commentId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

