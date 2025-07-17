package com.englishscenario.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 词汇记录实体类
 * 用于存储用户查询的场景和生成的词汇
 */
@Entity  // 标记这是一个JPA实体
@Table(name = "vocabulary_records")  // 对应数据库表名
@Data  // Lombok注解，自动生成getter/setter等方法
@NoArgsConstructor  // 生成无参构造函数
@AllArgsConstructor  // 生成全参构造函数
public class VocabularyRecord {

    @Id  // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增ID
    private Long id;

    @Column(nullable = false)
    private String scenario;  // 场景描述

    @Column(nullable = false, length = 20)
    private String language;  // 语言类型 (english/japanese)

    @Column(columnDefinition = "TEXT")  // 长文本类型
    private String vocabularyJson;  // 存储词汇的JSON字符串

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // 关联用户

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 创建时间

    /**
     * 在保存之前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}