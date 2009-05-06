package org.mss;
import java.io.IOException;

import org.mss.games.*;
import org.mss.Spiel;
import org.mss.Spieler;
import org.mss.utils.Console;

public class Tempserv {
	//Spielvars
	static boolean firstrun = true;
	static boolean vier = false;
	static boolean comp = false;
	static int width = 0;
	static int height = 0;
	static boolean protokol = false;
	static Spieler spieler1 = null;
	static Spieler spieler2 = null;
	static boolean newPlayer = true;
	static boolean nextGame = false;
	
	public static void main(String[] args) throws IOException{
		abfragen();
		do {
			Spiel spiel = null;
			if (vier) {
				spiel = (Spiel) ((width == 0 && height == 0)? new Viergewinnt():new Viergewinnt(width, height));
			} else {
				spiel = (Spiel) ((width == 0 && height == 0)? new Chomp(): new Chomp(width, height));
			}
	
			spiel.track(protokol);
		
			try {
				spiel.addPlayer(spieler1);
				spiel.addPlayer(spieler2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			spiel.displayFeld();
			Spieler winner[] = spiel.runde();
			spiel.listTurns();
			spiel.removeTurn();
			spiel.displayFeld();
	
			if (winner.length == 1) {
				System.out.println("Gewonnen hat der Spieler:"+winner[0].toString());	
			} else {
				System.out.println("Remi zwischen den Spielern:");
				for (int i = 0; i < winner.length; i++) {
					System.out.println(winner[i].toString());
				}
			}
			if (protokol) {
				spiel.listTurns();
			}
			abfragen();
		} while (nextGame);
		System.out.println("Vielen Dank für das Spielen mit MSS.\nAnd Remember the Answer for all Questions is in *.tiff byte 3&4.");
	}
	
	private static void abfragen() {
		if (firstrun) {
			System.out.println("Willkommen bei MSS, dem MultiplayerSpielSystem!\nWelches Spiel willst du spielen?");
			firstrun = false;
			coreQuestions();
		} else {
			if (Console.read("Noch ein Spiel(weitere Abfrage später)","ja").contentEquals("ja")) {
				nextGame = true;
				if (!Console.read("Alles wie beim ersten mal?","ja").contentEquals("ja")) {
					newPlayer = (Console.read("Spieler beibehalten", "ja").contentEquals("ja"))? false:true; 
					coreQuestions();
				} 
			} else {
				nextGame = false;
			}
		}
	}

	private static void coreQuestions() {
		vier = (Console.read("1:Viergewinnt;2:Chomp",1)==1)? true:false;
		comp = (Console.read("Gegen Mensch(1) oder PC(2)",1) ==1)? false:true;

		if (newPlayer) {
			spieler1 = new Spieler(Console.read("Name Spieler1",""));
			
			if (!comp) {
				spieler2 = new Spieler(Console.read("Name Spieler2",""));
			} else {
				spieler2 = new Spieler();
			}
		}
		System.out.println("Spielfeldgröße:");
		System.out.println("Breite (leer für default):");
		width = Console.read(0);
		
		System.out.println("Höhe (leer für default):");
		height = Console.read(0);
		
		protokol = Console.read("Protokollieren","ja").contentEquals("nein")? false:true;
	}
}