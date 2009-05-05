package org.mss;
import java.io.IOException;

import org.mss.games.*;
import org.mss.Spiel;
import org.mss.Spieler;
import org.mss.utils.Console;

public class Tempserv {
	static int width = 0;
	static int height = 0;
	static boolean protokol = false;
	static Spieler spieler1 = null;
	static Spieler spieler2 = null;
	
	public static void main(String[] args) throws IOException{
		System.out.println("Willkommen bei MSS was wollen Sie spielen:");
		System.out.println("1:Viergewinnt;2:Chomp (def 1)?");
		boolean vier = (Console.read(2)==1)? true:false;
		
		System.out.println("Gegen Mensch(1) oder PC(2) (def 1)?");
		boolean comp = (Console.read(1) ==1)? false:true;
		
		System.out.println("Name Spieler1 (leer für default):");
		spieler1 = new Spieler(Console.read(""));
		
		if (!comp) {
			System.out.println("Name Spieler2 (leer für default):");
			spieler2 = new Spieler(Console.read(""));
		} else {
			spieler2 = new Spieler();
		}
		
		System.out.println("Spielfeldgröße:");
		System.out.println("Breite (leer für default):");
		width = Console.read(0);
		
		System.out.println("Höhe (leer für default):");
		height = Console.read(0);
		
		System.out.println("Protokollieren(default nein)?");
		protokol = Console.read("nein").contentEquals("nein")? false:true;

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
	}
	
	private void abfragen() {
		
	}
}