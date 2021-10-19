package com.example.demo.repository;

import com.example.demo.enums.Status;
import com.example.demo.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @Query("SELECT a from Application a where " +
            "(:name IS NULL OR a.name = :name) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Application> findByNameOrStatus(
            @Param("name") String name,
            @Param("status") Status status, Pageable pageable);

    @Query("SELECT max(a.uniqueNumber) from Application a")
    Integer selectMaxUniqueNumber();
}
