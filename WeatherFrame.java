package weatherapp;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/*
 * WeatherFrame is the main GUI window.
 * It displays weather information, forecast, and search history.
 */
public class WeatherFrame extends JFrame {

    private static final long serialVersionUID = 1L;
	// Input field and unit selector
    private JTextField cityField;
    private JComboBox<String> unitBox;

    // Buttons
    private JButton getWeatherButton;
    private JButton clearButton;
    private JButton saveHistoryButton;

    // Labels used to display weather information
    private JLabel locationLabel;
    private JLabel iconLabel;
    private JLabel temperatureLabel;
    private JLabel humidityLabel;
    private JLabel windLabel;
    private JLabel conditionLabel;
    private JLabel messageLabel;

    // Text areas for forecast and history
    private JTextArea forecastArea;
    private JTextArea historyArea;

    // Main panels
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel weatherPanel;

    // Service class that communicates with the API
    private final WeatherService weatherService = new WeatherService();

    // Stores previous searches
    private final ArrayList<String> historyList = new ArrayList<>();

    /*
     * Constructor creates the window and displays it.
     */
    public WeatherFrame() {

        setTitle("Weather Information App");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createComponents();
        updateBackground();

        setVisible(true);
    }

    /*
     * Creates and arranges all GUI components.
     */
    private void createComponents() {

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        topPanel = new JPanel(new FlowLayout());

        JLabel cityLabel = new JLabel("City:");
        cityField = new JTextField(18);

        unitBox = new JComboBox<>(new String[]{"Celsius", "Fahrenheit"});

        getWeatherButton = new JButton("Get Weather");
        clearButton = new JButton("Clear");

        topPanel.add(cityLabel);
        topPanel.add(cityField);
        topPanel.add(new JLabel("Units:"));
        topPanel.add(unitBox);
        topPanel.add(getWeatherButton);
        topPanel.add(clearButton);

        weatherPanel = new JPanel(new GridLayout(7, 1, 5, 5));

        locationLabel = new JLabel("Location: N/A");
        iconLabel = new JLabel("Weather Icon: N/A");
        temperatureLabel = new JLabel("Temperature: N/A");
        humidityLabel = new JLabel("Humidity: N/A");
        windLabel = new JLabel("Wind Speed: N/A");
        conditionLabel = new JLabel("Condition: N/A");
        messageLabel = new JLabel("Enter a city name and click Get Weather.");

        weatherPanel.add(locationLabel);
        weatherPanel.add(iconLabel);
        weatherPanel.add(temperatureLabel);
        weatherPanel.add(humidityLabel);
        weatherPanel.add(windLabel);
        weatherPanel.add(conditionLabel);
        weatherPanel.add(messageLabel);

        forecastArea = new JTextArea(7, 45);
        forecastArea.setEditable(false);
        forecastArea.setBorder(BorderFactory.createTitledBorder("Short-Term Forecast"));

        historyArea = new JTextArea(7, 45);
        historyArea.setEditable(false);
        historyArea.setBorder(BorderFactory.createTitledBorder("Search History"));

        saveHistoryButton = new JButton("Save History");

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(weatherPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(forecastArea), BorderLayout.CENTER);
        centerPanel.add(new JScrollPane(historyArea), BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(saveHistoryButton, BorderLayout.SOUTH);

        add(mainPanel);

        getWeatherButton.addActionListener(e -> fetchWeather());
        clearButton.addActionListener(e -> clearFields());
        saveHistoryButton.addActionListener(e -> saveHistory());

        unitBox.addActionListener(e -> {
            if (!cityField.getText().trim().isEmpty()) {
                fetchWeather();
            }
        });
    }

    /*
     * Fetches weather data when the user clicks Get Weather.
     * SwingWorker keeps the GUI responsive while the API request runs.
     */
    private void fetchWeather() {

        String city = cityField.getText().trim();

        if (city.isEmpty()) {
            messageLabel.setText("Error: Please enter a city name.");
            return;
        }
        if (city.matches("\\d+")) {
            messageLabel.setText("Please enter a valid city name.");
            return;
        }

        String selectedUnit = (String) unitBox.getSelectedItem();

        messageLabel.setText("Loading weather data...");
        getWeatherButton.setEnabled(false);

        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {

            @Override
            protected WeatherData doInBackground() throws Exception {

                return weatherService.getWeatherData(city, selectedUnit);
            }

            @Override
            protected void done() {

                try {

                    WeatherData data = get();

                    updateWeatherDisplay(data);
                    addToHistory(data.locationName);

                    messageLabel.setText("Weather data loaded successfully.");

                } catch (Exception e) {

                    messageLabel.setText("Error: Unable to load weather data.");
                }

                getWeatherButton.setEnabled(true);
            }
        };

        worker.execute();
    }

    /*
     * Updates the GUI labels and forecast area.
     */
    private void updateWeatherDisplay(WeatherData data) {

        locationLabel.setText("Location: " + data.locationName);
        iconLabel.setText("Weather Icon: " + data.icon);
        temperatureLabel.setText("Temperature: " + data.temperature + data.tempUnit);
        humidityLabel.setText("Humidity: " + data.humidity + "%");
        windLabel.setText("Wind Speed: " + data.windSpeed + " " + data.windUnit);
        conditionLabel.setText("Condition: " + data.condition);

        forecastArea.setText(data.forecast);

        updateBackground();
    }

    /*
     * Adds a search record to the history list.
     */
    private void addToHistory(String location) {

        String time =
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String record = time + " - " + location;

        historyList.add(record);

        StringBuilder builder = new StringBuilder();

        for (String item : historyList) {
            builder.append(item).append("\n");
        }

        historyArea.setText(builder.toString());
    }

    /*
     * Clears the weather information from the screen.
     */
    private void clearFields() {

        cityField.setText("");

        locationLabel.setText("Location: N/A");
        iconLabel.setText("Weather Icon: N/A");
        temperatureLabel.setText("Temperature: N/A");
        humidityLabel.setText("Humidity: N/A");
        windLabel.setText("Wind Speed: N/A");
        conditionLabel.setText("Condition: N/A");
        messageLabel.setText("Enter a city name and click Get Weather.");

        forecastArea.setText("");
    }

    /*
     * Saves search history into a text file.
     */
    private void saveHistory() {

        try {

            PrintWriter writer =
                    new PrintWriter(new FileWriter("weather-history.txt"));

            for (String item : historyList) {
                writer.println(item);
            }

            writer.close();

            JOptionPane.showMessageDialog(
                    this,
                    "History saved to weather-history.txt"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving history."
            );
        }
    }

    /*
     * Changes the background color depending on the time of day.
     */
    private void updateBackground() {

        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        Color backgroundColor;

        if (hour >= 6 && hour < 12) {
            backgroundColor = new Color(210, 235, 255);
        } else if (hour >= 12 && hour < 18) {
            backgroundColor = new Color(230, 245, 255);
        } else if (hour >= 18 && hour < 21) {
            backgroundColor = new Color(255, 225, 190);
        } else {
            backgroundColor = new Color(215, 215, 235);
        }

        mainPanel.setBackground(backgroundColor);
        topPanel.setBackground(backgroundColor);
        weatherPanel.setBackground(backgroundColor);
    }
}