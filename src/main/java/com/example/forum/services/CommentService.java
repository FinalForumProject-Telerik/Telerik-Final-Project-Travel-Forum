package com.example.forum.services;

import com.example.forum.models.Comment;
import com.example.forum.models.User;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByPostId(int postId);
    Comment addCommentToPost(int postId,Comment comment);
    Comment getCommentById(int commentId);
    Comment updateComment(int commentId,Comment comment, User user);
    void deleteComment(int commentId, User user);


}
