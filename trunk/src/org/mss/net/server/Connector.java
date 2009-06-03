package org.mss.net.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.mss.utils.Console;

/*
 * Klasse Connector übernimmt die Rolle des Verbinders
 */
public class Connector extends Thread {
	private ServerSocket listener = null;

	public void run() {
		Console.log("Warte auf neuen Teilnehmer!");
		try {
			while (true) {
				//wartet auf neuen Teilnehmer und weist ihm anschließend seinen ClientThread zu
				ClientThread client = new ClientThread(this.listener.accept());
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

	Connector(ServerSocket listener) {
		this.listener = listener;
	}
}