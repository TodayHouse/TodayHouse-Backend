package com.todayhouse.domain.story.dao;

import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {

    Slice<Story> findAllByOrderByIdDesc(Pageable pageable);

    Slice<Story> findAllByUser(User user);
}
