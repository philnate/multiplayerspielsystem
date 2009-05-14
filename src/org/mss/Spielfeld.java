package org.mss;

public abstract class Spielfeld {
	public int hoehe;
	public int breite;
	public String[][] feld = null;
	
	public abstract void zeigeFeld();
	
	public Spielfeld() {
		this(10, 10);
	}
	
	public Spielfeld(int breite, int hoehe) {
		this.breite = breite;
		this.hoehe = hoehe;
		feld = new String[hoehe][breite];
	}

	public int getHoehe() {
		return hoehe;
	}

	public int getBreite() {
		return breite;
	}
}
