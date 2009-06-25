package org.mss;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.ArrayList;

import org.mss.windows.svg.SVGPanel;
import org.mss.utils.listener.FieldClickedListener;

public class Spielfenster extends JFrame implements MouseListener {
//	JFrame window = new JFrame();
	JPanel field = new JPanel();
	JPanel[] players = new JPanel[2];
	SVGPanel[] signs = new SVGPanel[2];
	JLabel[] names = new JLabel[2];
	SVGPanel[][] positions;
	ArrayList<FieldClickedListener> listener = new ArrayList<FieldClickedListener>(1);
	GridBagLayout gbl = new GridBagLayout();
	
	public Spielfenster(int x, int y, String initialDisplay) {
		positions = new SVGPanel[y][x];
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(gbl);
		this.setSize(new Dimension(400,400));

		GridBagLayout gblField = new GridBagLayout();
		field.setBorder(BorderFactory.createLineBorder(Color.black));
		field.setLayout(gblField);
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				positions[i][j] = new SVGPanel(SVGPanel.FULL,Color.blue);
				positions[i][j].addMouseListener(this);
				positions[i][j].setBorder(BorderFactory.createLineBorder(Color.white));
				positions[i][j].specialInfo = i + ":" + j;
				addComponent(field, gblField, positions[i][j], j, i, 1, 1, 1, 1);
			}
		}

		addComponent(this, gbl, field, 0, 0, 10, 10, 1, 1);
	}
	
	public void setPlayer(Spieler[] spieler, String[] playerSigns) {
		for (int i = 0; i < playerSigns.length; i++) {
			names[i] = new JLabel(spieler[i].toString());
			names[i].setVerticalAlignment(JLabel.CENTER);

			signs[i] = new SVGPanel(playerSigns[i], new Color(spieler[i].toString().hashCode()));
			signs[i].setSize(new Dimension(20,20));
			players[i] = new JPanel();
			players[i].setLayout(new GridLayout(1,2));
			players[i].setSize(new Dimension(20,100));
			players[i].add(signs[i]);
			players[i].add(names[i]);
			addComponent(this, gbl, players[i], 11, i, 1, 1, 0.1, 0.1);
		}		
	}

	public void setPicture(int x, int y, String picture) {
		positions[y][x] = new SVGPanel(picture);
	}

	public static void main(String[] args) {
		Spieler[] spieler = new Spieler[2];
		String[] signs = new String[2];
		signs[0] = SVGPanel.CIRCLE;
		signs[1] = SVGPanel.CROSS;
		spieler[0] = new Spieler("Phil");
		spieler[1] = new Spieler("MeMe");
		Spielfenster fenster = new Spielfenster( 4, 4, SVGPanel.FULL);
		fenster.setPlayer(spieler, signs);
		fenster.setVisible(true);
	}

	static void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y,
			int width, int height, double weightx, double weighty ) { 
		
		GridBagConstraints gbc = new GridBagConstraints(); 
		gbc.fill = GridBagConstraints.BOTH; 
		gbc.gridx = x; 
		gbc.gridy = y; 
		gbc.gridwidth = width; 
		gbc.gridheight = height; 
		gbc.weightx = weightx; 
		gbc.weighty = weighty; 
		gbl.setConstraints( c, gbc ); 
		cont.add( c ); 
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String info = ((SVGPanel)e.getSource()).specialInfo;
		FieldClickedListener[] listener=this.getFieldClickedListener();
		for (int i =0; i < listener.length; i++) {
			listener[i].zugIsDone(Integer.parseInt(info.substring(0,info.indexOf(":"))),
					Integer.parseInt(info.substring(info.indexOf(":")+1)));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	public void addFieldClickedListener(FieldClickedListener listener) {
		this.listener.add(listener);
	}
	
	public FieldClickedListener[] getFieldClickedListener() {
		return this.listener.toArray(new FieldClickedListener[0]);
	}
}
