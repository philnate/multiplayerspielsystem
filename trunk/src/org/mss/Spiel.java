package org.mss;

import org.mss.Spieler;
import org.mss.types.Zug;
import org.mss.utils.Console;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class Spiel extends Spielfeld implements Protokollierbar {
	protected ArrayList<Spieler> spieler = null;
	protected boolean track = false;
	protected Spieler[] gewinner = null;
	public abstract Zug frageSpieler(Spieler spieler);
	public abstract Spieler[] spielzug(Zug turn) throws Exception;
	public abstract Spieler[] runde();
	public abstract Spieler folgeSpieler();

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

	public Spieler[] listeSpieler() {
		Iterator<Spieler> it = this.spieler.iterator();
		Spieler[] spieler = new Spieler[this.spieler.size()];
		int i = 0;
		while(it.hasNext()) {
			spieler[i++] = it.next();
		}
		return spieler;
	}

	public Spiel() {
		spieler = new ArrayList<Spieler>(2);
	}
	
	public void track(boolean track) {
		this.track = track;
	}
	
	public void speicherZug(Zug zug) {
		System.out.println(zug.toString());
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
}
