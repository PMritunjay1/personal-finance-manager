package com.syfe.pfm.repository;

import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserIdOrderByDateDesc(Long userId);
    
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    
    boolean existsByCategoryId(Long categoryId);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:categoryName IS NULL OR t.category.name = :categoryName) " +
           "AND (:type IS NULL OR t.type = :type) " +
           "ORDER BY t.date DESC")
    List<Transaction> findFilteredTransactions(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("categoryName") String categoryName,
        @Param("type") CategoryType type
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.date >= :startDate")
    BigDecimal sumAmountByUserIdAndTypeAndDateGreaterThanEqual(
        @Param("userId") Long userId, 
        @Param("type") CategoryType type, 
        @Param("startDate") LocalDate startDate
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND FUNCTION('MONTH', t.date) = :month AND FUNCTION('YEAR', t.date) = :year")
    List<Transaction> findAllByUserIdAndMonthAndYear(
        @Param("userId") Long userId, 
        @Param("month") int month, 
        @Param("year") int year
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND FUNCTION('YEAR', t.date) = :year")
    List<Transaction> findAllByUserIdAndYear(
        @Param("userId") Long userId, 
        @Param("year") int year
    );
}
