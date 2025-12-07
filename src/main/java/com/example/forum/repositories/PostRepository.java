package com.example.forum.repositories;

import com.example.forum.models.Post;

import java.util.List;

public interface PostRepository {

    List<Post> getAllPosts();

    Post getPostById(int id);

    Post getPostByIdWithComments(int id);

    void createPost(Post post);

    void updatePost(Post post);

    void deletePostById(int id);

    List<Post> getAllPostsSorted(String sortBy, String order);

    List<Post> getTopCommentedPosts(int limit);
    List<Post> getAllPostsPaged(String sortBy, String order, int page, int pageSize);
    List<Post> getUserPostsPaged(int userId, String sortBy, String order, int page, int pageSize);
    long countPosts();
    long countUserPosts(int userId);
    boolean checkIfPostIsLikedByUser(int postId, int userId);
    void addLikeToPost(int postId, int userId);


}