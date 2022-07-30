package com.todayhouse.domain.likes.dao;


import com.todayhouse.domain.likes.domain.LikesStoryReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface LikesStoryReplyRepository extends JpaRepository<LikesStoryReply, Long> {
    boolean existsByStoryReply_IdAndUser_Id(Long replyId, Long userId);
    long countByStoryReply_Id(Long id);


    @Query(
            "select lsp from LikesStoryReply lsp " +
                    "join fetch lsp.user " +
                    "join fetch lsp.storyReply " +
                    "where lsp.user.email = :email and " +
                    "lsp.storyReply.id =:id"
    )
    Optional<LikesStoryReply> findByIdAndUserEmail(@Param("id") Long id, @Param("email") String email);

    @Query(
            "select lsp.storyReply.id from LikesStoryReply lsp " +
                    "where lsp.user.email = :email"
    )
    Set<Long> findIdsByUserEmail(@Param("email") String email);




}
