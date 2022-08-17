package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Iterable<Resource> findAllByOrderByUpdatedAtDesc();
    Resource findByResourceId(Long resourceId);
    Iterable<Resource> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
}