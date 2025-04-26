package com.hikers.hikemate.controller;


import com.hikers.hikemate.entity.ReviewPost;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.ImageService;
import com.hikers.hikemate.service.ReviewPostService;
import com.hikers.hikemate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewPostController {

    private final ReviewPostService reviewPostService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ImageService imageService;

    @Autowired
    public ReviewPostController(ReviewPostService reviewPostService, UserService userService, JwtUtil jwtUtil, ImageService imageService) {
        this.reviewPostService = reviewPostService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.imageService = imageService;

    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> createReviewPost(@RequestHeader("Authorization") String token,
                                              @RequestParam("title") String title,
                                              @RequestParam("content") String content,
                                              @RequestParam("level") String level,
                                              @RequestParam() {}

}
