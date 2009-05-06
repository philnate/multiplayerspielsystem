package org.mss.games;
import org.mss.Spiel;
import org.mss.types.Turn;
import org.mss.Spieler;

import java.util.ArrayList;
import java.util.Iterator;
import org.mss.utils.Console;

public final class Viergewinnt extends Spiel /*implements Protokollierbar*/ {
	private boolean spieler1 = true;
	private int[] hoehe;


	public Viergewinnt(int width, int height) {
		this.width = (width > 0)? width:-width;
		this.height = (height > 0)? height:-height;
		hoehe = new int[this.width];
		feld = new String[this.height][this.width];
		for (int i = 0; i < this.height; i++) {
			for (int j = 0;j < this.width; j++) {
				feld[i][j] = " ";
			}
		}
		for (int i = 0; i < this.width; i++) {
			hoehe[i] = this.height;
		}
		this.turns = new ArrayList<Turn>(this.width*this.height);
	}

	public Viergewinnt() {
		this(7, 6);
	}

	@Override
	public Spieler[] runde() {
		// Gibt entweder den Gewinner oder alle am Remi beteiligten Spieler zurück
		while (winner == null) {
			durchgang();
		}
		return winner;
	}

	@Override
	public Spieler[] durchgang() {
		//displayFeld();
		try {
			spielzug(queryPlayer(nextPlayer()));
			if (winner != null) {
				return winner;
			}
			spielzug(queryPlayer(nextPlayer()));
			return winner;
		} catch (Exception e) {
			e.printStackTrace();
			Console.read(32);
			return null;
		}
	}

