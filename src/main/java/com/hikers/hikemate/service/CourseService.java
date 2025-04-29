package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.course.CourseDetailWithScrapDTO;
import com.hikers.hikemate.dto.review.CardReviewDTO;
import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.ReviewPost;
import com.hikers.hikemate.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course findCourseById(Integer courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다."));
    }

    public List<CourseDetailWithScrapDTO> getCourseBySort(String sortType, Long mntId, Long memberId) {
        List<Course> courses;

        if ("abc".equals(sortType)) {
            courses = courseRepository.findByMountainIdOrderByTitleAsc(mntId);
        } else if ("level".equals(sortType)) {
            courses = courseRepository.findByMountainIdOrderByLevelAsc(mntId);
        } else if ("review".equals(sortType)) {
            courses = courseRepository.findByMountainIdOrderByReviewCountDesc(mntId);
        } else if ("scrap".equals(sortType)) {
            courses = courseRepository.findByMountainIdOrderByScrapCountDesc(mntId);
        } else {
            throw new IllegalArgumentException("정렬 타입이 잘못되었습니다.");
        }

        return courses.stream()
                .map(course -> new CourseDetailWithScrapDTO(
                        course.getId(),
                        course.getCourseFilePath(),
                        course.getCourseName(),
                        course.getStartName(),
                        course.getEndName(),
                        course.getLevel(),
                        course.getTime(),
                        course.getMountain().getId(),
                        isCourseScrappedByMember(course, memberId)
                ))
                .collect(Collectors.toList());
    }

    // 스크랩 여부
    private boolean isCourseScrappedByMember(Course course, Long memberId) {
        if (course.getScraps() == null) return false;
        return course.getScraps().stream()
                .anyMatch(scrap -> scrap.getUser().getId().equals(memberId));
    }
}
