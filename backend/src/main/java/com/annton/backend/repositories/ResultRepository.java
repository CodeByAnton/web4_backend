package com.annton.backend.repositories;

import com.annton.backend.entities.Result;
import com.annton.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findAllByUser(User user);
    void deleteAllByUser(User user);
}
