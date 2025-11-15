package com.example.forum.services;

import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.repositories.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CommentServiceImpl implements CommentService {
    private final PostService postService;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(PostService postService, CommentRepository commentRepository) {
        this.postService = postService;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public List<Comment> getCommentsByPostId(int postId) {
       Post post  = postService.getPostById(postId);

        Set<Comment> comments = post.getComments();
        if (comments == null || comments.isEmpty()){
            throw new EntityNotFoundException("No comments for this post");
        }
        return new ArrayList<>(comments);
    }

    @Override
    @Transactional
    public Comment addCommentToPost(int postId, Comment comment) {

        Post post  = postService.getPostById(postId);
        comment.setPost(post);

        Comment saved = commentRepository.save(comment);
        post.getComments().remove(comment);
        post.getComments().add(saved);

        return saved;

    }

    @Override
    @Transactional
    public Comment getCommentById(int commentId) {
        return commentRepository.getCommentById(commentId);
    }

    @Override
    @Transactional
    public Comment updateComment(int commentId, Comment comment) {
        //toDo authorization
        Comment commentCurrent = commentRepository.getCommentById(commentId);
        commentCurrent.setContent(comment.getContent());
        return commentRepository.save(comment);

    }

    @Override
    @Transactional
    public void deleteComment(int commentId) {
        //toDo authorizatrion
        Comment commentCurrent = commentRepository.getCommentById(commentId);
        Post post = commentCurrent.getPost();
        if (post != null) {
            post.getComments().remove(commentCurrent);
        }
        commentRepository.deleteById(commentId);

    }
}
