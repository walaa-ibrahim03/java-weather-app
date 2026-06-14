package weatherapp;

/*
 * WeatherData is a model class.
 * It stores weather information after it is received from the API.
 */
public class WeatherData {

    // City and country name
    public String locationName;

    // Current temperature
    public double temperature;

    // Current humidity percentage
    public int humidity;

    // Current wind speed
    public double windSpeed;

    // Weather code returned by Open-Meteo API
    public int weatherCode;

    // Weather description, such as Clear sky or Rain
    public String condition;

    // Text icon such as SUNNY, RAIN, SNOW
    public String icon;

    // Temperature unit: °C or °F
    public String tempUnit;

    // Wind speed unit: km/h or mph
    public String windUnit;

    // Three-day forecast text
    public String forecast;
}