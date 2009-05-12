package org.mss;
import org.mss.Spieler;
import org.mss.types.Turn;
import org.mss.utils.Console;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class Spiel extends Spielfeld implements Protokollierbar {
	protected ArrayList<Spieler> spieler = null;
	protected boolean track = false;
	protected Spieler[] winner = null;
	public abstract Turn queryPlayer(Spieler spieler);
	public abstract Spieler[] spielzug(Turn turn) throws Exception;
	public abstract Spieler[] runde();
	public abstract Spieler nextPlayer();

	public Spieler[] durchgang() {
		boolean error =false;
		do {
			error = false;
			try {
				spielzug(queryPlayer(nextPlayer()));
				if (winner != null) {
					return winner;
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
				spielzug(queryPlayer(nextPlayer()));
				return winner;
			} catch (Exception e) {
				error = true;
				e.printStackTrace();
				Console.read("Wait a second", 42);
			}
			System.out.println(error);
		} while (error);
		return winner;
	}

	public Spieler[] playerList() {
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
	
	public void addTurn(Turn turn) {
		System.out.println(turn.toString());
		turns.add(turn);
	}

	public int findTurn(Turn turn) {
		Turn temp = null;
		Iterator<Turn> it = turns.iterator();

		while (it.hasNext()) {
			temp = (Turn) it.next();
			if (temp.equals(turn)) {
				return turns.size() - turns.indexOf(temp);//So das der Spieler weiß der wie vielte Zug es war;
			}
		}
		return -1;
	}

	public void listTurns() {
		Iterator<Turn> iterate = turns.iterator();
		Turn turn = null;
		System.out.println("Spielzüge:");
		while (iterate.hasNext()) {
			turn = iterate.next();
			System.out.println(turn.toString());
		}
	}

	public void displayFeld() {
		for (int i = 0; i < height; i++) {
			System.out.print("|");
			for (int j = 0; j < width; j++) {
				System.out.print("--|");
			}
			System.out.println();
			System.out.print("|");
			for (int j = 0; j < width; j++) {
				System.out.print(feld[i][j]+ " |");
			}
			System.out.println(height-i-1);
		}
		System.out.print("|");
		for (int j = 0; j < width; j++) {
			System.out.print((Integer.toString(j).length() ==1)? j+"-|": j+"|");
		}
		System.out.println();
	}
	
	public boolean addPlayer(Spieler spieler) throws Exception{
		return this.spieler.add(spieler);
	}
}
