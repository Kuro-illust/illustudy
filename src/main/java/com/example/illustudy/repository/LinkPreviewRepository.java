package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.LinkPreview;


@Repository
public interface LinkPreviewRepository extends JpaRepository <LinkPreview, Long> {
	LinkPreview findByUrl(String url);
	LinkPreview findByLinkPreviewId(Long linkPreviewId);
	//Hashtag findByTagNameOrderByUpdatedAtDesc(String tagName);
	//Hashtag findByHashtagId(Long hashtagId);
	}
