package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.UserIdNickNameDto;
import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.scrap.ScrapDTO;
import com.hikers.hikemate.dto.scrap.ScrapsByUserDTO;
import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Scrap;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;

    @Transactional
    public Scrap postScrap(User user, Course course) {
        boolean isScrapped = scrapRepository.findByUserAndCourse(user, course).isPresent();

        if (isScrapped) {
            throw new IllegalStateException("이미 스크랩한 코스입니다.");
        }

        Scrap scrap = new Scrap(user, course);
        scrapRepository.save(scrap);

        return scrap;
    }

    @Transactional
    public void deleteScrap(User user, Course course) {
        Optional<Scrap> scrap = scrapRepository.findByUserAndCourse(user, course);

        if (!scrap.isPresent()) {
            throw new IllegalStateException("스크랩하지 않은 코스입니다.");
        }

        scrapRepository.delete(scrap.get());
    }

    @Transactional
    public ScrapsByUserDTO getScrapByUser(User user) {
        List<Scrap> scraps = scrapRepository.findByUser(user);
        UserIdNickNameDto userDto = new UserIdNickNameDto(user.getUserId(), user.getNickname());

        List<ScrapDTO> scrapDTOList = scraps.stream()
                .map(scrap -> new ScrapDTO(
                        scrap.getId(),
                        new CourseDetailDto(
                                scrap.getCourse().getId(),
                                scrap.getCourse().getCourseFilePath(),
                                scrap.getCourse().getCourseName(),
                                scrap.getCourse().getStartName(),
                                scrap.getCourse().getEndName(),
                                scrap.getCourse().getLevel(),
                                scrap.getCourse().getTime(),
                                scrap.getCourse().getMountain().getId()
                        )
                ))
                .collect(Collectors.toList());

        return new ScrapsByUserDTO(userDto, scrapDTOList);

    }
}
