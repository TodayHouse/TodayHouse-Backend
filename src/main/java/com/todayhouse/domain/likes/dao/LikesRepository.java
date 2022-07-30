package com.todayhouse.domain.likes.dao;

import com.todayhouse.domain.likes.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
}
