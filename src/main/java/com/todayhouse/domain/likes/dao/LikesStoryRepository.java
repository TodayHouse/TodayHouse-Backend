package com.todayhouse.domain.likes.dao;

import com.todayhouse.domain.likes.domain.LikesStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesStoryRepository extends JpaRepository<LikesStory, Long> {
    boolean existsByUser_EmailAndStory_Id(String email, Long id);

    @Query("select ls from LikesStory ls " +
            "join fetch ls.story s " +
            "join fetch ls.user u " +
            "where s.id = :storyId and u.id = :userId")
    Optional<LikesStory> findByUser_IdAndStory_Id(@Param("userId") Long userId,@Param("storyId") Long storyId);

    @Query("select ls from LikesStory ls " +
            "join fetch ls.story s " +
            "join fetch ls.user u " +
            "where s.id = :storyId and u.email = :email")
    Optional<LikesStory> findByUser_EmailAndStory_Id(@Param("email") String email,@Param("storyId") Long storyId);


    long countByStory_Id(Long id);

}
