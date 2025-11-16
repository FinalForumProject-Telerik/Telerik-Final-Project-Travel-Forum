package com.example.forum.repositories;

import com.example.forum.exceptions.EntityNotFoundException;
import org.apache.catalina.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> get() {
        try(Session session = this.sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list();
        }
    }

    @Override
    public User get(int id) {
        try(Session session = this.sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null){
                throw new EntityNotFoundException("User", id );
            }
            return user;
        }
    }

    @Override
    public User get(String username) {
        try(Session session = this.sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            List<User> list = query.list();
            if (list.size() == 0){
                throw new EntityNotFoundException("User", "username",  username);
            }
            return list.get(0);
        }
    }

    @Override
    public void create(User user) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        }

    }

    @Override
    public void update(User user) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        }
    }
}
