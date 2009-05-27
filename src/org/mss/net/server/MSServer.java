package org.mss.net.server;

import org.mss.utils.*;

import java.io.IOException;
import java.net.ServerSocket;

public class MSServer {
	private static ServerSocket listener = null;
	private static int port = 62742;

	public static void main(String[] args) {
		try {
			Console.write("Willkommen bei dem MSS Spielserver!");
			listener = new ServerSocket(Console.read("Benutze Socket", port));
			Connector connector = new Connector(listener);
			connector.start();
			while (true) {
				switch (Console.read("?").charAt(0)) {
				case 'u':
					Console.write("Angemeldete Benutzer:");
					for (int i = 0; i < ClientThread.activeUser.size(); i++) {
						Console.write(ClientThread.activeUser.get(i));
					}
					break;
				default:
					Console.write("Mögliche Befehle:");
					Console.write("u - Listen aller angemeldeten Benutzer");
					break;
				}
				
			}
		} catch (IOException e) {
			Console.write("Server läuft bereits und nur eine Instanz erlaubt! Programm wird beendet!");
		}
	}
}
