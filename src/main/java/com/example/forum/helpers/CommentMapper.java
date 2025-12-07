package com.example.forum.helpers;

import com.example.forum.models.Comment;
import com.example.forum.models.dto.CommentDto;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();

        dto.setUserId(comment.getUser() != null ? comment.getUser().getId() : null);
        dto.setPostId(comment.getPost() != null ? comment.getPost().getId() : null);
        dto.setContent(comment.getContent());
        return dto;
    }

    public Comment fromDto(CommentDto dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        // User and Post will be set by the service/controller layer
        return comment;
    }
}
