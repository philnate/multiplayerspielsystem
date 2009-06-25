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
	
	public Spielfenster(int x, int y, String initialDisplay, Spieler[] spieler, String[] playerSigns) {
		if (spieler.length != 2) {
			throw new IllegalArgumentException("Spieleranzahl stimmt nicht. Muss 2 betragen");
		}
		positions = new SVGPanel[y][x];
		GridBagLayout gbl = new GridBagLayout();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(gbl);
		this.setSize(new Dimension(400,400));

		GridBagLayout gblField = new GridBagLayout();
		field.setBorder(BorderFactory.createLineBorder(Color.black));
		field.setLayout(gblField);
		int i,j=0;
		for (i = 0; i < y; i++) {
			for (j = 0; j < x; j++) {
				positions[i][j] = new SVGPanel(SVGPanel.FULL,Color.blue);
				positions[i][j].addMouseListener(this);
				positions[i][j].setBorder(BorderFactory.createLineBorder(Color.white));
				positions[i][j].specialInfo = i + ":" + j;
				addComponent(field, gblField, positions[i][j], j, i, 1, 1, 1, 1);
			}
		}

		addComponent(this, gbl, field, 0, 0, 10, 10, 1, 1);
		for (int k = 0; k < playerSigns.length; k++) {
			names[k] = new JLabel(spieler[k].toString());
			names[k].setVerticalAlignment(JLabel.CENTER);

			signs[k] = new SVGPanel(playerSigns[k], new Color(spieler[k].toString().hashCode()));
			signs[k].setSize(new Dimension(20,20));
			players[k] = new JPanel();
			players[k].setLayout(new GridLayout(1,2));
			players[k].setSize(new Dimension(20,100));
			players[k].add(signs[k]);
			players[k].add(names[k]);
			addComponent(this, gbl, players[k], 11, k, 1, 1, 0.1, 0.1);
		}
	}
	
	public void setPicture(int x, int y, int Picture) {
		
	}

	public static void main(String[] args) {
		Spieler[] spieler = new Spieler[2];
		String[] signs = new String[2];
		signs[0] = SVGPanel.CIRCLE;
		signs[1] = SVGPanel.CROSS;
		spieler[0] = new Spieler("Phil");
		spieler[1] = new Spieler("MeMe");
		Spielfenster fenster = new Spielfenster( 4, 4, SVGPanel.FULL, spieler, signs);
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
		FieldClickedListener[] listener=this.getFieldClickedListener();
		for (int i =0; i < listener.length; i++) {
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
