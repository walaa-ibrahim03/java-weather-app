package weatherapp;

import javax.swing.SwingUtilities;

/*
 * Application entry point.
 * This class starts the Weather Information App.
 */
public class WeatherApp {

    public static void main(String[] args) {

        // Runs the GUI safely on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new WeatherFrame());
    }
}