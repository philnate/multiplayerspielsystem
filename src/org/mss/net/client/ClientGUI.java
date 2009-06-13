package org.mss.net.client;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ClientGUI extends JFrame implements KeyListener, ActionListener{
		
	JLabel LUser = new JLabel("Benutzer");
	JLabel LPw = new JLabel("Passwort");
	JTextField txtUser = new JTextField();
	JTextField txtPw = new JTextField();
	JButton bnext = new JButton("weiter");
	JFrame window = new JFrame();
	
	boolean visible = true;
	
	public ClientGUI() {
		this("");
	}
	
	public ClientGUI(String windowTitle) {
				
		window.setSize(new Dimension(500,100));
		window.setResizable(false);
		window.setLayout(new BorderLayout(5,5));
		window.setTitle(windowTitle);
		
		//Größenfestlegung
		LUser.setPreferredSize(new Dimension(100,30));		
		LPw.setPreferredSize(new Dimension(100,30));
		txtUser.setPreferredSize(new Dimension(150,30));
		txtPw.setPreferredSize(new Dimension(150,30));
		
		window.add(LUser, BorderLayout.NORTH);
		window.add(LPw, BorderLayout.SOUTH);
		window.add(txtUser, BorderLayout.NORTH);
		window.add(txtPw, BorderLayout.SOUTH);
		window.add(bnext, BorderLayout.SOUTH);
		window.setLocationRelativeTo(null);	 
		
	}
	
	public String getUserText(){
		return txtUser.getText();
	}
	
	public String getPwText(){
		return txtPw.getText();
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == bnext){			
			notifyAll();
		}
	
	}	
	
	public void keyPressed(KeyEvent e){		
	}
	
	public void keyReleased(KeyEvent e) {		
	}

	public void keyTyped(KeyEvent e) {		
	}

}
