package com.englishscenario.backend.repository;

import com.englishscenario.backend.entity.VocabularyRecord;
import com.englishscenario.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 词汇记录数据访问层
 */
@Repository
public interface VocabularyRecordRepository extends JpaRepository<VocabularyRecord, Long> {

    /**
     * 根据用户查找记录（分页）
     */
    Page<VocabularyRecord> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * 根据用户查找最近的记录
     */
    List<VocabularyRecord> findTop10ByUserOrderByCreatedAtDesc(User user);

    /**
     * 统计用户的查询次数
     */
    long countByUser(User user);

    /**
     * 根据用户和语言查找记录
     */
    Page<VocabularyRecord> findByUserAndLanguageOrderByCreatedAtDesc(User user, String language, Pageable pageable);

    /**
     * 获取最热门的查询场景
     */
    @Query("SELECT v.scenario, COUNT(v) as count FROM VocabularyRecord v " +
            "WHERE v.createdAt > :startDate " +
            "GROUP BY v.scenario " +
            "ORDER BY count DESC")
    List<Object[]> findTopScenarios(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    /**
     * 按日期统计查询量
     */
    @Query("SELECT DATE(v.createdAt) as date, COUNT(v) as count " +
            "FROM VocabularyRecord v " +
            "WHERE v.createdAt > :startDate " +
            "GROUP BY DATE(v.createdAt) " +
            "ORDER BY date ASC")
    List<Object[]> findDailyQueryCount(@Param("startDate") LocalDateTime startDate);

    /**
     * 统计语言使用情况
     */
    @Query("SELECT v.language, COUNT(v) FROM VocabularyRecord v " +
            "GROUP BY v.language")
    List<Object[]> findLanguageStatistics();
}