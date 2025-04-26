package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.mountain.MountainDto;
import com.hikers.hikemate.entity.Mountain;
import com.hikers.hikemate.repository.MountainRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MountainService {

    private final MountainRepository mountainRepository;

    public MountainService(MountainRepository mountainRepository) {
        this.mountainRepository = mountainRepository;
    }

    public List<MountainDto> getAllMountains() {
        List<Mountain> mountains = mountainRepository.findAll();

        return mountains.stream()
                .map(this::generateCoursesDto)
                .collect(Collectors.toList());
    }

    public MountainDto getMountain(Long mnt_id) {

        return generateCoursesDto(mountainRepository.findById(mnt_id).get());
    }

    private MountainDto generateCoursesDto(Mountain mountain) {
        return new MountainDto(
                mountain.getId(),
                mountain.getMntName(),
                mountain.getMntInfo(),
                mountain.getViewCount(),
                mountain.getCourses().stream()
                        .map(course -> new CourseDetailDto(
                                course.getId(),
                                course.getCourseFilePath(),
                                course.getCourseName(),
                                course.getStartName(),
                                course.getEndName(),
                                course.getLevel(),
                                course.getTime()
                        ))
                        .collect(Collectors.toList())
        );
    }
}