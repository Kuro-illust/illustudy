package com.example.illustudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Bookmark_Hashtag;
import com.example.illustudy.entity.Topic;

@Repository
public interface Bookmark_HashtagRepository extends JpaRepository <Bookmark_Hashtag, Long> {
	
	 public void deleteByBookmarkIdAndHashtagId(long bookmarkId, long hashtagId);
	 Iterable<Bookmark_Hashtag> findByBookmarkId(Long bookmarkId);
	 Bookmark_Hashtag findByBookmarkIdAndHashtagId(Long bookmarkId,Long hashtagId);
	 public void deleteByBookmarkId(long bookmarkId);
	}
