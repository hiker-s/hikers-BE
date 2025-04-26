package com.hikers.hikemate.dto.scrap;

import com.hikers.hikemate.dto.UserIdNickNameDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScrapResponseDto {

    private UserIdNickNameDto user;
    private ScrapDTO scrap;
}
