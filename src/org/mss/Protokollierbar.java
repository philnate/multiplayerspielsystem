package org.mss;
import org.mss.types.Turn;

public interface Protokollierbar {
	public abstract void addTurn(Turn turn);
	public abstract int findTurn(Turn turn);
	public abstract boolean removeTurn();
	public abstract void listTurns();
}
