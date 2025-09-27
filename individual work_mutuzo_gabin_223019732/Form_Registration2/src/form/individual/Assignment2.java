package form.individual;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;

public class Assignment2 implements ActionListener {

	
		private static final Border lineBorder = null;
		// Components declaration
	    JFrame frame;
	    JLabel namesLabel = new JLabel("Names:");
	    JLabel usernameLabel = new JLabel("UserName:");
	    JLabel passwordLabel = new JLabel("Password:");
	    
	    JTextField namesTextField = new JTextField();
	    JTextField usernameTextField = new JTextField();
	    JPasswordField passwordField = new JPasswordField();
	    
	    JButton registerButton = new JButton("Register");
	    JButton resetButton = new JButton("Reset");
	    
	    // Constructor
	    public Assignment2() {
	        createWindow();
	        setLocationAndSize();
	        addComponentsToFrame();
	        addActionEvent();
	    }
	    
	    private void addActionEvent() {
	        registerButton.addActionListener(this);
	        resetButton.addActionListener(this);
	    }
	    
	    private void addComponentsToFrame() {
	        frame.add(namesLabel);
	        namesLabel.setBorder(lineBorder);
	        frame.add(usernameLabel);
	        frame.add(passwordLabel);
	        frame.add(namesTextField);
	        frame.add(usernameTextField);
	        frame.add(passwordField);
	        frame.add(registerButton);
	        frame.add(resetButton);
	    }
	    
	    private void setLocationAndSize() {
	        // Labels and TextFields positioning
	        int labelX = 20, fieldX = 120, y = 20, height = 30, labelWidth = 80, fieldWidth = 200;
	        
	        // Names
	        namesLabel.setBounds(labelX, y, labelWidth, height);
	        namesTextField.setBounds(fieldX, y, fieldWidth, height);
	        y += 40;
	        
	        // Username
	        usernameLabel.setBounds(labelX, y, labelWidth, height);
	        usernameTextField.setBounds(fieldX, y, fieldWidth, height);
	        y += 40;
	        
	        // Password
	        passwordLabel.setBounds(labelX, y, labelWidth, height);
	        passwordField.setBounds(fieldX, y, fieldWidth, height);
	        y += 50;
	        
	        // Buttons
	        int buttonWidth = 100;
	        registerButton.setBounds(fieldX, y, buttonWidth, height);
	        resetButton.setBounds(fieldX + 110, y, buttonWidth, height);
	    }
	    
	    private void createWindow() {
	        frame = new JFrame();
	        frame.setTitle("Registration Form");
	        frame.setBounds(100, 100, 400, 250);
	        frame.getContentPane().setBackground(new Color(100, 159, 212));
	        frame.getContentPane().setLayout(null);
	        frame.setVisible(true);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setResizable(false);
	        
	        
	        // Callings
	        
	        Border lineBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
	    }
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        if (e.getSource() == registerButton) {
	            handleRegistration();
	        } else if (e.getSource() == resetButton) {
	            handleReset();
	        }
	    }
	    
	    private void handleRegistration() {
	        String names = namesTextField.getText();
	        String username = usernameTextField.getText();
	        String password = new String(passwordField.getPassword());
	        
	        // Validation
	        if (names.isEmpty() || username.isEmpty() || password.isEmpty()) {
	            JOptionPane.showMessageDialog(frame, 
	                "Please fill in all fields!", 
	                "Error", 
	                JOptionPane.ERROR_MESSAGE);
	        } else {
	            JOptionPane.showMessageDialog(frame, 
	                "Registration Successful!\n\n" +
	                "Names: " + names + "\n" +
	                "Username: " + username + "\n" +
	                "Password: " + "***" + password.length() + " characters", 
	                "Success", 
	                JOptionPane.INFORMATION_MESSAGE);
	        }
	    }
	    
	    private void handleReset() {
	        namesTextField.setText("");
	        usernameTextField.setText("");
	        passwordField.setText("");
	    }
	}



