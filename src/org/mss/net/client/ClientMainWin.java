package org.mss.net.client;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClientMainWin  extends JFrame implements KeyListener, ActionListener {

	
	JTextArea txtIn = new JTextArea(5,30);
	JTextArea txtOut = new JTextArea(20,25);
	JTextArea txtUserList = new JTextArea(30,15);
	JButton bSend = new JButton("send");
	JButton bClose = new JButton("Schließen");
	
	public ClientMainWin() {
		
		this.setResizable(true);
		Container c = this.getContentPane();		
		GridBagLayout gbl = new GridBagLayout();
		c.setLayout(gbl);	
		
		//User-Feld
		setComp(c, gbl, new JScrollPane(txtUserList), 0,0,3,1);
		txtUserList.setFocusable(false);
		
		//Ausgabe-Feld
		setComp(c, gbl, new JScrollPane(txtOut), 1,0,1,1);
		txtOut.setFocusable(false);

		//Eingabe-Feld
		setComp(c, gbl, new JScrollPane(txtIn), 1,1,1,1);
		txtIn.addKeyListener(this);
		
        //Close-Button
        setComp(c, gbl, bClose, 2,0,1,1);
		bClose.addActionListener(this);
		
		//Send-Button
		setComp(c, gbl, bSend, 2,1,1,1);
        bSend.addActionListener(this); 
        
        this.pack();        
		System.out.println(c.getSize().toString());
	}
	
	public void gameWindow(){
		
		this.setResizable(true);
		Container c = this.getContentPane();		
		GridBagLayout gbl = new GridBagLayout();
		c.setLayout(gbl);	
		
		//User-Feld
		setComp(c, gbl, new JScrollPane(txtUserList), 0,0,3,1);
		txtUserList.setFocusable(false);
		
		//Ausgabe-Feld
		setComp(c, gbl, new JScrollPane(txtOut), 1,0,1,1);
		txtOut.setFocusable(false);

		//Eingabe-Feld
		setComp(c, gbl, new JScrollPane(txtIn), 1,1,1,1);
		txtIn.addKeyListener(this);
		
        //Close-Button
        setComp(c, gbl, bClose, 2,0,1,1);
		bClose.addActionListener(this);
		
		//Send-Button
		setComp(c, gbl, bSend, 2,1,1,1);
        bSend.addActionListener(this); 
        
        this.pack();        
		System.out.println(c.getSize().toString());
	}
	
	void setComp(Container c, GridBagLayout gbl, Component comp, int x, int y, int h, int w){

		GridBagConstraints gbc=new GridBagConstraints();	
		
		gbc.fill=GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridheight = h;
		gbc.gridwidth = w;
		gbc.weightx = (double)w;
		gbc.weighty = (double)h;
		gbl.setConstraints(comp, gbc);
		c.add(comp,gbc);
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == bClose) {
			dispose();
		}		
		if (e.getActionCommand().equals("send")){
			send();			
		}		
	}	
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		//wenn Enter und Strg gedrückt, dann sende
	    if ((key == KeyEvent.VK_ENTER) && e.isControlDown()) {
	    	send();	    	
	    }
	}
	
	public void keyReleased(KeyEvent e) {		
	}

	public void keyTyped(KeyEvent e) {		
	}
	
	public void send(){
		txtOut.append(txtIn.getText() + "\n");
		txtIn.setText("");
	}


}
