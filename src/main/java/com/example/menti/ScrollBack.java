package com.example.menti;

import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScrollBack extends JPanel implements ActionListener {
    Image pic;
    Timer scrollTimer;
    Timer countdownTimer; // Timer for countdown
    int picWidth;
    int picHeight;
    int xStart = 1400;
    int move = 20;
    BufferedImage buffer; // Off-screen image for double buffering

    static String music = "Audio\\mindfulness-relaxation-amp-meditation-music-22174.wav";

    String open = "Audio\\Open Your Eyes.wav";
    long startTime; // Start time for countdown
    long countdownDuration; // Countdown duration in milliseconds
    JLabel countdownLabel; // Label to display countdown
    JTextField inputField; // Text field for user input
    JButton startButton; // Button to start countdown

    public ScrollBack() {
        ImageIcon obj = new ImageIcon("/Users/kennychen/Desktop/IdeaProjects/Menti/src/medSource/waves_1.png");
        pic = obj.getImage();
        picWidth = pic.getWidth(null);
        picHeight = pic.getHeight(null);

        setupInputField();
        setupStartButton();
        setupCountdownLabel();

        setLayout(new BorderLayout()); // Use BorderLayout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for label and button
        topPanel.add(countdownLabel);

        // Add a panel for the input field and its label
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(new JLabel("Enter countdown duration (seconds):"));
        inputPanel.add(inputField);

        topPanel.add(inputPanel); // Add the inputPanel to the topPanel
        topPanel.add(startButton);

        add(topPanel, BorderLayout.NORTH); // Add topPanel to the NORTH position
    }

    private void setupInputField() {
        inputField = new JTextField(10);
        inputField.setPreferredSize(new Dimension(150, 30)); // Set preferred size for inputField
    }

    private void setupStartButton() {
        startButton = new JButton("Start Countdown");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startCountdown();
            }
        });
    }

    private void setupCountdownLabel() {
        countdownLabel = new JLabel();
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 40));
        countdownLabel.setForeground(Color.BLACK);
    }
    public void start(Stage stage) {
        JFrame f = new JFrame("Meditation Space");

        // Set up the JFrame
        f.setSize(1400, 700);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(this); // Add the current instance of ScrollBack to the JFrame
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        // Call the LoopMusic method
        LoopMusic(music);
    }

    private void startCountdown() {
        try {
            long userInput = Long.parseLong(inputField.getText());
            countdownDuration = userInput * 1000; // Convert seconds to milliseconds
            startTime = System.currentTimeMillis();
            countdownTimer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateCountdownLabel();
                }
            });
            countdownTimer.start();
            startButton.setEnabled(false); // Disable the button after countdown starts

            // Start the image scrolling timer once the countdown starts
            scrollTimer = new Timer(75, this);
            scrollTimer.start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for countdown duration.");
        }
    }

    private void updateCountdownLabel() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long remainingTime = countdownDuration - elapsedTime;
        if (remainingTime <= 0) {
            startButton.setEnabled(true); // Disable the button after countdown starts
            countdownLabel.setText("Open your eyes");
            countdownTimer.stop();
            playOnce(open);
            if (scrollTimer != null) {
                scrollTimer.stop(); // Stop the image scrolling timer when the countdown ends
            }
        } else {
            long seconds = remainingTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            String timeString = String.format("%02d:%02d", minutes, seconds);
            countdownLabel.setText("Countdown: " + timeString);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawCircle(g); // Draw the circle around the timer label
    }

    private void drawBackground(Graphics g) {
        if (buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight()) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2d = (Graphics2D) buffer.getGraphics();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        double scaleX = (double) getWidth() / picWidth;
        double scaleY = (double) getHeight() / picHeight;
        double scale = Math.max(scaleX, scaleY);

        int scaledWidth = (int) (picWidth * scale);
        int scaledHeight = (int) (picHeight * scale);

        int xPosition = xStart % scaledWidth; // Adjusted x position for smooth scrolling

        // Draw the image at the adjusted x position
        int firstImageX = (xPosition >= 0) ? xPosition - scaledWidth : xPosition;
        int secondImageX = (xPosition >= 0) ? xPosition : xPosition + scaledWidth;

        g2d.drawImage(pic, firstImageX, 0, scaledWidth, scaledHeight, null);
        g2d.drawImage(pic, secondImageX, 0, scaledWidth, scaledHeight, null);

        g.drawImage(buffer, 0, 0, null);
    }


    private void drawCircle(Graphics g) {
        int labelWidth = countdownLabel.getWidth();
        int labelHeight = countdownLabel.getHeight();
        int labelX = countdownLabel.getX();
        int labelY = countdownLabel.getY();


        int diameter = Math.max(labelWidth, labelHeight)*(int)(1.5); // Diameter of the circle
        int cornerX = 500; // Adjusted x coordinate for top-left corner
        int cornerY = 100; // Adjusted y coordinate for top-left corner

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.ORANGE);

        long elapsedTime = System.currentTimeMillis() - startTime;
        long remainingTime = countdownDuration - elapsedTime;
        double progress = (double) elapsedTime / countdownDuration;

        // Draw the outline of the circle
        g2d.drawOval(cornerX, cornerY, diameter, diameter);

        // Draw the filled arc
        g2d.fill(new Arc2D.Double(cornerX, cornerY, diameter, diameter, 90, 360 * progress, Arc2D.PIE));
    }

    public void actionPerformed(ActionEvent e) {
        if (Math.abs(xStart) == picWidth) {
            xStart = 0;
        } else {
            xStart += move; // Change += to -= for the image to move left
        }
        repaint(); // Update the panel to reflect the new position of the image
    }

    public static void main(String[] args) {
        ScrollBack p = new ScrollBack();
        JFrame f = new JFrame("Meditation Space");
        LoopMusic(music);


        f.setSize(1400, 700);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(p);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    private void playOnce(String open) {
        try {
            File openPath = new File(open);

            if (openPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(openPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            } else {
                System.out.println("Can't find file: " + open);
            }
        } catch (Exception e) {
            System.out.println("Error playing music: " + e);
        }
    }
    private static void LoopMusic(String music) {
        try
        {
            File musicPath = new File(music);

            if (musicPath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip  = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            }
            else{
                System.out.println("cant find file");

            }
        }
        catch(Exception e){
            System.out.println(e);
        }

    }
}