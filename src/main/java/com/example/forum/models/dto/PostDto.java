package com.example.forum.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostDto {

    private Integer id;

    // validation for userId might not be needed as it would be added from session but might be
    //added if needed
    private Integer userId;

    @NotBlank(message = "Title is required")
    @Size(min = 16, max = 64, message = "Title must be between 16 and 64 symbols")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 32, max = 8192, message = "Content must be between 32 and 8192 symbols")
    private String content;

    public PostDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

