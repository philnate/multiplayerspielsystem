package org.mss;

import java.util.ArrayList;

import org.mss.types.Zug;

public interface Protokollierbar {
	public abstract void speicherZug(Zug turn);
	public abstract int findeZug(Zug turn);
	public abstract boolean entferneZug();
	public abstract void listeZuege();
	ArrayList<Zug> zuege = new ArrayList<Zug>(20);
}
