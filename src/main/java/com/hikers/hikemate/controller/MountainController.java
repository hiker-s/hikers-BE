package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.dto.mountain.MountainGetAllResponseDto;
import com.hikers.hikemate.service.MountainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> mountainGetAll() {
        List<MountainGetAllResponseDto> mountainList = mountainService.getAllMountains();
        SuccessResponseDTO<List<MountainGetAllResponseDto>> response =
                new SuccessResponseDTO<>(200, "산 리스트 조회 성공", mountainList);

        return ResponseEntity.ok(response);
    }
}
