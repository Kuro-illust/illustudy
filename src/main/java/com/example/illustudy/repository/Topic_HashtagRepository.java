package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Topic_Hashtag;


@Repository
public interface Topic_HashtagRepository extends JpaRepository <Topic_Hashtag, Long> {
	
	 public void deleteByTopicIdAndHashtagId(long topicId, long hashtagId);
	 Topic_Hashtag findByTopicId(Long topicId);
	 Topic_Hashtag findByTopicIdAndHashtagId(long topicId,long hashtagId);
	//Topic_Hashtag findByTagNameOrderByUpdatedAtDesc(Long hashtagId);
	//Hashtag findByHashtagId(Long hashtagId);
	}
