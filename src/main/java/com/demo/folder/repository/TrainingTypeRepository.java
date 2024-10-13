package com.demo.folder.repository;

import com.demo.folder.entity.base.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    @Query("FROM TrainingType")
    List<TrainingType> findAll();

    Optional<TrainingType> findById(Long id);

    @Query("FROM TrainingType WHERE trainingTypeName = :name")
    TrainingType findByName(@Param("name") String name);
}