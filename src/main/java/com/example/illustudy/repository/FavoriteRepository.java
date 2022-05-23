package com.example.illustudy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.illustudy.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    public Optional<Favorite> findByfavoriteId(String favoriteId);

    public List<Favorite> findByUserIdOrderByUpdatedAtDesc(Long userId);

    public List<Favorite> findByUserIdAndTopicId(Long userId, Long topicId);

    public void deleteByUserIdAndTopicId(long userId, long topicId);
}