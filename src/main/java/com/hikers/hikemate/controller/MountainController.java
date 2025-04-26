package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.dto.mountain.MountainDto;
import com.hikers.hikemate.service.MountainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        List<MountainDto> mountainList = mountainService.getAllMountains();
        SuccessResponseDTO<List<MountainDto>> response =
                new SuccessResponseDTO<>(200, "산 리스트 조회 성공", mountainList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{mnt_id}")
    public ResponseEntity<?> mountainGetById(
            @PathVariable Long mnt_id
    ) {
        MountainDto mountainDto = mountainService.getMountain(mnt_id);

        SuccessResponseDTO<MountainDto> response =
                new SuccessResponseDTO<>(200, "산 상세 조회 성공", mountainDto);

        return ResponseEntity.ok(response);
    }
}
