package org.mss.utils.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;

import org.mss.Spiel;
import org.mss.Spieler;
import org.mss.types.MSSDataObject;
import org.mss.windows.ClientMainWin;

public class CurGameClosedListener extends WindowAdapter {
	
	private ObjectOutputStream send;
	private Spieler toUser;
	private ClientMainWin guiCMainWin;
	private Spiel curGame;

	public CurGameClosedListener(ObjectOutputStream send, Spieler toUser, ClientMainWin guiCMainWin, Spiel curGame) {
		this.send = send;
		this.toUser = toUser;
		this.guiCMainWin = guiCMainWin;
		this.curGame = curGame;
	}

	public void windowClosing(WindowEvent e) {
		((JFrame)e.getSource()).dispose();
		if (!curGame.isClosed()) { 
			curGame.close();
			try {
				send.writeObject(new MSSDataObject(MSSDataObject.GAME_CLOSED,null, new Spieler[]{toUser}, null));
				send.flush();
				guiCMainWin.addMessage("Du hast das Spiel verlassen!");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
