package com.hikers.hikemate.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDTO {
    private int stats;
    private String message;
}
