package org.mss.games;

import org.mss.Spiel;
import org.mss.Spielfenster;
import org.mss.types.Zug;
import org.mss.Spieler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import org.mss.utils.Console;
import org.mss.windows.svg.SVGPanel;

public final class Viergewinnt extends Spiel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4717226035090425060L;
	private boolean spieler1 = true;
	private int[] freieFelder;
	private final static String[] playerSigns = new String[2];

	public Viergewinnt(int breite, int hoehe) {
		this.breite = (breite > 0)? breite:-breite;
		this.hoehe = (hoehe > 0)? hoehe:-hoehe;
		if (this.breite < 4) {
			this.breite = 4;
		}
		if (this.hoehe < 4) {
			this.hoehe = 4;
		}
		freieFelder = new int[this.breite];
		feld = new String[this.hoehe][this.breite];
		for (int i = 0; i < this.hoehe; i++) {
			for (int j = 0;j < this.breite; j++) {
				feld[i][j] = " ";
			}
		}
		zuege = new ArrayList<Zug>(hoehe*breite/2);
		for (int i = 0; i < this.breite; i++) {
			freieFelder[i] = this.hoehe;
		}

		playerSigns[0] = SVGPanel.CIRCLE;
		playerSigns[1] = SVGPanel.CROSS;

		fenster = new Spielfenster(breite,hoehe,SVGPanel.FULL);
	}

	public Viergewinnt() {
		this(7, 6);
	}

	public void show() {
		fenster.setTitle("Viergewinnt:" + spieler.get(0).getName() + "-" +spieler.get(1).getName());
		super.show();
	}

	@Override
	public Spieler[] runde() {
		// Gibt entweder den Gewinner oder alle am Remi beteiligten Spieler zurück
		while (gewinner == null) {
			durchgang();
		}
		return gewinner;
	}

	@Override
	public Spieler[] spielzug(Zug turn) throws Exception{
		if (gewinner != null) return null;//jemand hat bereits gewonnen also aufhören mit weiterspielen
		if (spieler.size() != 2) {
			throw new Exception("Es muss genau 2 Spieler geben!");
		}

		if (turn == null) {
			throw new Exception("Kein Zug übergeben!");
		}
		try {
			if (freieFelder[turn.getAufX()] > 0) {
				fenster.setPicture(turn.getAufX(), freieFelder[turn.getAufX()]-1, (spieler1)? playerSigns[0]:playerSigns[1], new Color(turn.getSpieler().toString().hashCode()));
				feld[--freieFelder[turn.getAufX()]][turn.getAufX()] = spieler1? "X":"O";
				spieler1 = !spieler1;
				if (track) {
					speicherZug(turn);
				}
//				this.zeigeFeld();
				checkWin(this.feld);
			} else {
				if (turn.getSpieler().isComp()) {
					spielzug(kI(turn.getSpieler()));
				} else {
					throw new Exception("Maximale Höhe in dieser Reihe erreicht!");
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new Exception("Zug liegt nicht im Spielfeld!");
		}
		return gewinner;
	}
	
	public void listeZuege() {
		Iterator<Zug> iterate = zuege.iterator();
		Zug zug = null;
		System.out.println("Spielzüge:");
		while (iterate.hasNext()) {
			zug = iterate.next();
			System.out.println(zug.getSpieler().toString() + " setzt in die Reihe: " + zug.getAufX());
		}
	}

	public boolean entferneZug() {
		if (zuege.size() != 0) {
			Zug zug = zuege.get(zuege.size()-1);
			feld[freieFelder[zug.getAufX()]++][zug.getAufX()] = " ";
			zuege.remove(0);
			return true;
		} else {
			return false;
		}
	}
	
	public Spieler folgeSpieler() {
		if (spieler1) {
			return spieler.get(0);
		} else {
			return spieler.get(1);
		}
	}

	public Zug frageSpieler(Spieler spieler) {
		if (spieler.isComp()) {
			return kI(spieler);
		}
		fenster.setLocked(false);
		try {
			synchronized (fenster) {
				fenster.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Zug(spieler, 0,0, fenster.getPosX(),0);
	}
	
	public boolean plusSpieler(Spieler spieler) throws Exception {
		if (this.spieler.size() == 2) {
			throw new Exception("Maximale Zahl von Spielern erreicht!");
		}
		return this.spieler.add(spieler);
	}
	
	private Zug kI(Spieler spieler) {
		String player = (spieler1)? "XXXX":"OOOO";
		String enemy = (spieler1)? "OOOO":"XXXX";

		try {
			Thread.sleep((long) (Math.random()*100000)/100 + 500);
			//Computer überlegt
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int[] setzeIn = new int[breite];//Wertung für die einzelnen Spalten
		int aktPos;//Tempspeicher für die aktuelle Position des gesuchten Strings
		String pruefe = "";
	//->Vertikale Prüfen
		for (int i = 0; i < breite; i++) {
			pruefe = "";
			for (int j = 0; j < hoehe; j++) {
				pruefe += feld[j][i];
			}
		//->Eigene Gewinnchancen prüfen
			//Prüfen ob irgendwo 4 Vertikal möglich
			if (pruefe.indexOf(" " +player.substring(1)) != -1 && (Math.random() < .95)) {
				setzeIn[i] += 100;
			}
			//3er Vertikal möglich
			if (pruefe.indexOf("  "+player.substring(2)) != -1 && (Math.random() < .8)) {
				setzeIn[i] += 15;
			}
			//2er Vertikal möglich
			if (pruefe.indexOf("   "+ player.substring(3)) != -1) {
				setzeIn[i] += 10;
			}
			
	//->Gegner behindern
			//Verhindern eines 4er
			if (pruefe.indexOf(" " + enemy.substring(1)) != -1 && (Math.random() < .95)) {
				setzeIn[i] += 90;
			}
			//Verhindern eines 3er
			if (pruefe.indexOf("  " + enemy.substring(2)) != -1 && (Math.random() < .5)) {
				setzeIn[i] += 15;
			}
		}
	//->Horizontale Prüfen
		for (int j = 0; j < hoehe; j++) {
			pruefe = "";
			for (int i = 0; i < breite; i++) {
				pruefe += feld[j][i];
			}
		//->Eigene Gewinnchancen prüfen
			//Prüfen auf _???
			if (((aktPos = pruefe.indexOf(" " + player.substring(1))) != -1)) {
				if (freieFelder[aktPos]-1 == j && (Math.random() < .95)) {
					setzeIn[aktPos] += 100;
				}
			}
			//Prüfen auf ???_
			if (((aktPos = pruefe.indexOf(player.substring(1) + " ")) != -1)) {
				if (freieFelder[aktPos+3]-1 == j && (Math.random() < .95)) {
					setzeIn[aktPos+3] += 100;
				}
			}
			
			//Prüfen auf _??_
			if ((aktPos = pruefe.indexOf(" " + player.substring(2) + " ")) != -1 && Math.random() < 0.7) {
				if (freieFelder[aktPos]-1 == j) {
					setzeIn[aktPos] += 50;
				}
				if (freieFelder[aktPos+3]-1 == j) {
					setzeIn[aktPos+3] += 50;
				}
			}
			
			//Prüfen auf ?_??
			if (((aktPos = pruefe.indexOf(player.substring(3) + " " + player.substring(2))) != -1)) {
				if (freieFelder[aktPos+1]-1 == j && (Math.random() < .95)) {
					setzeIn[aktPos+1] += 100;
				}
			}
			//Prüfen auf ??_?
			if (((aktPos = pruefe.indexOf(player.substring(2) + " " + player.substring(3))) != -1)) {
				if (freieFelder[aktPos+2]-1 == j && (Math.random() < .95)) {
					setzeIn[aktPos+2] += 100;
				}
			}
			//Prüfen auf ?_?
			if ((aktPos = pruefe.indexOf(player.substring(3) + " " + player.substring(3))) != -1 && (Math.random() < 0.4)) {
				if (freieFelder[aktPos+1]-1 == j) {
					setzeIn[aktPos+1] += 15;
				}
			}

			//Prüfen auf __?
			if ((aktPos = pruefe.indexOf("  " + player.substring(2))) != -1 && (Math.random() < 0.5)) {
				if (freieFelder[aktPos]-1 == j && Math.random() < 0.5) {
					setzeIn[aktPos] += 10;
				} else if (freieFelder[aktPos+1]-1 == j) {
					setzeIn[aktPos+1] += 10;
				}
			}
			
			//Prüfen auf ?__
			if ((aktPos = pruefe.indexOf(player.substring(2) + "  ")) != -1 && (Math.random() < 0.5)) {
				if (freieFelder[aktPos+1]-1 == j && Math.random() < 0.5) {
					setzeIn[aktPos] += 10;
				} else if (freieFelder[aktPos+2]-1 == j) {
					setzeIn[aktPos+1] += 10;
				}
			}
			//Prüfen auf _?_
			if ((aktPos = pruefe.indexOf(" " + player.substring(3) + " ")) != -1 && (Math.random() < 0.5)) {
				if (freieFelder[aktPos]-1 == j && Math.random() < 0.5) {
					setzeIn[aktPos] += 10;
				} else if (freieFelder[aktPos+2]-1 == j) {
					setzeIn[aktPos+1] += 10;
				}
			}
		//->Gegner behindern
			//Prüfen auf _???
			if (((aktPos = pruefe.indexOf(" " + enemy.substring(1))) != -1)) {
				if (freieFelder[aktPos]-1 == j && (Math.random() < .95)) {
					setzeIn[aktPos] += 90;
				}
			}
			//Prüfen auf ???_
			if (((aktPos = pruefe.indexOf(enemy.substring(1) + " ")) != -1)) {
				if (freieFelder[aktPos+3]-1 == j && (Math.random() < .95)) {
					setzeIn[aktPos+3] += 90;
				}
			}
			
			//Prüfen auf _??_
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(2) + " ")) != -1 && Math.random() < 0.7) {
				if (freieFelder[aktPos]-1 == j) {
					setzeIn[aktPos] += 20;
				}
				if (freieFelder[aktPos+3]-1 == j) {
					setzeIn[aktPos+3] += 20;
				}
			}
			
			//Prüfen auf ?_??
			if (((aktPos = pruefe.indexOf(enemy.substring(3) + " " + enemy.substring(2))) != -1)) {
				if (freieFelder[aktPos+1]-1 == j && (Math.random() < .95)) {
					setzeIn[aktPos+1] += 100;
				}
			}
			
			//Prüfen auf ??_?
			if (((aktPos = pruefe.indexOf(enemy.substring(2) + " " + enemy.substring(3))) != -1)) {
				if (freieFelder[aktPos+2]-1 == j && (Math.random() < .95)) {
					setzeIn[aktPos+2] += 100;
				}
			}
			
			//Prüfen auf ?_?
			if ((aktPos = pruefe.indexOf(enemy.substring(3) + " " + enemy.substring(3))) != -1 && (Math.random() < 0.4)) {
				if (freieFelder[aktPos+1]-1 == j) {
					setzeIn[aktPos+1] += 20;
				}
			}
		}

		//-> /-Diagonale Prüfen
		pruefe = "";
		// /-Diagonalen bestimmen
		for (int i = 0; i < breite; i++) {
			int j = 0;
			int temp = i;
			while (temp >= 0 && j < hoehe) {
				pruefe += feld[j][temp];
				j++;
				temp--;
			}
			//Gegner behindern
			//Prüfen auf ???_
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[i-aktPos] == hoehe - aktPos -1 ) {
					setzeIn[i-aktPos] +=90;
				}
			}
			//Prüfen auf ??_
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(2))) != -1 && Math.random() < .5) {
				if (freieFelder[i-aktPos] == hoehe - aktPos -1 ) {
					setzeIn[i-aktPos] +=20;
				}
			}
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(enemy.substring(1) + " ")) != -1 && Math.random() < .95) {
				if (freieFelder[i-aktPos-3] == aktPos + 3 + 1) {
					setzeIn[i-aktPos-3] +=90;
				}
			}
			//Prüfen auf ?_??
			if (((aktPos = pruefe.indexOf(enemy.substring(2) + " " + enemy.substring(3))) != -1 && Math.random() < .95)) {
				if (freieFelder[i-aktPos-2] == aktPos + 2 + 1) {
					setzeIn[i-aktPos-2] +=90;
				}
			}
			//Prüfen auf ??_?
			if (((aktPos = pruefe.indexOf(enemy.substring(3) + " " + enemy.substring(2))) != -1 && Math.random() < .95)) {
				if (freieFelder[i-aktPos-1] == aktPos + 1 + 1) {
					setzeIn[i-aktPos-1] +=90;
				}
			}
			//Prüfen auf _??
			if ((aktPos = pruefe.indexOf(enemy.substring(2) + " ")) != -1 && Math.random() < .5) {
				if (freieFelder[i-aktPos-2] == aktPos + 2 + 1) {
					setzeIn[i-aktPos-2] +=20;
				}
			}

			//Selber gewinnen
			if ((aktPos = pruefe.indexOf(" " + player.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[i-aktPos] == hoehe - aktPos -1 ) {
					setzeIn[i-aktPos] +=100;
				}
			}
			//Prüfen auf ??_
			if ((aktPos = pruefe.indexOf(" " + player.substring(2))) != -1 && Math.random() < .8) {
				if (freieFelder[i-aktPos] == hoehe - aktPos -1 ) {
					setzeIn[i-aktPos] +=50;
				}
			}
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(player.substring(1) + " ")) != -1 && Math.random() < .95) {
				if (freieFelder[i-aktPos-3] == aktPos + 3 + 1) {
					setzeIn[i-aktPos-3] +=100;
				}
			}
			//Prüfen auf ?_??
			if (((aktPos = pruefe.indexOf(player.substring(2) + " " + player.substring(3))) != -1 && Math.random() < .95)) {
				if (freieFelder[i-aktPos-2] == aktPos + 2 + 1) {
					setzeIn[i-aktPos-2] +=100;
				}
			}
			//Prüfen auf ??_?
			if (((aktPos = pruefe.indexOf(player.substring(3) + " " + player.substring(2))) != -1 && Math.random() < .95)) {
				if (freieFelder[i-aktPos-1] == aktPos + 1 + 1) {
					setzeIn[i-aktPos-1] +=100;
				}
			}
			//Prüfen auf _??
			if ((aktPos = pruefe.indexOf(player.substring(2) + " ")) != -1 && Math.random() < .8) {
				if (freieFelder[i-aktPos-2] == aktPos + 2 + 1) {
					setzeIn[i-aktPos-2] +=50;
				}
			}
			pruefe = ""; 
		}
		//untere  /-Diagonalen
		for (int j = 1; j < hoehe; j++) {
			int i = breite-1;
			int temp = j;
			while (i >= 0 && temp < hoehe) {
				pruefe += feld[temp++][i--];
			}
			//Gegner behindern
			//Prüfen auf ???_
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[breite-1-aktPos] == j + aktPos +1 ) {
					setzeIn[breite-1-aktPos] +=90;
				}
			}
			//Prüfen auf ??_
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(2))) != -1 && Math.random() < .8) {
				if (freieFelder[breite-1-aktPos] == j + aktPos +1 ) {
					setzeIn[breite-1-aktPos] +=20;
				}
			}
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(enemy.substring(1) + " ")) != -1 && Math.random() < .95) {
				if (freieFelder[breite-1-aktPos-3] == j + aktPos + 3 + 1 ) {
					setzeIn[breite-1-aktPos-3] +=90;
				}
			}
			//Prüfen auf _??
			if ((aktPos = pruefe.indexOf(enemy.substring(2) + " ")) != -1 && Math.random() < .8) {
				if (freieFelder[breite-1-aktPos-2] == j + aktPos + 2 + 1 ) {
					setzeIn[breite-1-aktPos-2] +=20;
				}
			}
			//Prüfen auf ?_??
			if ((aktPos = pruefe.indexOf(enemy.substring(2) + " " + enemy.substring(3))) != -1 && Math.random() < .95) {
				if (freieFelder[breite-1-aktPos-2] == j + aktPos + 2 + 1 ) {
					setzeIn[breite-1-aktPos-2] +=90;
				}
			}
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(enemy.substring(3) + " " + enemy.substring(2))) != -1 && Math.random() < .95) {
				if (freieFelder[breite-1-aktPos-1] == j + aktPos + 1 + 1 ) {
					setzeIn[breite-1-aktPos-1] +=90;
				}
			}
			//Eigene Gewinnchancen
			//Prüfen auf ???_
			if ((aktPos = pruefe.indexOf(" " + player.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[breite-1-aktPos] == j + aktPos +1 ) {
					setzeIn[breite-1-aktPos] +=90;
				}
			}
			//Prüfen auf ??_
			if ((aktPos = pruefe.indexOf(" " + player.substring(2))) != -1 && Math.random() < .8) {
				if (freieFelder[breite-1-aktPos] == j + aktPos +1 ) {
					setzeIn[breite-1-aktPos] +=50;
				}
			}
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(player.substring(1) + " ")) != -1 && Math.random() < .95) {
				if (freieFelder[breite-1-aktPos-3] == j + aktPos + 3 + 1 ) {
					setzeIn[breite-1-aktPos-3] +=100;
				}
			}
			//Prüfen auf _??
			if ((aktPos = pruefe.indexOf(player.substring(2) + " ")) != -1 && Math.random() < .8) {
				if (freieFelder[breite-1-aktPos-2] == j + aktPos + 2 + 1 ) {
					setzeIn[breite-1-aktPos-2] +=50;
				}
			}
			//Prüfen auf ?_??
			if ((aktPos = pruefe.indexOf(player.substring(2) + " " + player.substring(3))) != -1 && Math.random() < .95) {
				if (freieFelder[breite-1-aktPos-2] == j + aktPos + 2 + 1 ) {
					setzeIn[breite-1-aktPos-2] +=100;
				}
			}
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(player.substring(3) + " " + player.substring(2))) != -1 && Math.random() < .95) {
				if (freieFelder[breite-1-aktPos-1] == j + aktPos + 1 + 1 ) {
					setzeIn[breite-1-aktPos-1] +=100;
				}
			}
			pruefe = "";
		}
		
		// \-Diagonalen bestimmen
		for (int i = breite; i > 0; i--) {
			int j = 0;
			int temp = i;
			while (temp < breite) {
				pruefe += feld[j][temp];
				j++;
				temp++;
			} 
			//Gegner behindern
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[i+aktPos] == hoehe-aktPos -1) {
					setzeIn[i+aktPos] += 90;
				}
			}
			//Prüfen auf _??
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(2))) != -1 && Math.random() < .8) {
				if (freieFelder[i+aktPos] == hoehe-aktPos -1) {
					setzeIn[i+aktPos] += 20;
				}
			}
			//Prüfen auf ??_
			if ((aktPos = pruefe.indexOf(enemy.substring(2) + " ")) != -1 && Math.random() < .8) {
				if (freieFelder[i+aktPos+2] == aktPos +2+1) {
					setzeIn[i+aktPos+2] += 20;
				}
			}
			//Prüfen auf ??_? 
			if ((aktPos = pruefe.indexOf(enemy.substring(2) + " " + enemy.substring(3))) != -1 && Math.random() < .95) {
				if (freieFelder[i+aktPos+2] == aktPos +2+1) {
					setzeIn[i+aktPos+2] += 90;
				}
			}
			//Prüfen auf ?_?? 
			if ((aktPos = pruefe.indexOf(enemy.substring(3) + " " + enemy.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[i+aktPos+1] == aktPos +1+1) {
					setzeIn[i+aktPos+1] += 90;
				}
			}
			//Prüfen auf ???_ 
			if ((aktPos = pruefe.indexOf(enemy.substring(1) + " ")) != -1 && Math.random() < .95) {
				if (freieFelder[i+aktPos+3] == aktPos +3+1) {
					setzeIn[i+aktPos+3] += 90;
				}
			}
			//Eigene Gewinnchancen
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(" " + player.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[i+aktPos] == hoehe-aktPos -1) {
					setzeIn[i+aktPos] += 100;
				}
			}
			//Prüfen auf _??
			if ((aktPos = pruefe.indexOf(" " + player.substring(2))) != -1 && Math.random() < .8) {
				if (freieFelder[i+aktPos] == hoehe-aktPos -1) {
					setzeIn[i+aktPos] += 50;
				}
			}
			//Prüfen auf ??_
			if ((aktPos = pruefe.indexOf(player.substring(2) + " ")) != -1 && Math.random() < .8) {
				if (freieFelder[i+aktPos+2] == aktPos +2+1) {
					setzeIn[i+aktPos+2] += 50;
				}
			}
			//Prüfen auf ??_? 
			if ((aktPos = pruefe.indexOf(player.substring(2) + " " + player.substring(3))) != -1 && Math.random() < .95) {
				if (freieFelder[i+aktPos+2] == aktPos +2+1) {
					setzeIn[i+aktPos+2] += 100;
				}
			}
			//Prüfen auf ?_?? 
			if ((aktPos = pruefe.indexOf(player.substring(3) + " " + player.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[i+aktPos+1] == aktPos +1+1) {
					setzeIn[i+aktPos+1] += 100;
				}
			}
			//Prüfen auf ???_ 
			if ((aktPos = pruefe.indexOf(player.substring(1) + " ")) != -1 && Math.random() < .95) {
				if (freieFelder[i+aktPos+3] == aktPos +3+1) {
					setzeIn[i+aktPos+3] += 100;
				}
			}
			pruefe = "";
		}
		//untere \-Diagonalen
		for (int j = 0; j < hoehe; j++) {
			int i = 0;
			int temp = j;
			
			while (i < breite && temp < hoehe) {
				pruefe += feld[temp][i];
				i++;
				temp++;
			}
			//Gegner behindern
			//Prüfen auf ???_
			if ((aktPos = pruefe.indexOf(enemy.substring(1) + " ")) != -1 && Math.random() < .95) {
				if (freieFelder[aktPos+3] == j+aktPos +3 +1) {
					setzeIn[aktPos+3] += 90;
				}
			}
			//Prüfen auf ??_
			if ((aktPos = pruefe.indexOf(enemy.substring(2) + " ")) != -1 && Math.random() < .8) {
				if (freieFelder[aktPos+2] == j+aktPos +2 +1) {
					setzeIn[aktPos+2] += 20;
				}
			}
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[aktPos] == j+aktPos +1) {
					setzeIn[aktPos] += 90;
				}
			}
			//Prüfen auf _??
			if ((aktPos = pruefe.indexOf(" " + enemy.substring(2))) != -1 && Math.random() < .8) {
				if (freieFelder[aktPos] == j+aktPos +1) {
					setzeIn[aktPos] += 20;
				}
			}
			//Prüfen auf ?_??
			if ((aktPos = pruefe.indexOf(enemy.substring(3) + " " + enemy.substring(2))) != -1 && Math.random() < .95) {
				if (freieFelder[aktPos+1] == j+aktPos +1 +1) {
					setzeIn[aktPos+1] += 90;
				}
			}
			//Prüfen auf ?_??
			if ((aktPos = pruefe.indexOf(enemy.substring(2) + " " + enemy.substring(3))) != -1 && Math.random() < .95) {
				if (freieFelder[aktPos+2] == j+aktPos +2 +1) {
					setzeIn[aktPos+2] += 90;
				}
			}
			//Eigene Gewinnchancen
			//Prüfen auf ???_
			if ((aktPos = pruefe.indexOf(player.substring(1) + " ")) != -1 && Math.random() < .95) {
				if (freieFelder[aktPos+3] == j+aktPos +3 +1) {
					setzeIn[aktPos+3] += 90;
				}
			}
			//Prüfen auf ??_
			if ((aktPos = pruefe.indexOf(player.substring(2) + " ")) != -1 && Math.random() < .8) {
				if (freieFelder[aktPos+2] == j+aktPos +2 +1) {
					setzeIn[aktPos+2] += 20;
				}
			}
			//Prüfen auf _???
			if ((aktPos = pruefe.indexOf(" " + player.substring(1))) != -1 && Math.random() < .95) {
				if (freieFelder[aktPos] == j+aktPos +1) {
					setzeIn[aktPos] += 90;
				}
			}
			//Prüfen auf _??
			if ((aktPos = pruefe.indexOf(" " + player.substring(2))) != -1 && Math.random() < .8) {
				if (freieFelder[aktPos] == j+aktPos +1) {
					setzeIn[aktPos] += 20;
				}
			}
			//Prüfen auf ?_??
			if ((aktPos = pruefe.indexOf(player.substring(3) + " " + player.substring(2))) != -1 && Math.random() < .95) {
				if (freieFelder[aktPos+1] == j+aktPos +1 +1) {
					setzeIn[aktPos+1] += 90;
				}
			}
			//Prüfen auf ?_??
			if ((aktPos = pruefe.indexOf(player.substring(2) + " " + player.substring(3))) != -1 && Math.random() < .95) {
				if (freieFelder[aktPos+2] == j+aktPos +2 +1) {
					setzeIn[aktPos+2] += 90;
				}
			}
			pruefe = "";
		}
		
		int maxVal = 0;
		for (int i = 0; i < breite; i++) {
			maxVal = (maxVal < setzeIn[i])? setzeIn[i]:maxVal;
		}
		Console.debug("maxWertung:"+maxVal);
		int[] pos = new int[breite];//Felder welche ausgewählt werden sollten
		int moegliche = 0;//Zahl der Felder zum auswählen
		for (int i = 0; i < breite; i++) {
			if (setzeIn[i] == maxVal) {
				pos[moegliche++] = i;
			}
		}
		
		int nehm = pos[(int) (Math.random()*moegliche)];
		//Prüfen ob Gegner dadurch gewinnen würde
		String[][] tempfeld = new String[hoehe][breite];
		for (int a = 0; a < hoehe; a++) {
			for (int b = 0; b < breite; b++) {
				tempfeld[a][b] = feld[a][b];
			}
		}

		if (freieFelder[nehm] > 1) {
			//System.out.println(take);
			tempfeld[freieFelder[nehm]-1][nehm] = player.substring(3);
			tempfeld[freieFelder[nehm]-2][nehm] = enemy.substring(3);
			checkWin(tempfeld);
			if (gewinner != null && gewinner.length == 1 && gewinner[0] != folgeSpieler()) {
				nehm = (int) ((breite-1)*Math.random());
				Console.debug("Feld Zufällig wählen Gegner gewinnt sonst");
			}
			gewinner = null;
		}
		Console.debug("Position zum Setzen:"+nehm);
		return new Zug(spieler,0, 0, nehm,0);
	}
	
	private void checkWin(String[][] feld) {
		//Zeilen generieren
		String zuPruefen = "";
		for (int i= 0; i < hoehe; i++) {
			for (int j = 0; j < breite; j++) {
				zuPruefen += feld[i][j];
			}
			zuPruefen += ";";
		}
		//Spalten generieren
		for (int i = 0; i < breite; i++) {
			for (int j = 0; j < hoehe; j++) {
				zuPruefen += feld[j][i];
			}
			zuPruefen += ";";
		}
		// /-Diagonalen bestimmen
		for (int i = 0; i < breite; i++) {
			int j = 0;
			int temp = i;
			while (temp >= 0 && j < hoehe) {
				zuPruefen += feld[j][temp];
				j++;
				temp--;
			}
			zuPruefen += ";"; 
		}
		//untere Diagonalen
		for (int j = 1; j < hoehe; j++) {
			int i = breite-1;
			int temp = j;
			while (i >= 0 && temp < hoehe) {
				zuPruefen += feld[temp++][i--];
			}
			zuPruefen += ";";
		}

		// \-Diagonalen bestimmen
		for (int i = breite; i > 0; i--) {
			int j = 0;
			int temp = i;
			while (temp < breite) {
				zuPruefen += feld[j][temp];
				j++;
				temp++;
			}
			zuPruefen += ";"; 
		}
		//untere Diagonalen
		for (int j = 0; j < hoehe; j++) {
			int i = 0;
			int temp = j;
			
			while (i < breite && temp < hoehe) {
				zuPruefen += feld[temp][i];
				i++;
				temp++;
			}
			zuPruefen += ";";
		}

		if (zuPruefen.contains("XXXX")) {
			gewinner = new Spieler[1];
			gewinner[0] = spieler.get(0);
		} else if (zuPruefen.contains("OOOO")) {
			gewinner = new Spieler[1];
			gewinner[0] = spieler.get(1);
		} else if (remi()) {
			gewinner = new Spieler[2];
			gewinner[0] = this.spieler.get(0);
			gewinner[1] = this.spieler.get(1);
		} else {
			gewinner = null;
		}
	}
	
	private boolean remi() {
		for (int i = 0; i < freieFelder.length; i++) {
			if (freieFelder[i] > 0) {
				return false;//noch nicht alle Felder belegt also kann einer noch gewinnen
			}
		}
		return true;
	}
}