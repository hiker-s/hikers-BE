package com.hikers.hikemate.dto.stamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StampForServerDTO {
    @JsonProperty("courseId")
    private int courseId;
}
