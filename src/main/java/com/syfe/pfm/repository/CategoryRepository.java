package com.syfe.pfm.repository;

import com.syfe.pfm.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId OR c.isCustom = false")
    List<Category> findAllByUserIdOrNotCustom(@Param("userId") Long userId);

    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.isCustom = false) AND c.name = :name")
    Optional<Category> findByNameAndUserIdOrNotCustom(@Param("name") String name, @Param("userId") Long userId);

    Optional<Category> findByNameAndUserId(String name, Long userId);
    
    boolean existsByNameAndUserId(String name, Long userId);
}
