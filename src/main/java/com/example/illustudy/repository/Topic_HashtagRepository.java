package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Topic_Hashtag;


@Repository
public interface Topic_HashtagRepository extends JpaRepository <Topic_Hashtag, Long> {
	//Hashtag findByTagNameOrderByUpdatedAtDesc(String tagName);
	//Hashtag findByHashtagId(Long hashtagId);
	}
