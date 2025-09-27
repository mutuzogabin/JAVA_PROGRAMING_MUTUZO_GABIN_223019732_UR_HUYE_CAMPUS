package com.Registration;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;


public class RegistrationProject implements ActionListener {
	

	

		JFrame frame;
		JLabel fnumber = new JLabel("Amount Requested");
		JLabel lnumber = new JLabel("Duration(Year)");
		JLabel results = new  JLabel("Total Return");
		JTextField lntxf = new JTextField();
		JTextField fntxf = new JTextField();
		JTextField resultTxf = new JTextField();

		JButton add = new JButton(" Calculate");
		
		

		// constructors

		public RegistrationProject() {
			createWindow();
			setLocationAndSize();
			addcomponentsToFrame();
			addActionEvent();


		}
		

		private void addActionEvent() {
			add.addActionListener(this);
			
			
		}

		private void addcomponentsToFrame() {
			frame.add(fnumber);
			frame.add(lnumber);
			frame.add(results);
			frame.add(fntxf);
			frame.add(lntxf);
			frame.add(add);
			frame.add(resultTxf);
			
		}

		private void setLocationAndSize() {
			//First number
					fnumber.setBounds(4, 10, 115,40);
					fntxf.setBounds(120, 10, 110,40);
					
					//Last Number
					lnumber.setBounds(4, 60, 115,40);
					lntxf.setBounds(120, 60, 115,40);
					
					// Results
					
					results.setBounds(4, 110, 115,40);
					resultTxf.setBounds(120, 110, 115,40);
					
					//Buttons
					
					int x =90, y=160, w = 120, h =40;
					
					add.setBounds(x,y,w,h);
					x+=40;
					
				}

			

		private void createWindow() {
			frame = new JFrame();
			frame.setTitle("===Operations==");
			frame.setBounds(10,10,400,250);
			frame.getContentPane().setBackground(new Color(100, 159, 212));
			frame.getContentPane().setLayout(null);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(true);
			
			Border lineBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
			
			
			//Set Colors
			add.setBackground(new Color (100, 159, 212));
			add.setForeground(Color.white);
			fnumber.setForeground(Color.white);
			fnumber.setBorder(lineBorder);
			lnumber.setForeground(Color.white);
			lnumber.setBorder(lineBorder);
			results.setForeground(Color.white);
			results.setBorder(lineBorder);
			add.setOpaque(true);
			add.setBorderPainted(true);
			
			
			//Add colors
			
			fnumber.setBackground(Color.DARK_GRAY);
			lnumber.setBackground(Color.DARK_GRAY);
			//Border lineBorder = BorderFactory.createLineBorder(Color.BLUE, 2); 
		}


		@Override
		public void actionPerformed(ActionEvent e) {
	if (e.getSource() ==add) {
				
				int a_r = Integer.parseInt(fntxf.getText());
				int d_y = Integer.parseInt(lntxf.getText());
				int t_r = d_y *12000;
				resultTxf.setText(String.valueOf(a_r + t_r));
				
	}else {
				System.out.println("Unkown Operation");
			}
				
			
		}
			
		}


