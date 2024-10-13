package com.demo.folder.repository;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("FROM Training")
    List<Training> findAll();

    @Query("FROM Training t WHERE t.trainee = :trainee")
    List<Training> findByTrainee(@Param("trainee") Trainee trainee);
}