package com.hikers.hikemate.dto.stamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationAuthRequestDTO {

    @JsonProperty("courseId")
    private int courseId;
    @JsonProperty("latitude")
    private double latitude;
    @JsonProperty("longitude")
    private double longitude;


}
