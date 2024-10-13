package com.demo.folder.repository;

import com.demo.folder.entity.base.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("FROM User u WHERE u.username = :username")
    User findByUsername(@Param("username") String username);

    @Query("FROM User u LEFT JOIN FETCH u.trainee LEFT JOIN FETCH u.trainerS WHERE u.username = :username")
    User findByUsernameWithAssociations(@Param("username") String username);

    @Query("FROM User")
    List<User> findAll();

    @Query("SELECT u.username FROM User u WHERE u.username LIKE :baseUsername%")
    List<String> findUsernamesStartingWith(@Param("baseUsername") String baseUsername);
}