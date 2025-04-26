package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.mountain.MountainGetAllResponseDto;
import com.hikers.hikemate.entity.Mountain;
import com.hikers.hikemate.repository.MountainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MountainService {

    private final MountainRepository mountainRepository;

    public MountainService(MountainRepository mountainRepository) {
        this.mountainRepository = mountainRepository;
    }

    public List<MountainGetAllResponseDto> getAllMountains() {
        List<Mountain> mountains = mountainRepository.findAll();

        return mountains.stream()
                .map(this::generateCoursesDto)
                .collect(Collectors.toList());
    }

    public MountainGetAllResponseDto getMountain(Long mnt_id) {

        return generateCoursesDto(mountainRepository.findById(mnt_id).get());
    }

    private MountainGetAllResponseDto generateCoursesDto(Mountain mountain) {
        return new MountainGetAllResponseDto(
                mountain.getId(),
                mountain.getMntName(),
                mountain.getMntInfo(),
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