package weatherapp;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/*
 * WeatherService handles all API communication.
 * It sends requests to Open-Meteo and returns a WeatherData object.
 */
public class WeatherService {

    // HttpClient sends HTTP requests to online APIs
    private final HttpClient client = HttpClient.newHttpClient();

    /*
     * Main method used by the GUI.
     * It receives a city name and selected unit,
     * then returns complete weather information.
     */
    public WeatherData getWeatherData(String city, String selectedUnit) throws Exception {

        // Encode city name to make it safe for URLs
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

        // First API: converts city name into latitude and longitude
        String geoUrl =
                "https://geocoding-api.open-meteo.com/v1/search?name="
                        + encodedCity
                        + "&count=1&language=en&format=json";

        // Send request and receive JSON response
        String geoJson = sendRequest(geoUrl);

        // Validate that location results exist
        if (!geoJson.contains("\"results\"")) {
            throw new Exception("Location not found.");
        }

        // Extract location information
        String latitude = WeatherParser.getLatitude(geoJson);
        String longitude = WeatherParser.getLongitude(geoJson);
        String locationName = WeatherParser.getLocationName(geoJson);

        // Optional temperature unit parameter
        String tempUnitParam = selectedUnit.equals("Fahrenheit")
                ? "&temperature_unit=fahrenheit"
                : "";

        // Optional wind speed unit parameter
        String windUnitParam = selectedUnit.equals("Fahrenheit")
                ? "&wind_speed_unit=mph"
                : "&wind_speed_unit=kmh";

        // Second API: gets current weather and forecast
        String weatherUrl =
                "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + latitude
                        + "&longitude=" + longitude
                        + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code"
                        + "&daily=temperature_2m_max,temperature_2m_min,weather_code"
                        + "&timezone=auto"
                        + tempUnitParam
                        + windUnitParam;

        // Send weather request and receive JSON response
        String weatherJson = sendRequest(weatherUrl);

        // Create WeatherData object and fill it with parsed values
        WeatherData data = new WeatherData();

        data.locationName = locationName;
        data.temperature = WeatherParser.getTemperature(weatherJson);
        data.humidity = WeatherParser.getHumidity(weatherJson);
        data.windSpeed = WeatherParser.getWindSpeed(weatherJson);
        data.weatherCode = WeatherParser.getWeatherCode(weatherJson);

        data.condition = WeatherParser.weatherCodeToText(data.weatherCode);
        data.icon = WeatherParser.weatherCodeToIcon(data.weatherCode);

        data.tempUnit = selectedUnit.equals("Fahrenheit") ? "°F" : "°C";
        data.windUnit = selectedUnit.equals("Fahrenheit") ? "mph" : "km/h";

        data.forecast = WeatherParser.buildForecast(weatherJson, data.tempUnit);

        return data;
    }

    /*
     * Sends an HTTP GET request and returns the response body as text.
     */
    private String sendRequest(String url) throws Exception {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API request failed.");
        }

        return response.body();
    }
}