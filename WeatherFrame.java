package weatherapp;
import java.awt.FlowLayout;
import javax.swing.*;

public class WeatherFrame {

    public WeatherFrame() {

        JFrame frame = new JFrame("Weather App");

        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter a city:");

        JTextField cityField = new JTextField(15);
        JButton button = new JButton("Get Weather");
        

        JLabel resultLabel = new JLabel("Weather will appear here");

       
        
        button.addActionListener(e -> {

            String city = cityField.getText();

            resultLabel.setText(city);

        });
        
        
        
        

        frame.add(label);
        frame.add(cityField);
        frame.add(button);
        frame.add(resultLabel);

        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}