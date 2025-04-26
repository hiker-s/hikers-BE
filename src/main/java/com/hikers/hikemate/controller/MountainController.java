package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.mountain.MountainGetAllResponseDto;
import com.hikers.hikemate.service.MountainService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mountain")
public class MountainController {

    private final MountainService mountainService;

    public  MountainController(MountainService mountainService) {
        this.mountainService = mountainService;
    }

    @GetMapping("/list")
    public List<MountainGetAllResponseDto> mountainGetAllResponseDtos() {
        return mountainService.getAllMountains();
    }
}
