package org.mss;
import java.util.ArrayList;

import org.mss.types.Turn;

public interface Protokollierbar {
	public abstract void addTurn(Turn turn);
	public abstract int findTurn(Turn turn);
	public abstract boolean removeTurn();
	public abstract void listTurns();
	ArrayList<Turn> turns = new ArrayList<Turn>(20);
}
