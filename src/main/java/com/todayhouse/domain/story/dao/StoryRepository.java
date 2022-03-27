package com.todayhouse.domain.story.dao;

import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {

    Slice<Story> findAllByOrderById(Pageable pageable);

    Slice<Story> findAllByUser(User user, Pageable pageable);
}