	@Override
	public Spieler[] spielzug(Turn turn) throws Exception{
		if (winner != null) return null;//jemand hat bereits gewonnen also aufhören mit weiterspielen
		if (spieler.size() != 2) {
			throw new Exception("Es muss genau 2 Spieler geben!");
		}
		
		if (turn == null) {
			throw new Exception("Kein Zug übergeben!");
		}
		try {
			if (hoehe[turn.getToX()] > 0) {
				feld[--hoehe[turn.getToX()]][turn.getToX()] = (spieler1)? "X":"O";
				spieler1 = !spieler1;
				if (track) {
					addTurn(turn);
				}
			} else {
				throw new Exception("Maximale Höhe erreicht in dieser Reihe");
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new Exception("Zug liegt nicht im Spielfeld!");
		}
		
		this.displayFeld();
		//Sobald ein Spieler gewonnen hat wird kein Spieler mehr zurück gegeben
		checkWin(this.feld);
		return winner;
	}
	
	public void listTurns() {
		Iterator<Turn> iterate = turns.iterator();
		Turn turn = null;
		System.out.println("Spielzüge:");
		while (iterate.hasNext()) {
			turn = iterate.next();
			System.out.println(turn.getSpieler().toString() + " setzt in die Reihe: " + turn.getToX());
		}
	}

	public boolean removeTurn() {
		if (turns.size() != 0) {
			Turn turn = turns.get(turns.size()-1);
			feld[hoehe[turn.getToX()]--][turn.getToX()] = " ";
			turns.remove(0);
			return true;
		} else {
			return false;
		}
	}
	public Spieler nextPlayer() {
		if (spieler1) {
			return spieler.get(0);
		} else {
			return spieler.get(1);
		}
	}

	public Turn queryPlayer(Spieler spieler) {
		//displayFeld();
		if (spieler.isComp()) {
			return kI(spieler);
		}
		System.out.println(spieler.toString() + " du bist dran:");
		System.out.println("Wähle eine Spalte:");
		return new Turn(spieler, 0,0, Console.read(-1), 0);
	}
	
	public boolean addPlayer(Spieler spieler) throws Exception {
		if (this.spieler.size() == 2) {
			throw new Exception("Maximale Zahl von Spielern erreicht!");
		}
		return this.spieler.add(spieler);
	}
	
	private Turn kI(Spieler spieler) {
		String player = (spieler1)? "XXXX":"OOOO";
		String enemy = (spieler1)? "OOOO":"XXXX";

		int[] setTo = new int[width];//Wertung für die einzelnen Spalten
		int curPos;//Tempspeicher für die aktuelle Position des gesuchten Strings
		String check = "";
	//->Vertikale Prüfen
		for (int i = 0; i < width; i++) {
			check = "";
			for (int j = 0; j < height; j++) {
				check += feld[j][i];
			}
		//->Eigene Gewinnchancen prüfen
			//Prüfen ob irgendwo 4 Vertikal gemacht werden kann, mit 5% wird das jedoch übersehn
			if (check.indexOf(" " +player.substring(1)) != -1 && (Math.random() < .95)) {
				setTo[i] += 100;
			}
			//3er Vertikal möglich
			if (check.indexOf("  "+player.substring(2)) != -1 && (Math.random() < .95)) {
				setTo[i] += 15;
			}
			//2er Vertikal möglich
			if (check.indexOf("   "+ player.substring(3)) != -1) {
				setTo[i] += 10;
			}
			
	//->Gegner behindern
			//Verhindern eines 4er
			if (check.indexOf(" " + enemy.substring(1)) != -1 && (Math.random() < .95)) {
				setTo[i] += 90;
			}
			//Verhindern eines 3er
			if (check.indexOf("  " + enemy.substring(2)) != -1 && (Math.random() < .4)) {
				setTo[i] += 15;
			}
		}
	//->Horizontale Prüfen
		for (int j = 0; j < height; j++) {
			check = "";
			for (int i = 0; i < width; i++) {
				check += feld[j][i];
			}
		//->Eigene Gewinnchancen prüfen
			//Prüfen auf _???
			if (((curPos = check.indexOf(" " + player.substring(1))) != -1)) {
				if (hoehe[curPos]-1 == j && (Math.random() < .95)) {
					setTo[curPos] += 100;
				}
			}
			//Prüfen auf ???_
			if (((curPos = check.indexOf(player.substring(1) + " ")) != -1)) {
				if (hoehe[curPos+3]-1 == j && (Math.random() < .95)) {
					setTo[curPos+3] += 100;
				}
			}
			//Prüfen auf ?_??
			if (((curPos = check.indexOf(player.substring(3) + " " + player.substring(2))) != -1)) {
				if (hoehe[curPos+1]-1 == j && (Math.random() < .95)) {
					setTo[curPos+1] += 100;
				}
			}
			//Prüfen auf ??_?
			if (((curPos = check.indexOf(player.substring(2) + " " + player.substring(3))) != -1)) {
				if (hoehe[curPos+2]-1 == j && (Math.random() < .95)) {
					setTo[curPos+2] += 100;
				}
			}
			//Prüfen auf ?_?
			if ((curPos = check.indexOf(player.substring(3) + " " + player.substring(3))) != -1 && (Math.random() < 0.4)) {
				if (hoehe[curPos+1]-1 == j) {
					setTo[curPos+1] += 15;
				}
			}

			//Prüfen auf __?
			if ((curPos = check.indexOf("  " + player.substring(2))) != -1 && (Math.random() < 0.)) {
				if (hoehe[curPos]-1 == j && Math.random() < 0.5) {
					setTo[curPos] += 10;
				} else if (hoehe[curPos+1]-1 == j && Math.random() < 0.5) {
					setTo[curPos+1] += 10;
				}
			}
			
			//Prüfen auf ?__
			if ((curPos = check.indexOf(player.substring(2) + "  ")) != -1 && (Math.random() < 0.)) {
				if (hoehe[curPos+1]-1 == j && Math.random() < 0.5) {
					setTo[curPos] += 10;
				} else if (hoehe[curPos+2]-1 == j && Math.random() < 0.5) {
					setTo[curPos+1] += 10;
				}
			}
			//Prüfen auf _?_
			if ((curPos = check.indexOf(" " + player.substring(3) + " ")) != -1 && (Math.random() < 0.)) {
				if (hoehe[curPos]-1 == j && Math.random() < 0.5) {
					setTo[curPos] += 10;
				} else if (hoehe[curPos+2]-1 == j && Math.random() < 0.5) {
					setTo[curPos+1] += 10;
				}
			}
		//->Gegner behindern
			//Prüfen auf _???
			if (((curPos = check.indexOf(" " + enemy.substring(1))) != -1)) {
				if (hoehe[curPos]-1 == j && (Math.random() < .95)) {
					setTo[curPos] += 100;
				}
			}
			//Prüfen auf ???_
			if (((curPos = check.indexOf(enemy.substring(1) + " ")) != -1)) {
				if (hoehe[curPos+3]-1 == j && (Math.random() < .95)) {
					setTo[curPos+3] += 100;
				}
			}
			
			//Prüfen auf _??_
			if ((curPos = check.indexOf(" " + enemy.substring(2) + " ")) != -1 && Math.random() < 0.7) {
				if (hoehe[curPos]-1 == j) {
					setTo[curPos] += 20;
				}
				if (hoehe[curPos+4]-1 == j) {
					setTo[curPos+4] += 20;
				}
			}
			
			//Prüfen auf ?_??
			if (((curPos = check.indexOf(enemy.substring(3) + " " + enemy.substring(2))) != -1)) {
				if (hoehe[curPos+1]-1 == j && (Math.random() < .95)) {
					setTo[curPos+1] += 100;
				}
			}
			
			//Prüfen auf ??_?
			if (((curPos = check.indexOf(enemy.substring(2) + " " + enemy.substring(3))) != -1)) {
				if (hoehe[curPos+2]-1 == j && (Math.random() < .95)) {
					setTo[curPos+2] += 100;
				}
			}
			
			//Prüfen auf ?_?
			if ((curPos = check.indexOf(enemy.substring(3) + " " + enemy.substring(3))) != -1 && (Math.random() < 0.4)) {
				if (hoehe[curPos+1]-1 == j) {
					setTo[curPos+1] += 20;
				}
			}
		}
		//TODO Diagonalen prüfen
		int maxVal = 0;
		for (int i = 0; i < width; i++) {
			maxVal = (maxVal < setTo[i])? setTo[i]:maxVal;
		}
		System.out.println("maxWertung:"+maxVal);
		int[] pos = new int[width];//Felder welche ausgewählt werden sollten
		int possible = 0;//Zahl der Felder zum auswählen
		for (int i = 0; i < width; i++) {
			if (setTo[i] == maxVal) {
				pos[possible++] = i;
			}
		}
		
		int take = pos[(int) (Math.random()*possible)];
		//Prüfen ob Gegner dadurch gewinnen würde
		String[][] tempfeld = this.feld.clone();
		if (hoehe[take] < height -3) {
			tempfeld[hoehe[take]][take] = player.substring(3);
			tempfeld[hoehe[take]+1][take] = enemy.substring(3);
			checkWin(tempfeld);
			if (winner != null && winner.length == 1 && winner[0] != nextPlayer()) {
				winner = null;
			}
			//TODO besser auswahl treffen
			take = (int) ((width-1)*Math.random());
		}
		System.out.println("Position zum Setzen:"+take);
		return new Turn(spieler,0, 0, take,0);
	}
	
	private void checkWin(String[][] feld) {
		//Zeilen generieren
		String toCheck = "";
		for (int i= 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				toCheck += feld[i][j];
			}
			toCheck += ";";
		}
		//Spalten generieren
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				toCheck += feld[j][i];
			}
			toCheck += ";";
		}
		// /-Diagonalen bestimmen
		for (int i = 0; i < width; i++) {
			int j = 0;
			int temp = i;
			while (temp >= 0 && j < height) {
				toCheck += feld[j][temp];
				j++;
				temp--;
			}
			toCheck += ";"; 
		}
		//untere Diagonalen
		for (int j = 1; j < height; j++) {
			int i = width-1;
			int temp = j;
			while (i >= 0 && temp < height) {
				toCheck += feld[temp++][i--];
			}
			toCheck += ";";
		}

		// \-Diagonalen bestimmen
		for (int i = width; i > 0; i--) {
			int j = 0;
			int temp = i;
			while (temp < width) {
				toCheck += feld[j][temp];
				j++;
				temp++;
			}
			toCheck += ";"; 
		}
		//untere Diagonalen
		for (int j = 0; j < height; j++) {
			int i = 0;
			int temp = j;
			
			while (i < width && temp < height) {
				toCheck += feld[temp][i];
				i++;
				temp++;
			}
			toCheck += ";";
		}

		if (toCheck.contains("XXXX")) {
			winner = new Spieler[1];
			winner[0] = spieler.get(0);
		} else if (toCheck.contains("OOOO")) {
			winner = new Spieler[1];
			winner[0] = spieler.get(1);
		} else if (remi()) {
			winner = new Spieler[2];
			winner[0] = this.spieler.get(0);
			winner[1] = this.spieler.get(1);
		} else {
			winner = null;
		}
	}
	
	private boolean remi() {
		for (int i = 0; i < hoehe.length; i++) {
			if (hoehe[i] > 0) {
				return false;//noch nicht alle Felder belegt also kann einer noch gewinnen
			}
		}
		return true;
	}
}