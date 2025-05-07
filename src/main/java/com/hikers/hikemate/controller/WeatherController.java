package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.WeatherDataDTO;
import com.hikers.hikemate.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/api/weather")
    public WeatherDataDTO getWeather() throws Exception {
        return weatherService.getWeatherData();
    }
}
