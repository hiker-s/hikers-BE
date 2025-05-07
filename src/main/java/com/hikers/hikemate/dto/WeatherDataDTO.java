package com.hikers.hikemate.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherDataDTO {

    @JsonProperty("TEMP")
    private String TEMP;

    @JsonProperty("SENSIBLE_TEMP")
    private String SENSIBLE_TEMP;

    @JsonProperty("MAX_TEMP")
    private String MAX_TEMP;

    @JsonProperty("MIN_TEMP")
    private String MIN_TEMP;

    @JsonProperty("PM10_INDEX")
    private String PM10_INDEX;

    @JsonProperty("PM10")
    private String PM10;

    @JsonProperty("PM25_INDEX")
    private String PM25_INDEX;

    @JsonProperty("PM25")
    private String PM25;

    @JsonProperty("UV_INDEX")
    private String UV_INDEX;

    @JsonProperty("RAIN_CHANCE")
    private String RAIN_CHANCE;

}
