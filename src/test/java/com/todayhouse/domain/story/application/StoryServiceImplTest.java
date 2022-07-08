package com.todayhouse.domain.story.application;

import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.dto.reqeust.StorySearchRequest;
import com.todayhouse.domain.story.dto.response.StoryGetListResponse;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoryServiceImplTest {
    @InjectMocks
    StoryServiceImpl storyService;
    @Mock
    StoryRepository storyRepository;
    @Mock
    UserRepository userRepository;

    @AfterEach
    void clear(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void searchStory() {
        StorySearchRequest mockRequest = mock(StorySearchRequest.class);
        Pageable mockPageable = mock(Pageable.class);
        Page<Story> storyPage = mock(Page.class);
        List<Story> stories = mock(List.class);
        List<Story> scrapedStories = mock(List.class);
        User mockUser = mock(User.class);
        Page<StoryGetListResponse> result = mock(Page.class);

        setSecurityName("test");
        when(storyRepository.searchCondition(mockRequest, mockPageable)).thenReturn(storyPage);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(mockUser));
        when(storyPage.getContent()).thenReturn(stories);
        when(storyRepository.findScrapedByStoriesAndUser(stories, mockUser)).thenReturn(scrapedStories);
        doNothing().when(scrapedStories).forEach(any(Consumer.class));
        when(storyPage.map(any(Function.class))).thenReturn(result);

        Page<StoryGetListResponse> responses = storyService.searchStory(mockRequest, mockPageable);

        assertThat(responses).isEqualTo(result);
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }
}