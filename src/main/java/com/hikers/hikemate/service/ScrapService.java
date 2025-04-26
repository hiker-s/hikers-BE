package com.hikers.hikemate.service;

import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Scrap;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
