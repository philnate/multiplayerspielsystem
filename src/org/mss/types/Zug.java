package org.mss.types;

import java.io.Serializable;

import org.mss.Spieler;

public class Zug implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8002531918533708021L;
	private int aufX;
	private int aufY;
	private Spieler spieler;
	private int vonX;
	private int vonY;
	
	public Zug(Spieler spieler, int vonX, int vonY, int aufX, int aufY) {
		this.spieler = spieler;
		this.vonX = vonX;
		this.vonY = vonY;
		this.aufX = aufX;
		this.aufY = aufY;
	}
	
	public boolean equals(Zug turn) {
		return (this.toString().equals(turn.toString()));
	}
	
	public String toString() {
		return spieler.toString() + " X: " + vonX + " > " + aufX + " Y: " + vonY + " > " + aufY;
	}

	public int getAufX() {
		return aufX;
	}

	public int getAufY() {
		return aufY;
	}

	public Spieler getSpieler() {
		return spieler;
	}

	public int getVonX() {
		return vonX;
	}

	public int getVonY() {
		return vonY;
	}
	
}
