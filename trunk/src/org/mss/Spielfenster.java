package org.mss;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.util.ArrayList;

import org.mss.windows.svg.SVGPanel;
import org.mss.utils.listener.FieldClickedListener;

public class Spielfenster extends JFrame implements MouseListener, ActionListener {
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
	JLabel history = new JLabel("Spielprotokoll");
	JButton previous = new JButton("<");
	JButton next = new JButton(">");
	ArrayList<FieldClickedListener> listener = new ArrayList<FieldClickedListener>(1);
	ArrayList<WindowListener> windowListener = new ArrayList<WindowListener>(1);
	ArrayList<ActionListener> actionListener = new ArrayList<ActionListener>(1);
	String initialDisplay = "";
	
	int posX = -1;
	int posY = -1;

	boolean locked = false;

	public Spielfenster(int x, int y, String initialDisplay) {
		positions = new SVGPanel[y][x];

		this.setLayout(null);
		this.setSize(new Dimension(800, 800));
		heights = new JLabel[y];
		widths = new JLabel[x];
		this.initialDisplay = initialDisplay;

		GridLayout gblField = new GridLayout();
		gblField.setRows(y + 1);
		gblField.setColumns(x + 1);
		field.setBorder(BorderFactory.createLineBorder(Color.black));
		field.setLayout(gblField);
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				positions[i][j] = new SVGPanel(initialDisplay, Color.white);
				positions[i][j].addMouseListener(this);
				positions[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				positions[i][j].specialInfo = y - i - 1 + ":" + j;
				field.add(positions[i][j], null);
			}
			heights[i] = new JLabel("" + (y - i - 1));
			heights[i].setHorizontalAlignment(SwingConstants.CENTER);
			heights[i].setPreferredSize(new Dimension(10, 10));
			field.add(heights[i], null);
		}

		for (int i = 0; i < x; i++) {
			widths[i] = new JLabel("" + i);
			widths[i].setHorizontalAlignment(SwingConstants.CENTER);
			widths[i].setPreferredSize(new Dimension(10, 10));
			field.add(widths[i], null);
		}

		this.add(field, null);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				for (int i = 0; i < windowListener.size(); i++) {
					windowListener.get(i).windowClosing(e);
				}
			}
		});
		field.setBounds(new Rectangle(0, 0, 600, 800));

		previous.addActionListener(this);
		next.addActionListener(this);
	}

	public void clear() {
		System.out.println("drin");
		for (int i = 0; i < positions.length; i++) {
			for (int j = 0; j < positions[0].length; j++) {
				positions[i][j].setPicture(initialDisplay, Color.white);
			}
		}
	}
	// TODO dynamisch Spieler hinzufügen
	public void setPlayer(Spieler[] spieler, String[] playerSigns) {
		int i;
		for (i = 0; i < playerSigns.length; i++) {
			names[i] = new JLabel(spieler[i].toString());
			names[i].setVerticalAlignment(JLabel.CENTER);

			signs[i] = new SVGPanel(playerSigns[i], new Color(spieler[i].toString().hashCode()));
			signs[i].setSize(new Dimension(20, 20));
			players[i] = new JPanel();
			players[i].setLayout(new GridLayout(1, 2));
			players[i].add(signs[i]);
			players[i].add(names[i]);
			players[i].setBounds(new Rectangle(601, i * 20, 100, 20));
			this.add(players[i], null);
		}
		history.setBounds(new Rectangle(601, (i + 1) * 20, 100, 20));
		previous.setBounds(new Rectangle(601, (i + 2) * 20, 50, 20));
		next.setBounds(new Rectangle(651, (i + 2) * 20, 50, 20));
		previous.setEnabled(false);
		next.setEnabled(false);

		this.add(history, null);
		this.add(previous, null);
		this.add(next, null);
	}

	public void setPicture(int x, int y, String picture, Color col) {
		positions[y][x].setPicture(picture, col);
	}

	public static void main(String[] args) {
		Spieler[] spieler = new Spieler[2];
		String[] signs = new String[2];
		signs[0] = SVGPanel.CIRCLE;
		signs[1] = SVGPanel.CROSS;
		spieler[0] = new Spieler("Phil");
		spieler[1] = new Spieler("MeMe");
		Spielfenster fenster = new Spielfenster(20, 20, SVGPanel.FULL);
		fenster.setPlayer(spieler, signs);
		fenster.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (locked)
			return;// Eingaben werden nicht bearbeitet
		String info = ((SVGPanel) e.getSource()).specialInfo;
		FieldClickedListener[] listener = this.getFieldClickedListener();
		for (int i = 0; i < listener.length; i++) {
			listener[i].zugIsDone(Integer.parseInt(info.substring(0, info.indexOf(":"))), Integer.parseInt(info.substring(info.indexOf(":") + 1)));
		}
		locked = true;
		posX = Integer.parseInt(info.substring(info.indexOf(":") + 1));
		posY = Integer.parseInt(info.substring(0, info.indexOf(":")));
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

	public void addWinListener(WindowListener listener) {
		this.windowListener.add(listener);
	}

	public void addActionListener(ActionListener listener) {
		this.actionListener.add(listener);
	}

	public FieldClickedListener[] getFieldClickedListener() {
		return this.listener.toArray(new FieldClickedListener[0]);
	}

	public final void enableHistory() {
		next.setEnabled(true);
		previous.setEnabled(true);
	}

	public void actionPerformed(ActionEvent arg0) {
		for (int i = 0; i < actionListener.size(); i++) {
			actionListener.get(i).actionPerformed(arg0);
		}
	}
}
