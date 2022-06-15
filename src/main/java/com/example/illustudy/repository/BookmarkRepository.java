package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Bookmark;

@Repository
public interface BookmarkRepository extends JpaRepository <Bookmark, Long> {
	public Bookmark findByUserIdAndTopicId(Long userId, Long topicId);
	public void deleteByUserIdAndTopicId(long userId, long topicId);
	}