package org.mss.net.client;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientGUI extends JFrame implements ActionListener{
		
	JTextArea txtIn = new JTextArea();
	JTextArea txtOut = new JTextArea();
	JTextArea txtUser = new JTextArea();
	JButton bSend = new JButton("send");
	JButton bClose = new JButton("Schlieﬂen");
	
	public ClientGUI() {		

		//JFrame fCom = new JFrame("Client");
		this.setSize(500, 500);		
		this.setResizable(true);
		Container c = this.getContentPane();//new Container();		

		GridBagLayout gbl = new GridBagLayout();

		c.setLayout(gbl);	
		
		//User-Feld
		setComp(c, gbl, txtUser, 0,0,3,1);
		txtUser.setFocusable(false);
		
		//Ausgabe-Feld
		setComp(c, gbl, txtOut, 1,0,1,1);
		txtOut.setFocusable(false);

		//Eingabe-Feld
		setComp(c, gbl, txtIn, 1,1,1,1);
		
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
			txtOut.setText(txtIn.getText());
			
		}
	}

//	public void setVisible(boolean bool) {
//		ClientGUI gui = new ClientGUI();
//		gui.setVisible(true);
//	}
}
