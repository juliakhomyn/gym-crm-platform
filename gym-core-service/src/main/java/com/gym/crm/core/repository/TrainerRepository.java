package com.gym.crm.core.repository;

import com.gym.crm.core.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("FROM Trainer t JOIN FETCH t.user WHERE t.user.username = :username")
    Optional<Trainer> findByUserUsername(@Param("username") String username);

    @Query("FROM Trainer t " +
            "JOIN FETCH t.user " +
            "LEFT JOIN FETCH t.trainees trn " +
            "LEFT JOIN FETCH trn.user " +
            "WHERE t.user.username = :username")
    Optional<Trainer> findByUsernameWithTrainees(@Param("username") String username);

    @Query("SELECT DISTINCT t FROM Trainer t " +
            "LEFT JOIN FETCH t.user " +
            "LEFT JOIN FETCH t.trainees tr " +
            "LEFT JOIN FETCH tr.user " +
            "WHERE t NOT IN (" +
            "  SELECT tr2 FROM Trainee trn " +
            "  JOIN trn.trainers tr2 " +
            "  WHERE trn.user.username = :username)")
    List<Trainer> findNotAssignedToTrainee(@Param("username") String traineeUsername);

    long countByUserIsActive(boolean isActive);
}
