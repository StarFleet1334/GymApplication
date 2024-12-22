package com.demo.folder.repository;

import com.demo.folder.entity.base.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("FROM Trainer t WHERE t.user.username = :username")
    Trainer findByUsername(@Param("username") String username);

    @Query("FROM Trainer")
    List<Trainer> findAll();

    default void updateTrainerStatus(Long trainerId, boolean status) {
        findById(trainerId).ifPresent(trainer -> {
            trainer.getUser().setActive(status);
            save(trainer);
        });
    }

}