package org.mss.net.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.mss.utils.Console;
import org.mss.windows.MainWin;

/*
 * Klasse Connector übernimmt die Rolle des Verbinders
 */
public class Connector implements Runnable {
	private ServerSocket listener = null;
	private MainWin window = null;
	
	public void run() {
		window.addMessage("Warte auf neuen Teilnehmer!", window.COLOR_NOTE);
		try {
			while (true) {
				//wartet auf neuen Teilnehmer und weist ihm anschließend seinen ClientThread zu
				ClientThread client = new ClientThread(this.listener.accept(), window);
				Thread clientThread = new Thread(client);
				clientThread.start();
			}
		} catch (IOException e) {
			//Irgendein Fehler ist aufgetreten im Socket, lieber beenden.
			Console.log("Fehler bei Verbindungsaufnahme mit Client!");
			System.exit(1);
			e.printStackTrace();
		}
	}

	Connector(ServerSocket listener, MainWin window) {
		this.listener = listener;
		this.window = window;
	}
}