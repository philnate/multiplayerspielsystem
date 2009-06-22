package org.mss.windows;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;

public class ClientConnect implements ActionListener {
	
	JLabel LAddr = new JLabel("Adresse des Mss Servers");
	JLabel LPort = new JLabel("Port");
	JTextField txtAddr = new JTextField();
	JTextField txtPort = new JTextField();
	JButton bnext = new JButton("weiter");
	JFrame window = new JFrame();
	JEditorPane messages = new JEditorPane();
	Object nop = new Object();
	String queryAnswer = "";
	boolean canceled = false;
	
	public ClientConnect(String windowTitle, String addr,  String port) {
		

		window.setResizable(false);
		window.setPreferredSize(new Dimension(500,100));
		window.setSize(window.getPreferredSize());
		window.setLocationRelativeTo(null);	 
				
		GridBagLayout gbl = new GridBagLayout();
		window.setLayout(gbl);
		
		messages.setEditable(false);
		
		messages.setEditorKit(new HTMLEditorKit());
		
		setComp(window, gbl, new JScrollPane(LAddr), 0,0,1,1,1,2);
		setComp(window, gbl, new JScrollPane(LPort), 0,1,1,1,1,1);
		setComp(window, gbl, new JScrollPane(txtAddr), 1,0,1,1,1,2);
		setComp(window, gbl, new JScrollPane(txtPort), 1,1,1,1,1,1);
		setComp(window, gbl, new JScrollPane(bnext), 2,1,1,1,1,1);
		txtAddr.setText(addr);
		txtPort.setText(port);
		bnext.addActionListener(this);		
	}
	
	public void setComp(Container window, GridBagLayout gbl, Component comp, int x, int y, int h, int w, double gh, double gw){

		GridBagConstraints gbc=new GridBagConstraints();	
		
		gbc.fill=GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridheight = h;
		gbc.gridwidth = w;
		gbc.weightx = gw;
		gbc.weighty = gh;
		gbl.setConstraints(comp, gbc);
		window.add(comp);
	}
	
	public String getAddrText(){
		return txtAddr.getText();
	}
	
	public String getPortText(){
		return txtPort.getText();
	}	
	
	public void show() {			
		
		window.pack();
		window.setVisible(true);
		synchronized(nop) {
			try {
				nop.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
		
	private void closeWindow(boolean canceled) {
		synchronized(nop) {
			queryAnswer = canceled? "":txtAddr.getText();//Festlegen welcher Text �bergeben wird
			this.canceled = canceled;
			nop.notifyAll();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnext) {
			window.dispose();
			closeWindow(true);
		}			
	}

}
