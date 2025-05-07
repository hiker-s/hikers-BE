package com.hikers.hikemate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.hikers.hikemate.dto.WeatherDataDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String API_KEY;

    private static final String LOCATION = "서울역";


    public WeatherDataDTO getWeatherData() throws Exception {

        String URL_STR = "http://openapi.seoul.go.kr:8088/" + API_KEY + "/xml/citydata/1/5/" + LOCATION;
        URL url = new URL(URL_STR);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8")
        );
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        //System.out.println("Received XML data: " + sb.toString());

        // XML → JSON-like 구조로 파싱
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode root = xmlMapper.readTree(sb.toString());


        JsonNode cityDataNode = root.path("CITYDATA");
        JsonNode outerWeatherNode = cityDataNode.path("WEATHER_STTS");
        JsonNode weatherNode = outerWeatherNode.path("WEATHER_STTS");

        // weatherNode의 내용이 비어있는지 확인
        /*if (weatherNode.isMissingNode()) {
            System.out.println("No WEATHER_STTS node found.");
        } else {
            System.out.println("Found WEATHER_STTS node: " + weatherNode.toString());
        }*/

        WeatherDataDTO dto = new WeatherDataDTO();
        dto.setTEMP(weatherNode.path("TEMP").asText());
        dto.setSENSIBLE_TEMP(weatherNode.path("SENSIBLE_TEMP").asText());
        dto.setMAX_TEMP(weatherNode.path("MAX_TEMP").asText());
        dto.setMIN_TEMP(weatherNode.path("MIN_TEMP").asText());
        dto.setPM10_INDEX(weatherNode.path("PM10_INDEX").asText());
        dto.setPM10(weatherNode.path("PM10").asText());
        dto.setPM25_INDEX(weatherNode.path("PM25_INDEX").asText());
        dto.setPM25(weatherNode.path("PM25").asText());
        dto.setUV_INDEX(weatherNode.path("UV_INDEX").asText());

        // 예보에서 RAIN_CHANCE 가져오기 (예: 첫 시간 기준)
        JsonNode rainForecastNode = weatherNode.path("FCST24HOURS").path("FCST24HOURS");
        if (rainForecastNode.isArray() && rainForecastNode.size() > 0) {
            dto.setRAIN_CHANCE(rainForecastNode.get(0).path("RAIN_CHANCE").asText());
        } else {
            dto.setRAIN_CHANCE("정보 없음");
        }

        return dto;
    }
}
