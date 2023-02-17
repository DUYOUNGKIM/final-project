package com.project.sparta.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CommunityController {
    // TODO 어드민 Community API 제작
    @GetMapping("/boards/communities")
    public void getCommunityList(){

    }

    @GetMapping("/boards/communities/{boardId}")
    public void getCommunity(){

    }

    @PatchMapping("/boards/communities/{boardId}")
    public void updateCommunity(){

    }

    @DeleteMapping("/boards/communities/{boardId}")
    public void deleteCommunity(){

    }
}