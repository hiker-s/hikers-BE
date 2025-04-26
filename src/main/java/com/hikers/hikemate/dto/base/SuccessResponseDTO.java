package com.hikers.hikemate.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponseDTO<T> {
    private int stats;
    private String message;
    private T data;
}
