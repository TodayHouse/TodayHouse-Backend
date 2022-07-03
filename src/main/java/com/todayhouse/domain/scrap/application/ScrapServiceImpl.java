package com.todayhouse.domain.scrap.application;

import com.todayhouse.domain.scrap.dao.ScrapRepository;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.scrap.exception.ScrapExistException;
import com.todayhouse.domain.scrap.exception.ScrapNotFoundException;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.exception.StoryNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final StoryRepository storyRepository;

    @Override
    public Scrap saveScrap(Long storyId) {
        User user = getValidUser();
        Story story = storyRepository.findById(storyId).orElseThrow(StoryNotFoundException::new);
        scrapRepository.findByUserAndStory(user, story).ifPresent(s -> {
            throw new ScrapExistException();
        });

        Scrap scrap = Scrap.builder().user(user).story(story).build();
        return scrapRepository.save(scrap);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isScraped(Long storyId) {
        Scrap scrap = findScrapNullable(storyId);
        return scrap != null;
    }

    @Override
    public void deleteScrap(Long storyId) {
        Scrap scrap = findScrapNullable(storyId);
        if (scrap == null)
            throw new ScrapNotFoundException();
        scrapRepository.delete(scrap);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countScrapByStoryId(Long storyId) {
        Story story = storyRepository.findById(storyId).orElseThrow(StoryNotFoundException::new);
        return scrapRepository.countByStory(story);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countMyScrap() {
        User user = getValidUser();
        return scrapRepository.countByUser(user);
    }

    private User getValidUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    private Scrap findScrapNullable(Long storyId) {
        User user = getValidUser();
        Story story = storyRepository.findById(storyId).orElseThrow(StoryNotFoundException::new);
        return scrapRepository.findByUserAndStory(user, story).orElse(null);
    }
}
