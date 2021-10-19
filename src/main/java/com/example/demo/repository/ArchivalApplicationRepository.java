package com.example.demo.repository;

import com.example.demo.model.ArchivalApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivalApplicationRepository extends JpaRepository<ArchivalApplication, Long> {
    List<ArchivalApplication> findByApplicationId(Long applicationId);
}
