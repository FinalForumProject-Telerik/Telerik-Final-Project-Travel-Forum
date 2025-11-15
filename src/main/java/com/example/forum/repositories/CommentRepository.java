package com.example.forum.repositories;

import com.example.forum.models.Comment;

public interface CommentRepository {
    Comment save(Comment comment);

    Comment getCommentById(int commentId);

    Comment save(Comment comment);



    void deleteById(int commentId);
}
