package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.mountain.MountainGetAllResponseDto;
import com.hikers.hikemate.entity.Mountain;
import com.hikers.hikemate.repository.MountainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
                .map(mountain -> new MountainGetAllResponseDto(
                        mountain.getId(),
                        mountain.getMntName(),
                        mountain.getMntInfo(),
                        mountain.getCourses().stream()
                                .map(course -> new CourseDetailDto(
                                        course.getId(),
                                        course.getCourseName(),
                                        course.getCourseFilePath()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}