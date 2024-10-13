package com.demo.folder.repository;

import com.demo.folder.entity.base.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee,Long> {

    @Query("FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.user.username = :username")
    Trainee findByUsername(String username);

    @Query("FROM Trainee")
    List<Trainee> findAll();

    default void updateTraineeStatus(Long traineeId, boolean status) {
        findById(traineeId).ifPresent(trainee -> {
            trainee.getUser().setActive(status);
            save(trainee);
        });
    }

}