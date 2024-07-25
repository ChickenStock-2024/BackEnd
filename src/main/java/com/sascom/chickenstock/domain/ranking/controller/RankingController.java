package com.sascom.chickenstock.domain.ranking.controller;

import com.sascom.chickenstock.domain.ranking.dto.response.RankingListResponse;
import com.sascom.chickenstock.domain.ranking.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    private final RankingService rankingService;

    @Autowired
    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/all")
    public ResponseEntity<RankingListResponse> getAllRankingByOffset(
            @RequestParam(name = "offset") Integer offset
    ) {
        if(offset == null) {
            offset = 1;
        }
        if(isValidOffset(offset)) {
            // TODO: IllegalArgumentException -> Custom Exception
            throw new IllegalArgumentException("offset must be greater than zero");
        }
        RankingListResponse rankingListResponse = rankingService.lookUpPaginationRanking(offset);
        return ResponseEntity.ok().body(rankingListResponse);
    }

    @GetMapping("/rival")
    public ResponseEntity<RankingListResponse> getRivalRankingByOffset(
            @RequestParam(name = "offset") Integer offset
    ) {
        if(offset == null) {
            offset = 0;
        }
        if(isValidOffset(offset)) {
            // TODO: IllegalArgumentException -> Custom Exception
            throw new IllegalArgumentException("offset must be greater than zero");
        }

        return ResponseEntity.ok().body(null);
    }

    private boolean isValidOffset(Integer offset) {
        return offset != null && offset > 0;
    }
}
