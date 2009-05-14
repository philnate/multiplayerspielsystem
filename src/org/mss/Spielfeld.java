package org.mss;

public abstract class Spielfeld {
	public int hoehe;
	public int breite;
	public String[][] feld = null;
	
	public abstract void zeigeFeld();

	public int getHoehe() {
		return hoehe;
	}

	public int getBreite() {
		return breite;
	}
}
