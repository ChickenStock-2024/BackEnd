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

    @GetMapping
    public ResponseEntity<CompanyLikeResponse> makeLikeRelationship(@RequestParam Long companyId) {
        CompanyLikeResponse response = companyLikeService.makeLikeRelationship(companyId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<CompanyLikeResponse> removeLikeRelationship(@RequestParam Long companyId) {
        CompanyLikeResponse response = companyLikeService.removeLikeRelationship(companyId);
        return ResponseEntity.ok().body(response);
    }

}
