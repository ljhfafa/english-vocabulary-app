package com.englishscenario.backend.repository;

import com.englishscenario.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 统计时间段内的新用户数
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 统计某个时间之后的新用户数
     */
    long countByCreatedAtAfter(LocalDateTime date);

    /**
     * 统计活跃用户数（最近登录）
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u WHERE u.lastLogin > :date")
    long countActiveUsersSince(@Param("date") LocalDateTime date);
}