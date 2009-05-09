package org.mss.games;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

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
		turns.clear();
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
		return winner;
	}
	
	@Override
	public Spieler[] runde() {
		while (winner == null) {
			durchgang();
			removeTurn();
			displayFeld();
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
		return new Turn(spieler, 0,0, col, row);
	}

	private void setTurn(Turn turn) throws Exception {
		if ((turn.getToX() < width && turn.getToX() >= 0) 
				&& ((height-1 - turn.getToY()) < height && (height-1 - turn.getToY()) >= 0) 
				&& feld[height-1 - turn.getToY()][turn.getToX()].contentEquals(" ")) {
			for (int i = turn.getToX(); i < width; i++) {
				for (int j = height-1 - turn.getToY(); j < height; j++) {
					if (feld[j][i].contentEquals(" ")) {
						feld[j][i] = (spieler1)? "X":"O";
					}
				}
			}
			if (track) {
				addTurn(turn);
			}
		} else {
			throw new Exception ("Zug liegt nicht im Spielfeld, oder Feld ist bereits belegt!");
		}
	}

	private Turn kI(Spieler spieler) {
		
		boolean needKi = false;
		boolean isFree = false;
		int col = 0;
		int row = 0;
		Random random = new Random();
		
		// TODO vielleicht anders, indem man prüft, ob "unterhalb" noch was frei ist und nicht
		//      "oberhalb" was gesetzt
		
		
		// prüfen, ob in den ersten beiden Reihen schon was gesetzt ist
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < width; j++){
				if(feld[i][j] != " "){
					needKi = true;
				}
			}
		}
		
		// prüfen, ob in den ersten beiden Spalten schon was gesetzt ist
		for(int i = height; i < 2; i++){
			for(int j = 0; j < 2; j++){
				if(feld[i][j] != " "){
					needKi = true;
				}
			}
		}
		
		// zufälliges Feld berechnen und prüfen, ob es schon gesetzt ist
		// feld[0][0] und feld[1][1] dürfen dabei nicht gesetzt werden
		if(!needKi){
			while(isFree == false){
				col = random.nextInt(width-1);
				row = random.nextInt(height-1);
				System.out.println("zufall: " + col + " " + row);				
				if(feld[height-1 - row][col].equals(" ")){
					isFree = true;
				}
				if((col == 0 && row == 0) || (col == 1 && row == 1)){
					isFree = false;
				}
			}
		} 
		// ab hier greift KI
		else { 	
			
			// wenn in 2. Reihe ein Feld besetzt, besetze 2. Reihe außer die beiden linken Felder 
			if(feld[1][2].equals(" ")){
				for(int j = 2; j < feld.length; j++){
					if(feld[1][j] != " "){
						System.out.println("oben rechts");
						col = 2;
						row = 8;
						break;
					}
				}
			}				

			// wenn in 2. Spalte ein Feld besetzt, besetze 2 Spalte, bis auf das oberste Feld
			if(feld[2][1].equals(" ")){
				for(int j = 1; j < 2; j++){
					if(feld[j][2] != " "){
						System.out.println("unten links");
						col = 1;
						row = 8;
						break;
					}
				}
			}
			
			// wenn feld[1][1] besetzt, markiere linkes unterstes Feld
			if(!feld[1][1].equals(" ")){
				col = 0;
				row = 0;
			}
			
			
			int set = 0;
			int direction = 0; // 0 = horizontal; 1 = vertical;
			
			for(int i = 0; i < width; i++){
				if(!feld[i][0].equals(" ")){
					set = i;
					break;
				}
			}

			System.out.println("set 1: " + set);
			for(int i = 0; i < height; i++){
				if(!feld[0][i].equals(" ")){
					if(i < set){
						set = i;
						direction = 1;
						break;
					}
				}
			}
			
			if(set != 0){
				if(direction == 0){
					col = set;
					row = width-1;
				} else {
					col = 0;
					row = height - 1 - set;
				}
			}
				
			
		}
		

		System.out.println(col + " " + row);
		return new Turn(spieler, 0,0, col, height-1 - row);
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
			spieler1 = true;
			while (it.hasNext()) {
				try {
					setTurn(it.next());
					spieler1 = !spieler1;
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