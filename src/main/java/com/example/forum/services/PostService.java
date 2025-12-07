package com.example.forum.services;

import com.example.forum.models.Post;
import com.example.forum.models.User;

import java.util.List;

public interface PostService {

    List<Post> getAllPosts();

    Post getPostById(int id);

    Post getPostByIdWithComments(int id);

    void createPost(Post post, User user);

    void updatePost(Post post, User user);

    void deletePostById(int id, User user);

    void incrementPostLikes(int id, User user);

    List<Post> getAllPostsSorted(String sortBy, String order);

    List<Post> getTopCommentedPosts(int limit);
    public List<Post> getAllPostsPaged(String sortBy, String order, int page, int pageSize);
    public List<Post> getUserPostsPaged(int userId, String sortBy, String order, int page, int pageSize);
    public long getTotalPosts();
    public long getTotalUserPosts(int userId);
}
