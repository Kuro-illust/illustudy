package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Iterable<Topic> findAllByOrderByUpdatedAtDesc();
    Topic findByTopicId(Long topicId);
    Iterable<Topic> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
    
}