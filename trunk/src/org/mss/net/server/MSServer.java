package org.mss.net.server;

import org.mss.utils.*;
import org.mss.windows.MainWin;
import org.mss.windows.NoticeWin;
import org.mss.windows.QueryWin;

import java.awt.Dimension;
import java.io.IOException;
import java.net.ServerSocket;

/*
 * Hauptserverthread an diesem Geschieht das Management
 */
public class MSServer {
	private static ServerSocket listener = null;
	private static int port = 62742;

	public static void main(String[] args) {
		//Abfragefenster für die Portauswahl erzeugen
		QueryWin query= new QueryWin("Willkommen bei dem MSS Spielserver! - Portwahl", "Welcher Port soll, zum lauschen, benutzt werden?","Ok", Integer.toString(port),new Dimension(400,100));
		query.show();

		if (query.isCanceled()) System.exit(0);//Benutzer will Programm beenden
		
		try {
			int usePort = port;
			if (Integer.getInteger(query.getQueryAnswer()) != null) {
				usePort = Integer.getInteger(query.getQueryAnswer());
			}
			query = null;
			MainWin mainWin = new MainWin();
			mainWin.show();

			listener = new ServerSocket(usePort);
			mainWin.addMessage("Warte auf neuen Teilnehmer!", mainWin.COLOR_NOTE);
			try {
				while (true) {
					//wartet auf neuen Teilnehmer und weist ihm anschließend seinen ClientThread zu
					ClientThread client = new ClientThread(listener.accept(), mainWin);
					Thread clientThread = new Thread(client);
					clientThread.start();
				}
			} catch (IOException e) {
				//Irgendein Fehler ist aufgetreten im Socket, lieber beenden.
				Console.log("Fehler bei Verbindungsaufnahme mit Client!");
				System.exit(1);
				e.printStackTrace();
			}
		} catch (IOException e) {
			//Nur ein Server pro Host/Port
			NoticeWin nw = new NoticeWin("Server läuft bereits!","Server läuft bereits und nur eine Instanz erlaubt! Programm wird beendet!", new Dimension(300,100));
			nw.show();
			System.exit(1);
		}
	}
}
