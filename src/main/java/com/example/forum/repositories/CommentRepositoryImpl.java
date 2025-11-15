package com.example.forum.repositories;

import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.models.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepositoryImpl implements CommentRepository{
    private final SessionFactory sessionFactory;

    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Comment getCommentById(int commentId) {
        Session session = sessionFactory.getCurrentSession();
        Comment comment = session.get(Comment.class, commentId);
        if (comment == null){
            throw new EntityNotFoundException("Comment", commentId);
        }
        return comment;
    }

    @Override
    public Comment save(Comment comment) {
        Session session = sessionFactory.getCurrentSession();
        Comment managed = (Comment) session.merge(comment);
        return managed;
    }

    @Override
    public void deleteById(int commentId) {
      Session session = sessionFactory.getCurrentSession();
      Comment comment = getCommentById(commentId);
      session.remove(comment);

    }
}
