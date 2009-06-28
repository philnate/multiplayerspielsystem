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
import javax.swing.SwingConstants;

import java.util.ArrayList;

import org.mss.windows.svg.SVGPanel;
import org.mss.utils.listener.FieldClickedListener;

public class Spielfenster extends JFrame implements MouseListener {
/**
	 * 
	 */
	private static final long serialVersionUID = 1828815814452820069L;
	JPanel field = new JPanel();
	JPanel[] players = new JPanel[2];
	SVGPanel[] signs = new SVGPanel[2];
	JLabel[] names = new JLabel[2];
	JLabel[] heights;
	JLabel[] widths;
	SVGPanel[][] positions;
	ArrayList<FieldClickedListener> listener = new ArrayList<FieldClickedListener>(1);
	GridBagLayout gbl = new GridBagLayout();
	int posX = -1;
	int posY = -1;

	boolean locked = false;

	public Spielfenster(int x, int y, String initialDisplay) {
		positions = new SVGPanel[y][x];
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(gbl);
		this.setSize(new Dimension(800,800));
		heights = new JLabel[y];
		widths = new JLabel[x];

		GridBagLayout gblField = new GridBagLayout();
		field.setBorder(BorderFactory.createLineBorder(Color.black));
		field.setLayout(gblField);
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				positions[i][j] = new SVGPanel(SVGPanel.FULL,Color.white);
				positions[i][j].addMouseListener(this);
				positions[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				positions[i][j].specialInfo = y-i-1 + ":" + j;
				addComponent(field, gblField, positions[i][j], j, i, 1, 1, 1, 1);
			}
			heights[i] = new JLabel(""+(y-i-1));
			heights[i].setHorizontalAlignment(SwingConstants. CENTER);
			heights[i].setPreferredSize(new Dimension(10,10));
			addComponent(field, gblField, heights[i], x+1, i, 1, 1, 1, 1);
		}

		for (int i = 0; i < x; i++) {
			widths[i] = new JLabel(""+i);
			widths[i].setHorizontalAlignment(SwingConstants.CENTER);
			widths[i].setPreferredSize(new Dimension(10,10));
			addComponent(field, gblField, widths[i], i, y+1, 1, 1, 1, 1);
		}

		addComponent(this, gbl, field, 0, 0, 10, 10, 1, 1);
		field.setPreferredSize(new Dimension(300,300));
	}
	//TODO dynamisch Spieler hinzufügen
	public void setPlayer(Spieler[] spieler, String[] playerSigns) {
		for (int i = 0; i < playerSigns.length; i++) {
			names[i] = new JLabel(spieler[i].toString());
			names[i].setVerticalAlignment(JLabel.CENTER);

			signs[i] = new SVGPanel(playerSigns[i], new Color(spieler[i].toString().hashCode()));
			signs[i].setSize(new Dimension(20,20));
			players[i] = new JPanel();
			players[i].setLayout(new GridLayout(1,2));
//			players[i].setSize(new Dimension(20,100));
			players[i].add(signs[i]);
			players[i].add(names[i]);
			addComponent(this, gbl, players[i], 11, i, 1, 1, 0, 0);
		}		
	}

	public void setPicture(int x, int y, String picture, Color col) {
		positions[y][x].setPicture(picture, col);
	}

//	public static void main(String[] args) {
//		Spieler[] spieler = new Spieler[2];
//		String[] signs = new String[2];
//		signs[0] = SVGPanel.CIRCLE;
//		signs[1] = SVGPanel.CROSS;
//		spieler[0] = new Spieler("Phil");
//		spieler[1] = new Spieler("MeMe");
//		Spielfenster fenster = new Spielfenster( 4, 4, SVGPanel.FULL);
//		fenster.setPlayer(spieler, signs);
//		fenster.setVisible(true);
//	}

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
		if (locked) return;//Eingaben werden nicht bearbeitet
		String info = ((SVGPanel)e.getSource()).specialInfo;
		System.out.println(info);
		FieldClickedListener[] listener=this.getFieldClickedListener();
		for (int i =0; i < listener.length; i++) {
			listener[i].zugIsDone(Integer.parseInt(info.substring(0,info.indexOf(":"))),
					Integer.parseInt(info.substring(info.indexOf(":")+1)));
		}
		locked=true;
		posX = Integer.parseInt(info.substring(info.indexOf(":")+1));
		posY = Integer.parseInt(info.substring(0,info.indexOf(":")));
		synchronized (this) {
			this.notifyAll();
		}
	}

	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
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
	
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean bool) {
		locked = bool;
	}
	public void addFieldClickedListener(FieldClickedListener listener) {
		this.listener.add(listener);
	}
	
	public FieldClickedListener[] getFieldClickedListener() {
		return this.listener.toArray(new FieldClickedListener[0]);
	}
}
