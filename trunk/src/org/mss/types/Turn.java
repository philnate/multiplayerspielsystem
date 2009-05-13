package org.mss.types;

import org.mss.Spieler;

public class Turn {
	private int toX;
	private int toY;
	private Spieler spieler;
	private int fromX;
	private int fromY;
	
	public Turn(Spieler spieler, int fromX, int fromY, int toX, int toY) {
		this.spieler = spieler;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}
	
	public boolean equals(Turn turn) {
		return (this.toString().equals(turn.toString()));
	}
	
	public String toString() {
		return spieler.toString() + " X: " + fromX + " > " + toX + " Y: " + fromY + " > " + toY;
	}

	public int getToX() {
		return toX;
	}

	public int getToY() {
		return toY;
	}

	public Spieler getSpieler() {
		return spieler;
	}

	public int getFromX() {
		return fromX;
	}

	public int getFromY() {
		return fromY;
	}
	
}
