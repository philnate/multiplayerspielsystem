package org.mss.net.server;

import org.mss.utils.*;
import org.mss.windows.MainWin;
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
			Connector connector = new Connector(listener, mainWin);
			Thread connect = new Thread(connector);
			connect.start();
			while (true) {//Was will Admin machen
				switch (Console.read("?").charAt(0)) {
				case 'x':
					Console.write("Server wird herunter gefahren!");
					System.exit(0);
					break;
				case 'w':
				case 'k':
					Console.write("Nicht eingebaut aktuell");
					break;
				default:
					Console.write("Mögliche Befehle:");
					Console.write("u - Liste aller angemeldeten Benutzer");
					Console.write("x - Beendet Server");
					Console.write("k NAME- Benutzer mit dem Namen NAME kicken");
					Console.write("w NAME - Benutzer mit dem Namen NAME verwarnen");
					break;
				}
			}
		} catch (IOException e) {
			//Nur ein Server pro Host/Port
			Console.write("Server läuft bereits und nur eine Instanz erlaubt! Programm wird beendet!");
		}
	}
}
