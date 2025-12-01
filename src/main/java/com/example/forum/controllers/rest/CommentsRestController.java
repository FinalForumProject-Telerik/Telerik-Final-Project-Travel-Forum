package com.example.forum.controllers.rest;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.CommentMapper;
import com.example.forum.models.Comment;
import com.example.forum.models.User;
import com.example.forum.models.dto.CommentDto;
import com.example.forum.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentsRestController {
    private final CommentService commentsService;
    private final AuthenticationHelper authenticationHelper;
    private final CommentMapper commentMapper;

    public CommentsRestController(CommentService commentsService, AuthenticationHelper authenticationHelper, CommentMapper commentMapper) {
        this.commentsService = commentsService;
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable int postId,@RequestHeader HttpHeaders headers) {

        try {
            User user = authenticationHelper.tryGetUser(headers);
            return commentsService.getCommentsByPostId(postId).stream()
                    .map(commentMapper::toDto)
                    .toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/posts/{postId}/comments")
    public CommentDto addCommentToPost(@PathVariable int postId, @Valid @RequestBody CommentDto commentDto,
                                       @RequestHeader HttpHeaders headers) {

        Comment comment = commentMapper.fromDto(commentDto);
        try {
            User user = authenticationHelper.tryGetUser(headers);
            comment.setUser(user);
            return commentMapper.toDto(commentsService.addCommentToPost(postId, comment, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
        @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable int commentId, @RequestHeader HttpHeaders headers) {

        try {
            User user = authenticationHelper.tryGetUser(headers);
            return commentMapper.toDto(commentsService.getCommentById(commentId));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/comments/{commentId}")
    public CommentDto updateComment(@PathVariable int commentId, @Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader HttpHeaders headers) {
        Comment comment = commentMapper.fromDto(commentDto);
        try {
            User user = authenticationHelper.tryGetUser(headers);
            comment.setUser(user);
            return commentMapper.toDto(commentsService.updateComment(commentId, comment, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable int commentId, @RequestHeader HttpHeaders headers) {

        try {
            User user = authenticationHelper.tryGetUser(headers);
            commentsService.deleteComment(commentId, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}

