package com.hikers.hikemate.dto.scrap;

import com.hikers.hikemate.dto.UserIdNickNameDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScrapsByUserDTO {
    private UserIdNickNameDto user;
    private List<ScrapDTO> scraps;
}