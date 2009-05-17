package org.mss.games;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.mss.types.Zug;
import org.mss.utils.Console;
import org.mss.Spiel;
import org.mss.Spieler;

public class Chomp extends Spiel {
	private boolean spieler1 = true;
	
	public Chomp(int breite, int hoehe) {
		this.breite = (breite > 0)? breite:-breite;
		this.hoehe = (hoehe > 0)? hoehe:-hoehe;
		if (this.breite < 3) {
			this.breite = 3;
		}
		if (this.hoehe < 3) {
			this.hoehe = 3;
		}
		feld = new String[this.hoehe][this.breite];
		for (int i = 0; i < this.hoehe; i++) {
			for (int j = 0;j < this.breite; j++) {
				feld[i][j] = " ";
			}
		}
		zuege = new ArrayList<Zug>(breite*hoehe/4);
	}
	
	public Chomp() {
		this(7,4);
	}

	@Override
	public Spieler[] spielzug(Zug zug) throws Exception {

		if (gewinner != null) return null;
		if (spieler.size() != 2) {
			throw new Exception ("Es muss genau Zwei Spieler geben!");
		}
		if (zug == null) {
			throw new Exception ("Kein Zug übergeben!");
		}
		setzeZug(zug);
		spieler1 = !spieler1;
		zeigeFeld();
		checkWin();
		return gewinner;
	}
	
	@Override
	public Spieler[] runde() {
		while (gewinner == null) {
			durchgang();
		}
		return gewinner;
	}
	
	@Override
	public Spieler folgeSpieler() {
		if (spieler1) {
			return spieler.get(0);
		} else {
			return spieler.get(1);
		}
	}

	@Override
	public Zug frageSpieler(Spieler spieler) {
		if (spieler.isComp()) {
			System.out.println("Computer "+ spieler.getName() + " ist an der Reihe.");
			return kI(spieler);
		}
		System.out.println(spieler.toString() + " du bist dran:");
		System.out.println("Wähle eine Spalte:");
		int col = Console.read(-1);
		System.out.println("Wähle eine Zeile:");
		int row = Console.read(-1);
		return new Zug(spieler, 0,0, col, row);
	}

	private void setzeZug(Zug zug) throws Exception {
		System.out.println(zug.toString());
		if ((zug.getAufX() < breite && zug.getAufX() >= 0) 
				&& ((hoehe-1 - zug.getAufY()) < hoehe && (hoehe-1 - zug.getAufY()) >= 0) 
				&& feld[hoehe-1 - zug.getAufY()][zug.getAufX()].contentEquals(" ")) {
			for (int i = zug.getAufX(); i < breite; i++) {
				for (int j = hoehe-1 - zug.getAufY(); j < hoehe; j++) {
					if (feld[j][i].contentEquals(" ")) {
						feld[j][i] = (spieler1)? "X":"O";
					}
				}
			}
			if (track) {
				speicherZug(zug);
			}
		} else {
			throw new Exception ("Zug liegt nicht im Spielfeld, oder Feld ist bereits belegt!");
		}
	}

	private Zug kI(Spieler spieler) {
		
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
			for(int j = 0; j < breite; j++){
				if(!feld[i][j].contentEquals(" ")){
					needKi = true;
					break;
				}
			}
		}
		
		// prüfen, ob in den ersten beiden Spalten schon was gesetzt ist
		for(int i = hoehe; i < 2; i++){
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
					col = random.nextInt(breite-2)+1;
					row = random.nextInt(hoehe-2)+1;
					Console.debug("zufall: " + col + " " + row);				
					if(feld[hoehe-1 - row][col].contentEquals(" ")){
						isFree = true;
					}
					if((col == 0 && row == hoehe-1) || (col == 1 && row == hoehe -2 )){
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
			return new Zug(spieler, 0,0, col, row);
		} 
		// ab hier greift KI
		else {
			if (!feld[2][0].contentEquals(" ") && !feld[2][0].contentEquals(" ") && feld[1][1].contentEquals(" ")) {
				Console.debug("Geheimtrick");
				return new Zug(spieler, 0,0, 1,hoehe-2);
				/*__						__
				 *__ Feld besetzen sodass:  _? gilt dadurch Gegner verloren
				 */
			}
			if ((!feld[2][1].contentEquals(" ") || !feld[1][2].contentEquals(" ")) && feld[1][1].contentEquals(" ")) {
				Console.debug("What am I doing?");
				if (!feld[2][1].contentEquals(" ") && feld[1][2].contentEquals(" ")) {
					return new Zug(spieler,0,0,2,hoehe-2);
				} else {
					return new Zug(spieler, 0,0, 0,hoehe-3);
				}
			}
			int i = 0;
			int j = 0;
			for (i = 0; i < breite; i++) {
				if (!feld[0][i].contentEquals(" ")) {
					break;
				}
			}
			for (j = 0; j < hoehe; j++) {
				if (!feld[j][0].contentEquals(" ")) {
					break;
				}
			}
			i--;
			j--;
			Console.debug(i + " "+j);
			if (j > i) {
				Console.debug("Case1");
				return new Zug(spieler, 0,0,0,hoehe-i-2);
			} else if (i > j) {
				Console.debug("Case2");
				return new Zug (spieler, 0, 0, j+1, hoehe -1);
			} else {
				Console.debug("Case3");
				
				if (Math.random() <= 0.5) {
					return new Zug (spieler, 0, 0, (i>0)? i-1:0, hoehe-1);
				} else {
					return new Zug (spieler, 0, 0, 0, (j>0)? hoehe-j -1:0);
				}
			}
		}		
	}

	private void checkWin() {
		switch (feld[0][0].charAt(0)) {
		case 'X': gewinner = new Spieler[1];
			gewinner[0] = spieler.get(1);
			break;
		case 'O': gewinner = new Spieler[1];
			gewinner[0] = spieler.get(0);
			break;
		default: break;
		}
	}

	public void listeZuege() {
		Iterator<Zug> iterate = zuege.iterator();
		Zug turn = null;
		System.out.println("Spielzüge:");
		while (iterate.hasNext()) {
			turn = iterate.next();
			System.out.println(turn.getSpieler().toString() + " setzt auf die Position: X:" + turn.getAufX() + " Y:" + turn.getAufY());
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean entferneZug() {
		if (zuege.size() != 0) {
			for (int i = 0; i < breite; i++) {
				for (int j = 0; j < hoehe; j++) {
					feld[j][i] = " ";
				}
			}

			zuege.remove(zuege.size()-1);
			ArrayList<Zug> theTurns = (ArrayList<Zug>) zuege.clone();
			zuege.clear();
			
			Iterator<Zug> it = theTurns.iterator();
			spieler1 = true;
			while (it.hasNext()) {
				try {
					setzeZug(it.next());
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
	public boolean plusSpieler(Spieler spieler) throws Exception {
		if (this.spieler.size() == 2) {
			throw new Exception("Maximale Zahl von Spielern erreicht!");
		}
		return this.spieler.add(spieler);
	}
}