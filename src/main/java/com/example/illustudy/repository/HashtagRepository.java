package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Hashtag;


@Repository
public interface HashtagRepository extends JpaRepository <Hashtag, Long> {
	Hashtag findByTagName(String tagName);
	Hashtag findByHashtagId(Long hashtagId);
	//Hashtag findByTagNameOrderByUpdatedAtDesc(String tagName);
	//Hashtag findByHashtagId(Long hashtagId);
	}
