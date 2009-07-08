package org.mss.net.client;

import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.mss.Spiel;
import org.mss.Spieler;
import org.mss.types.MSSDataObject;
import org.mss.types.Zug;
import org.mss.windows.ClientMainWin;
import org.mss.windows.NoticeWin;

public class RunnableTurn implements Runnable {

	private Spiel curGame;
	private Spieler myself;
	private ObjectOutputStream send;
	private boolean first;
	private MSSDataObject inData;
	private ClientMainWin guiCMainWin;

	public RunnableTurn(boolean first, ObjectOutputStream send, MSSDataObject inData, Spieler myself, Spiel curGame, ClientMainWin guiCMainWin) {
		this.first = first;
		this.send = send;
		this.inData = inData;
		this.myself = myself;
		this.curGame = curGame;
		this.guiCMainWin = guiCMainWin;
	}

	@Override
	public void run() {
		try {
			if (!first) {
				if (checkIfWin(curGame.spielzug((Zug)inData.getData()),myself, curGame, guiCMainWin, inData.getFromUser(), send)) return;
			}
			Zug mine = curGame.frageSpieler(myself);
			send.writeObject(new MSSDataObject(MSSDataObject.GAME_TURN, mine,new Spieler[]{inData.getFromUser()}, myself));
			send.flush();
			if (checkIfWin(curGame.spielzug(mine), myself, curGame, guiCMainWin, inData.getFromUser(), send));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean checkIfWin(Spieler[] spieler, Spieler myself, Spiel curGame, ClientMainWin guiClientMainWin, Spieler enemy, ObjectOutputStream send) {
		boolean inIt = false;
		boolean other = false;
		NoticeWin nw = null;
		if (spieler == null) return false;
		for (int i = 0; i < spieler.length; i++) {
			if (spieler[i] == myself) {
				inIt = true;
			} else {
				other = true;
			}
		}
		if(inIt && !other) {
			nw = new NoticeWin("Gewonnen", "Glückwunsch! Du hast das Spiel gewonnen", new Dimension(300,100));
			guiClientMainWin.addMessage("Du hast das Spiel gegen " + enemy.getName() + " gewonnen.");
		} else if (inIt && other) {
			nw = new NoticeWin("Unentschieden", "Du hast ein Unentschieden erreicht!", new Dimension(300,100));
			guiClientMainWin.addMessage("Du hast beim Spiel gegen " + enemy.getName() + " ein Unentschieden erreicht.");
		} else if (!inIt && other) {
			nw = new NoticeWin("Verloren", "Du hast das Spiel leider verloren!", new Dimension(300, 100));
			guiClientMainWin.addMessage("Du hast das Spiel gegen " + enemy.getName() + " verloren.");
		}
		nw.show();
//		curGame.dispose();
//		curGame = null;
		curGame.close();
		try {
			send.writeObject(new MSSDataObject(MSSDataObject.GAME_CLOSED, myself));
			send.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
