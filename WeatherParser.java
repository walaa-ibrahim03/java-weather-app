package weatherapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * WeatherParser handles JSON parsing.
 * It uses Jackson to read API responses and extract needed values.
 */
public class WeatherParser {

    // ObjectMapper is used by Jackson to read JSON data
    private static final ObjectMapper mapper = new ObjectMapper();

    /*
     * Extracts latitude from the geocoding API response.
     */
    public static String getLatitude(String json) throws Exception {

        JsonNode root = mapper.readTree(json);

        return root.get("results")
                .get(0)
                .get("latitude")
                .asText();
    }

    /*
     * Extracts longitude from the geocoding API response.
     */
    public static String getLongitude(String json) throws Exception {

        JsonNode root = mapper.readTree(json);

        return root.get("results")
                .get(0)
                .get("longitude")
                .asText();
    }

    /*
     * Extracts city and country name from the geocoding API response.
     */
    public static String getLocationName(String json) throws Exception {

        JsonNode root = mapper.readTree(json);

        String name = root.get("results")
                .get(0)
                .get("name")
                .asText();

        String country = root.get("results")
                .get(0)
                .get("country")
                .asText();

        return name + ", " + country;
    }

    /*
     * Extracts current temperature from the weather API response.
     */
    public static double getTemperature(String json) throws Exception {

        JsonNode root = mapper.readTree(json);

        return root.get("current")
                .get("temperature_2m")
                .asDouble();
    }

    /*
     * Extracts current humidity from the weather API response.
     */
    public static int getHumidity(String json) throws Exception {

        JsonNode root = mapper.readTree(json);

        return root.get("current")
                .get("relative_humidity_2m")
                .asInt();
    }

    /*
     * Extracts current wind speed from the weather API response.
     */
    public static double getWindSpeed(String json) throws Exception {

        JsonNode root = mapper.readTree(json);

        return root.get("current")
                .get("wind_speed_10m")
                .asDouble();
    }

    /*
     * Extracts weather code from the weather API response.
     */
    public static int getWeatherCode(String json) throws Exception {

        JsonNode root = mapper.readTree(json);

        return root.get("current")
                .get("weather_code")
                .asInt();
    }

    /*
     * Builds a short-term forecast for the next three days.
     */
    public static String buildForecast(String json, String tempUnit) throws Exception {

        JsonNode root = mapper.readTree(json);

        JsonNode dates = root.get("daily").get("time");
        JsonNode maxTemps = root.get("daily").get("temperature_2m_max");
        JsonNode minTemps = root.get("daily").get("temperature_2m_min");
        JsonNode codes = root.get("daily").get("weather_code");

        StringBuilder forecast = new StringBuilder();

        int daysToShow = Math.min(3, dates.size());

        for (int i = 0; i < daysToShow; i++) {

            int code = codes.get(i).asInt();

            forecast.append(dates.get(i).asText())
                    .append(" | High: ")
                    .append(maxTemps.get(i).asDouble())
                    .append(tempUnit)
                    .append(" | Low: ")
                    .append(minTemps.get(i).asDouble())
                    .append(tempUnit)
                    .append(" | ")
                    .append(weatherCodeToText(code))
                    .append("\n");
        }

        return forecast.toString();
    }

    /*
     * Converts Open-Meteo weather code into readable text.
     */
    public static String weatherCodeToText(int code) {

        if (code == 0) return "Clear sky";
        if (code == 1 || code == 2 || code == 3) return "Partly cloudy";
        if (code == 45 || code == 48) return "Fog";
        if (code >= 51 && code <= 67) return "Rain";
        if (code >= 71 && code <= 77) return "Snow";
        if (code >= 80 && code <= 82) return "Rain showers";
        if (code >= 95 && code <= 99) return "Thunderstorm";

        return "Unknown condition";
    }

    /*
     * Converts Open-Meteo weather code into a simple text icon.
     */
    public static String weatherCodeToIcon(int code) {

        if (code == 0) return "SUNNY";
        if (code == 1 || code == 2 || code == 3) return "PARTLY CLOUDY";
        if (code == 45 || code == 48) return "FOG";
        if (code >= 51 && code <= 67) return "RAIN";
        if (code >= 71 && code <= 77) return "SNOW";
        if (code >= 80 && code <= 82) return "SHOWERS";
        if (code >= 95 && code <= 99) return "THUNDERSTORM";

        return "UNKNOWN";
    }
}