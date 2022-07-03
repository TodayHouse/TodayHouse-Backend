package com.todayhouse.domain.scrap.api;


import com.todayhouse.domain.scrap.application.ScrapService;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scraps")
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;

    @PostMapping("/{storyId}")
    public BaseResponse<Long> saveScrap(@PathVariable Long storyId) {
        Scrap scrap = scrapService.saveScrap(storyId);
        return new BaseResponse(scrap.getId());
    }

    @GetMapping("/{storyId}/exist")
    public BaseResponse<Boolean> isScraped(@PathVariable Long storyId) {
        Boolean scraped = scrapService.isScraped(storyId);
        return new BaseResponse(scraped);
    }

    @DeleteMapping("/{storyId}")
    public BaseResponse<String> deleteScrap(@PathVariable Long storyId) {
        scrapService.deleteScrap(storyId);
        return new BaseResponse("삭제되었습니다.");
    }

    @GetMapping("/{storyId}/count")
    public BaseResponse<Long> countStoryScrap(@PathVariable Long storyId) {
        Long count = scrapService.countScrapByStoryId(storyId);
        return new BaseResponse(count);
    }

    @GetMapping("/my/count")
    public BaseResponse<Long> countMyScrap() {
        Long count = scrapService.countMyScrap();
        return new BaseResponse(count);
    }
}
