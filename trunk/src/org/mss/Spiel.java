package org.mss;

import org.mss.Spieler;
import org.mss.types.Zug;
import org.mss.utils.Console;

import java.awt.event.WindowListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Spiel extends Spielfeld implements Protokollierbar, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8405472722827386734L;
	protected ArrayList<Spieler> spieler = new ArrayList<Spieler>(2);
	protected boolean track = true;//Per default mal tracken
	protected Spieler[] gewinner = null;
	public abstract Zug frageSpieler(Spieler spieler);
	public abstract Spieler[] spielzug(Zug turn) throws Exception;
	public abstract Spieler[] runde();
	public abstract Spieler folgeSpieler();
	protected ArrayList<Zug> zuege = null;
	protected Spielfenster fenster;
	protected boolean closed;
	protected int zeigeBisZug = 0;

	public Spieler[] durchgang() {
		boolean error =false;
		do {
			error = false;
			try {
				spielzug(frageSpieler(folgeSpieler()));
				if (gewinner != null) {
					return gewinner;
				}
			} catch (Exception e) {
				error = true;
				e.printStackTrace();
				Console.read("Wait a second", 42);
			}
		} while (error);
		do {
			error = false;
			try {
				spielzug(frageSpieler(folgeSpieler()));
				return gewinner;
			} catch (Exception e) {
				error = true;
				e.printStackTrace();
				Console.read("Wait a second", 42);
			}
		} while (error);
		return gewinner;
	}

	public void show() {
		fenster.setVisible(true);
		fenster.setLocationRelativeTo(null);
		fenster.toFront();
	}

	public void dispose() {
		fenster.setVisible(false);
		fenster.dispose();
	}

	public Spieler[] listeSpieler() {
		Iterator<Spieler> it = this.spieler.iterator();
		Spieler[] spieler = new Spieler[this.spieler.size()];
		int i = 0;
		while(it.hasNext()) {
			spieler[i++] = it.next();
		}
		return spieler;
	}

	public void track(boolean track) {
		this.track = track;
	}
	
	public void speicherZug(Zug zug) {
		zuege.add(zug);
	}

	public int findeZug(Zug zug) {
		Zug temp = null;
		Iterator<Zug> it = zuege.iterator();

		while (it.hasNext()) {
			temp = (Zug) it.next();
			if (temp.equals(zug)) {
				return zuege.size() - zuege.indexOf(temp);//So das der Spieler weiß der wie vielte Zug es war;
			}
		}
		return -1;
	}

	public void listeZuege() {
		Iterator<Zug> iterate = zuege.iterator();
		Zug turn = null;
		System.out.println("Spielzüge:");
		while (iterate.hasNext()) {
			turn = iterate.next();
			System.out.println(turn.toString());
		}
	}

	public void zeigeFeld() {
		for (int i = 0; i < hoehe; i++) {
			System.out.print("|");
			for (int j = 0; j < breite; j++) {
				System.out.print("--|");
			}
			System.out.println();
			System.out.print("|");
			for (int j = 0; j < breite; j++) {
				System.out.print(feld[i][j]+ " |");
			}
			System.out.println(hoehe-i-1);
		}
		System.out.print("|");
		for (int j = 0; j < breite; j++) {
			System.out.print((Integer.toString(j).length() ==1)? j+"-|": j+"|");
		}
		System.out.println();
	}
	
	public boolean plusSpieler(Spieler spieler) throws Exception{
		return this.spieler.add(spieler);
	}
	
	public void addListener(WindowListener listener) {
		fenster.addWinListener(listener);
	}
	
	public final void close() {
		closed = true;
		fenster.enableHistory();
	}

	public boolean isClosed() {
		return closed;
	}
}
