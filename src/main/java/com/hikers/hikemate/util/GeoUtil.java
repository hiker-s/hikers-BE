package com.hikers.hikemate.util;

public class GeoUtil {
    // 두 위도/경도 지점 간 거리 계산 (Haversine 공식) GPT가 추천해줌요..
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // 지구 반지름 (m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 거리(m)
    }

}
