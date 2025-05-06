package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.course.CourseFilePathDTO;
import com.hikers.hikemate.dto.course.CourseForListWithNameDTO;
import com.hikers.hikemate.dto.mountain.*;
import com.hikers.hikemate.entity.Mountain;
import com.hikers.hikemate.repository.MountainRepository;
import com.hikers.hikemate.service.MountainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mountain")
public class MountainController {

    private final MountainService mountainService;
    private final MountainRepository mountainRepository;

    @GetMapping("/list")
    public ResponseEntity<?> mountainGetAll() {
        List<MountainDto> mountainList = mountainService.getAllMountains();

        List<MountainCourseDTO> mountainDtos = mountainList.stream()
                .map(mountain -> new MountainCourseDTO(
                        mountain.getId(),
                        mountain.getMntName(),
                        mountain.getViewCount(),
                        mountain.getCourses().stream()
                                .map(course -> new CourseForListWithNameDTO(
                                        course.getId(),
                                        course.getMountainId(),
                                        course.getCourseFilePath(),
                                        course.getCourseName()
                                )).toList()
                ))
                .toList();

        SuccessResponseDTO<List<MountainCourseDTO>> response =
                new SuccessResponseDTO<>(
                        200,
                        "산 리스트 조회 성공",
                        mountainDtos
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{mnt_id}")
    public ResponseEntity<?> mountainGetById(
            @PathVariable Long mnt_id
    ) {
        // 조회수 증가
        Mountain mountain = mountainRepository.findById(mnt_id).get();
        mountain.setViewCount(mountain.getViewCount() + 1);
        mountainRepository.save(mountain);

        MountainDto mountainDto = mountainService.getMountain(mnt_id);

        MountainWithOutCourseDTO mountainNameDTO = new MountainWithOutCourseDTO(
                mountainDto.getId(),
                mountainDto.getMntName(),
                mountain.getMntInfo(),
                mountainDto.getViewCount()
        );

        SuccessResponseDTO<MountainWithOutCourseDTO> response =
                new SuccessResponseDTO<>(200, "산 상세 조회 성공", mountainNameDTO);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rank")
    public ResponseEntity<?> mountainGetRank() {
        List<Long> targetMountainIds = List.of(
                10L, 46L, 26L, 19L, 23L, 20L, 65L
        );

        List<Mountain> mountains = mountainRepository
                .findByIdInOrderByViewCountDesc(targetMountainIds);

        List<MountainRankDTO> mountainDtos = IntStream.range(0, Math.min(mountains.size(), 5))
                .mapToObj(index -> {
                    Mountain mountain = mountains.get(index);
                    return new MountainRankDTO(
                            mountain.getId(),
                            index + 1,
                            mountain.getMntName(),
                            mountain.getViewCount()
                    );
                })
                .toList();

        SuccessResponseDTO<List<MountainRankDTO>> response =
                new SuccessResponseDTO<>(200, "산 랭킹 조회 성공", mountainDtos);

        return ResponseEntity.ok(response);
    }
}
