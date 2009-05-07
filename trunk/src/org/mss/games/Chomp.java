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

		feld = new String[this.height][this.width];
		for (int i = 0; i < this.height; i++) {
			for (int j = 0;j < this.width; j++) {
				feld[i][j] = " ";
			}
		}
//		this.turns = new ArrayList<Turn>(this.width*this.height/3);
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
		setTurn(turn);
		spieler1 = !spieler1;
		displayFeld();
		checkWin();
		return winner;
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
			System.out.println("Computer "+ spieler.getName() + " ist an der Reihe.");
			return kI(spieler);
		}
		System.out.println(spieler.toString() + " du bist dran:");
		System.out.println("Wähle eine Spalte:");
		int col = Console.read(-1);
		System.out.println("Wähle eine Zeile:");
		int row = Console.read(-1);
		return new Turn(spieler, 0,0, col, height-1 - row);
	}

	private void setTurn(Turn turn) throws Exception {
		if ((turn.getToX() < width && turn.getToX() >= 0) 
				&& (turn.getToY() < height && turn.getToY() >= 0) 
				&& feld[turn.getToY()][turn.getToX()].contentEquals(" ")) {
			for (int i = turn.getToX(); i < width; i++) {
				for (int j = turn.getToY(); j < height; j++) {
					if (feld[j][i].contentEquals(" ")) {
						feld[j][i] = (spieler1)? "X":"O";
					}
				}
			}
			if (track) {
				addTurn(turn);
			}
		} else {
			System.out.println("Exception");
			throw new Exception ("Zug liegt nicht im Spielfeld, oder Feld ist bereits belegt!");
		}
	}

	private Turn kI(Spieler spieler) {
		System.out.println("Ich werde mal eine KI");
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
	
	@SuppressWarnings("unchecked")
	public boolean removeTurn() {
		if (turns.size() != 0) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					feld[j][i] = " ";
				}
			}

			turns.remove(turns.size()-1);
			ArrayList<Turn> theTurns = (ArrayList<Turn>) turns.clone();
			turns.clear();
			
			Iterator<Turn> it = theTurns.iterator();
			while (it.hasNext()) {
				try {
					setTurn(it.next());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		} else {
			return false;
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