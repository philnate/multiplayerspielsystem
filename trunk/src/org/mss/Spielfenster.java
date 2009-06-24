package org.mss;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mss.windows.svg.SVGPanel;

public class Spielfenster implements MouseListener {
	JFrame window = new JFrame();
	JPanel field = new JPanel();
	JPanel[] players = new JPanel[2];
	SVGPanel[] signs = new SVGPanel[2];
	JLabel[] names = new JLabel[2];
	SVGPanel[][] positions;

	public Spielfenster(int x, int y, String initialDisplay, Spieler[] spieler, String[] playerSigns) {
		if (spieler.length != 2) {
			throw new IllegalArgumentException("Spieleranzahl stimmt nicht. Muss 2 betragen");
		}
		positions = new SVGPanel[y][x];
		GridBagLayout gbl = new GridBagLayout();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(gbl);
		window.setSize(new Dimension(400,400));

		field.setBorder(BorderFactory.createLineBorder(Color.black));
		field.setLayout(gbl);
		
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				positions[i][j] = new SVGPanel(SVGPanel.FULL,Color.blue);
				positions[i][j].addMouseListener(this);
				positions[i][j].setBorder(BorderFactory.createLineBorder(Color.white));
				positions[i][j].specialInfo = i + ":" + j;
				addComponent(field, gbl, positions[i][j], j, i, 1, 1, 1, 1);
			}
		}
		addComponent(window, gbl, field, 0, 0, 10, 10, 1, 1);
		
		for (int i = 0; i < playerSigns.length; i++) {
			names[i] = new JLabel(spieler[i].toString());
			names[i].setVerticalAlignment(JLabel.CENTER);

			signs[i] = new SVGPanel(playerSigns[i], new Color(spieler[i].toString().hashCode()));
			signs[i].setPreferredSize(new Dimension(20,20));
			
			players[i] = new JPanel();
			players[i].add(signs[i]);
			players[i].add(names[i]);
			addComponent(window, gbl, players[i], 11, i, 1, 1, 0.1, 0.1);
		}
	}
	
	public void setPicture(int x, int y, int Picture) {
		
	}
	
	public void setVisible(boolean bool) {
		window.setVisible(bool);
	}

	public static void main(String[] args) {
		Spieler[] spieler = new Spieler[2];
		String[] signs = new String[2];
		signs[0] = SVGPanel.CIRCLE;
		signs[1] = SVGPanel.CROSS;
		spieler[0] = new Spieler("Phil");
		spieler[1] = new Spieler("MeMe");
		Spielfenster fenster = new Spielfenster( 10, 10, SVGPanel.FULL, spieler, signs);
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
		System.out.println(((SVGPanel)e.getSource()).specialInfo);
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
}
