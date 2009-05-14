package org.mss;

import java.io.IOException;

import org.mss.games.*;
import org.mss.Spiel;
import org.mss.Spieler;
import org.mss.utils.Console;

public class Tempserv {
	//Spielvars
	static boolean ersterLauf = true;
	static boolean vier = false;
	static boolean comp = false;
	static int breite = 0;
	static int hoehe = 0;
	static boolean protokol = false;
	static Spieler spieler1 = null;
	static Spieler spieler2 = null;
	static boolean neuerSpieler = true;
	static boolean weiteresSpiel = false;

	public static void main(String[] args) throws IOException{
		Console.setDebug(false);
		abfragen();
		do {
			Spiel spiel = null;
			if (vier) {
				spiel = (Spiel) ((breite == 0 && hoehe == 0)? new Viergewinnt():new Viergewinnt(breite, hoehe));
			} else {
				spiel = (Spiel) ((breite == 0 && hoehe == 0)? new Chomp(): new Chomp(breite, hoehe));
			}
	
			spiel.track(protokol);
		
			try {
				if (Math.random() <= .5) {
					spiel.plusSpieler(spieler1);
					spiel.plusSpieler(spieler2);
				} else {
					spiel.plusSpieler(spieler2);
					spiel.plusSpieler(spieler1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			spiel.zeigeFeld();
			Spieler gewinner[] = spiel.runde();
	
			if (gewinner.length == 1) {
				System.out.println("Gewonnen hat der Spieler:"+gewinner[0].toString());	
			} else {
				System.out.println("Remi zwischen den Spielern:");
				for (int i = 0; i < gewinner.length; i++) {
					System.out.println(gewinner[i].toString());
				}
			}
			if (protokol) {
				spiel.listeZuege();
			}
			abfragen();
		} while (weiteresSpiel);
		System.out.println("Vielen Dank für das Spielen mit MSS.\nAnd Remember the Answer for all Questions is in *.tiff byte 3&4.");
	}
	
	private static void abfragen() {
		if (ersterLauf) {
			System.out.println("Willkommen bei MSS, dem MultiplayerSpielSystem!\nWelches Spiel willst du spielen?");
			ersterLauf = false;
			kernFragen();
		} else {
			if (Console.read("Noch ein Spiel(weitere Abfrage später)","ja").contentEquals("ja")) {
				weiteresSpiel = true;
				if (!Console.read("Alles wie beim ersten mal?","ja").contentEquals("ja")) {
					neuerSpieler = (Console.read("Spieler beibehalten", "ja").contentEquals("ja"))? false:true; 
					kernFragen();
				} 
			} else {
				weiteresSpiel = false;
			}
		}
	}

	private static void kernFragen() {
		vier = (Console.read("1:Viergewinnt;2:Chomp",2)==1)? true:false;
		comp = (Console.read("Gegen Mensch(1) oder PC(2)",2) ==1)? false:true;

		if (neuerSpieler) {
			spieler1 = new Spieler(Console.read("Name Spieler1",""));
			//spieler1 = new Spieler();
			if (!comp) {
				spieler2 = new Spieler(Console.read("Name Spieler2",""));
			} else {
				spieler2 = new Spieler();
			}
		}
		System.out.println("Spielfeldgröße:");
		System.out.println("Breite (leer für default):");
		breite = Console.read(0);
		
		System.out.println("Höhe (leer für default):");
		hoehe = Console.read(0);
		
		protokol = Console.read("Protokollieren","ja").contentEquals("ja")? true:false;
	}
}