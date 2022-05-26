package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Comment;
import com.example.illustudy.entity.Topic;

@Repository
public interface CommentRepository extends JpaRepository <Comment, Long> {
	Iterable<Comment> findByTopicIdOrderByUpdatedAtDesc(Long topicId);
	Comment findByCommentId(Long commentId);
	
	}
