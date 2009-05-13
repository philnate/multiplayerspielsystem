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
		if (this.width < 3) {
			this.width = 3;
		}
		if (this.height < 3) {
			this.height = 3;
		}
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
		System.out.println(turn.toString());
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
		
		try {
			Thread.sleep((long) (Math.random()*100000)/100 + 500);
			//Computer überlegt
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO vielleicht anders, indem man prüft, ob "unterhalb" noch was frei ist und nicht
		//      "oberhalb" was gesetzt	
		
		// prüfen, ob in den ersten beiden Reihen schon was gesetzt ist
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < width; j++){
				if(!feld[i][j].contentEquals(" ")){
					needKi = true;
					break;
				}
			}
		}
		
		// prüfen, ob in den ersten beiden Spalten schon was gesetzt ist
		for(int i = height; i < 2; i++){
			for(int j = 0; j < 2; j++){
				if(!feld[i][j].contentEquals(" ")){
					needKi = true;
					break;
				}
			}
		}

		Console.debug("Ki "+needKi);
		// zufälliges Feld berechnen und prüfen, ob es schon gesetzt ist
		// feld[0][0] und feld[1][1] dürfen dabei nicht gesetzt werden
		if(!needKi){
//			if (feld[2][1].contentEquals(" ") && feld[1][2].contentEquals(" ") /*&& feld[1][1].contentEquals(" ")*/) {
				while(isFree == false){
					col = random.nextInt(width-2)+1;
					row = random.nextInt(height-2)+1;
					Console.debug("zufall: " + col + " " + row);				
					if(feld[height-1 - row][col].contentEquals(" ")){
						isFree = true;
					}
					if((col == 0 && row == height-1) || (col == 1 && row == height -2 )){
						isFree = false;
					}
				}
//			} else {
//				Console.debug("What am I doing?");
//				if (!feld[2][1].contentEquals(" ")) {
//					return new Turn(spieler,0,0,2,height-2);
//				} else {
//					return new Turn(spieler, 0,0, 0,height-3);
//				}
//			}
			return new Turn(spieler, 0,0, col, row);
		} 
		// ab hier greift KI
		else {
			if (!feld[2][0].contentEquals(" ") && !feld[2][0].contentEquals(" ") && feld[1][1].contentEquals(" ")) {
				Console.debug("Geheimtrick");
				return new Turn(spieler, 0,0, 1,height-2);
				/*__						__
				 *__ Feld besetzen sodass:  _? gilt dadurch Gegner verloren
				 */
			}
			if ((!feld[2][1].contentEquals(" ") || !feld[1][2].contentEquals(" ")) && feld[1][1].contentEquals(" ")) {
				Console.debug("What am I doing?");
				if (!feld[2][1].contentEquals(" ") && feld[1][2].contentEquals(" ")) {
					return new Turn(spieler,0,0,2,height-2);
				} else {
					return new Turn(spieler, 0,0, 0,height-3);
				}
			}
			int i = 0;
			int j = 0;
			for (i = 0; i < width; i++) {
				if (!feld[0][i].contentEquals(" ")) {
					break;
				}
			}
			for (j = 0; j < height; j++) {
				if (!feld[j][0].contentEquals(" ")) {
					break;
				}
			}
			i--;
			j--;
			Console.debug(i + " "+j);
			if (j > i) {
				Console.debug("Case1");
				return new Turn(spieler, 0,0,0,height-i-2);
			} else if (i > j) {
				Console.debug("Case2");
				return new Turn (spieler, 0, 0, j+1, height -1);
			} else {
				Console.debug("Case3");
				
				if (Math.random() <= 0.5) {
					return new Turn (spieler, 0, 0, (i>0)? i-1:0, height-1);
				} else {
					return new Turn (spieler, 0, 0, 0, (j>0)? height-j -1:0);
				}
			}
		}		
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