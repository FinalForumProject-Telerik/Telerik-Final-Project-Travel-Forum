package com.example.forum.helpers;

import com.example.forum.models.Post;
import com.example.forum.models.dto.PostDto;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setUserId(post.getUser() != null ? post.getUser().getId() : null);
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        return dto;
    }

    public Post fromDto(PostDto dto) {
        Post post = new Post();
        post.setId(dto.getId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        // User will be set by the service/controller layer
        return post;
    }
}
