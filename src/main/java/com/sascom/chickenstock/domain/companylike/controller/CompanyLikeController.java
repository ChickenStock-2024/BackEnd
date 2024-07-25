package com.sascom.chickenstock.domain.companylike.controller;

import com.sascom.chickenstock.domain.companylike.dto.response.CompanyLikeResponse;
import com.sascom.chickenstock.domain.companylike.service.CompanyLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companyLike")
public class CompanyLikeController {

    private CompanyLikeService companyLikeService;

    @Autowired
    CompanyLikeController(CompanyLikeService companyLikeService) {
        this.companyLikeService = companyLikeService;
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyLikeResponse> makeLikeRelationship(@PathVariable("companyId") Long companyId, Long memberId) {
        CompanyLikeResponse response = companyLikeService.makeLikeRelationship(companyId, memberId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<CompanyLikeResponse> removeLikeRelationship(@PathVariable("companyId") Long companyId, Long memberId) {
        CompanyLikeResponse response = companyLikeService.removeLikeRelationship(companyId, memberId);
        return ResponseEntity.ok().body(response);
    }

}
