package com.todayhouse.domain.scrap.dao;

import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomScrapRepository {
    Page<Scrap> findScrapWithStoryByUser(Pageable pageable, User user);
}
