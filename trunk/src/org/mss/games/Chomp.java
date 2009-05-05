package org.mss.games;

import java.util.ArrayList;
import java.util.Iterator;

import org.mss.types.Turn;
import org.mss.utils.Console;
import org.mss.Spiel;
import org.mss.Spieler;

public class Chomp extends Spiel {
	private boolean spieler1 = true;
	
	public Chomp(int width, int height) {
		this.width = (width > 0)? width:-width;
		this.height = (height > 0)? height:-height;
//		hoehe = new int[this.width];
		feld = new String[this.height][this.width];
		for (int i = 0; i < this.height; i++) {
			for (int j = 0;j < this.width; j++) {
				feld[i][j] = " ";
			}
		}
//		for (int i = 0; i < this.width; i++) {
//			hoehe[i] = this.height;
//		}
		this.turns = new ArrayList<Turn>(this.width*this.height/3);
	}
	
	public Chomp() {
		this(7,4);
	}

	@Override
	public Spieler[] spielzug(Turn turn) throws Exception {

		if (winner != null) return null;
		if (spieler.size() != 2) {
			throw new Exception ("Es muss genau Zwei Spieler geben!");
		}
		if (turn == null) {
			throw new Exception ("Kein Zug übergeben!");
		}
		if ((turn.getToX() < width && turn.getToX() >= 0) 
				&& (turn.getToY() < height && turn.getToY() >= 0) 
				&& feld[turn.getToY()][turn.getToX()].contentEquals(" ")) {
		System.out.println(turn.toString());
			for (int i = turn.getToX(); i < width; i++) {
				for (int j = turn.getToY(); j < height; j++) {
					if (feld[j][i].contentEquals(" ")) {
						feld[j][i] = (spieler1)? "X":"O";
					}
				}
			}
			spieler1 = !spieler1;
			displayFeld();
		} else {
			System.out.println("Exception");
			throw new Exception ("Zug liegt nicht im Spielfeld, oder Feld ist bereits belegt!");
		}
		checkWin();
		return winner;
	}
	
	@Override
	public Spieler[] durchgang() {
		try {
			spielzug(queryPlayer(nextPlayer()));
			if (winner != null) {
				return winner;
			}
			spielzug(queryPlayer(nextPlayer()));
			return winner;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Spieler[] runde() {
		while (winner == null) {
			durchgang();
		}
		return winner;
	}
	
	@Override
	public Spieler nextPlayer() {
		if (spieler1) {
			return spieler.get(0);
		} else {
			return spieler.get(1);
		}
	}

	@Override
	public Turn queryPlayer(Spieler spieler) {
		//displayFeld();
		if (spieler.isComp()) {
			return kI(spieler);
		}
		System.out.println(spieler.toString() + " du bist dran:");
		System.out.println("Wähle eine Spalte:");
		int col = Console.read(-1);
		System.out.println("Wähle eine Zeile:");
		int row = Console.read(-1);
		return new Turn(spieler, 0,0, col, height-1 - row);
	}

	private Turn kI(Spieler spieler) {
		return new Turn(spieler, 0,0,0,0);
	}
	
	private void checkWin() {
		switch (feld[0][0].charAt(0)) {
		case 'X': winner = new Spieler[1];
			winner[0] = spieler.get(1);
			break;
		case 'O': winner = new Spieler[1];
			winner[0] = spieler.get(0);
			break;
		default: break;
		}
	}
	public void listTurns() {
		Iterator<Turn> iterate = turns.iterator();
		Turn turn = null;
		System.out.println("Spielzüge:");
		while (iterate.hasNext()) {
			turn = iterate.next();
			System.out.println(turn.getSpieler().toString() + " setzt auf die Position: X:" + turn.getToX() + " Y:" + turn.getToY());
		}
	}
	@Override
	public boolean addPlayer(Spieler spieler) throws Exception {
		if (this.spieler.size() == 2) {
			throw new Exception("Maximale Zahl von Spielern erreicht!");
		}
		return this.spieler.add(spieler);
	}
}