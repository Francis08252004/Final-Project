package com.mycompany.numberguessinggame1;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NumberGuessingGame1 extends JFrame {
    private int randomNumber;
    private int attemptsLeft;
    private JTextField guessField;
    private JLabel infoLabel;
    
    public NumberGuessingGame1() {
        super("Number Guessing Game");
        randomNumber = (int) (Math.random() * 100) + 1;
        attemptsLeft = 10;
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        
        JLabel titleLabel = new JLabel("Guess the number (1-100):", JLabel.CENTER);
        panel.add(titleLabel);
        
        guessField = new JTextField(10);
        panel.add(guessField);
        
        JButton guessButton = new JButton("Guess");
        panel.add(guessButton);
        
        infoLabel = new JLabel("You have " + attemptsLeft + " attempts left.", JLabel.CENTER);
        panel.add(infoLabel);
        
        guessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkGuess();
            }
        });
        
        add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 150);
        setVisible(true);
    }
    
    private void checkGuess() {
        String guessText = guessField.getText();
        int guess = 0;
        
        try {
            guess = Integer.parseInt(guessText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        attemptsLeft--;
        
        if (guess == randomNumber) {
            JOptionPane.showMessageDialog(this, "Congratulations! You guessed the number.", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        } else if (attemptsLeft == 0) {
            JOptionPane.showMessageDialog(this, "You ran out of attempts. The number was " + randomNumber + ".", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        } else {
            String message = guess < randomNumber ? "Too low. " : "Too high. ";
            message += "You have " + attemptsLeft + " attempts left.";
            infoLabel.setText(message);
            guessField.setText("");
        }
    }
    
    private void resetGame() {
        randomNumber = (int) (Math.random() * 100) + 1;
        attemptsLeft = 10;
        infoLabel.setText("You have " + attemptsLeft + " attempts left.");
        guessField.setText("");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NumberGuessingGame1();
            }
        });
    }
}

