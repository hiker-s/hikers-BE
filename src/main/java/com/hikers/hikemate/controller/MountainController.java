package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.mountain.MountainGetAllResponseDto;
import com.hikers.hikemate.service.MountainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mountain")
public class MountainController {

    private final MountainService mountainService;

    @GetMapping("/list")
    public List<MountainGetAllResponseDto> mountainGetAll() {
        return mountainService.getAllMountains();
    }
}
