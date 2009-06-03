package org.mss.net.server;

import org.mss.utils.*;

import java.io.IOException;
import java.net.ServerSocket;

/*
 * Hauptserverthread an diesem Geschieht das Management
 */
public class MSServer {
	private static ServerSocket listener = null;
	private static int port = 62742;

	public static void main(String[] args) {
		try {
			Console.write("Willkommen bei dem MSS Spielserver!");
			listener = new ServerSocket(Console.read("Benutze Socket", port));
			Connector connector = new Connector(listener);
			connector.start();
			while (true) {//Was will Admin machen
				switch (Console.read("?").charAt(0)) {
				case 'u':
					Console.write("Angemeldete Benutzer:");
					for (int i = 0; i < ClientThread.activeUser.size(); i++) {
						Console.write(ClientThread.activeUser.get(i));
					}
					break;
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
