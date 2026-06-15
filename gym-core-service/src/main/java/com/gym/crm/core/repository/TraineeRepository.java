package com.gym.crm.core.repository;

import com.gym.crm.core.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    @Query("FROM Trainee t JOIN FETCH t.user WHERE t.user.username = :username")
    Optional<Trainee> findByUserUsername(@Param("username") String username);

    @Query("FROM Trainee t " +
            "JOIN FETCH t.user " +
            "LEFT JOIN FETCH t.trainers tr " +
            "LEFT JOIN FETCH tr.user " +
            "WHERE t.user.username = :username")
    Optional<Trainee> findByUsernameWithTrainers(@Param("username") String username);

    long countByUserIsActive(boolean isActive);
}
