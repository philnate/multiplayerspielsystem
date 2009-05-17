package org.mss;

import org.mss.types.Zug;

public interface Protokollierbar {
	public abstract void speicherZug(Zug turn);
	public abstract int findeZug(Zug turn);
	public abstract boolean entferneZug();
	public abstract void listeZuege();
}
