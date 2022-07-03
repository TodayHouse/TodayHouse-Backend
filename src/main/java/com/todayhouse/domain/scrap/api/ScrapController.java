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

    @PostMapping("/{productId}")
    public BaseResponse<Long> saveScrap(@PathVariable Long productId) {
        Scrap scrap = scrapService.saveScrap(productId);
        return new BaseResponse(scrap.getId());
    }

    @GetMapping("/{productId}/exist")
    public BaseResponse<Boolean> isScraped(@PathVariable Long productId) {
        Boolean scraped = scrapService.isScraped(productId);
        return new BaseResponse(scraped);
    }

    @DeleteMapping("/{productId}")
    public BaseResponse<String> deleteScrap(@PathVariable Long productId) {
        scrapService.deleteScrap(productId);
        return new BaseResponse("삭제되었습니다.");
    }

    @GetMapping("/{productId}/count")
    public BaseResponse<Long> countProductScrap(@PathVariable Long productId) {
        Long count = scrapService.countScrapByProductId(productId);
        return new BaseResponse(count);
    }

    @GetMapping("/my/count")
    public BaseResponse<Long> countMyScrap() {
        Long count = scrapService.countMyScrap();
        return new BaseResponse(count);
    }
}
