package org.mss.net.client;

import org.mss.types.Commands;
import org.mss.utils.*;
import org.mss.windows.ClientMainWin;
import org.mss.windows.NoticeWin;
import org.mss.windows.QueryWinDouble;
import org.mss.windows.QueryWinYesNo;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/*
 * Spielerhauptklasse
 */
public class MSSClient {
	
	public static void main(String[] args) {

		Socket server = null;
		String addr = "localhost";
		int port = 62742;	

		QueryWinDouble gwd = new QueryWinDouble("Verbindungsangaben","Host", "Port", "Ok", addr, Integer.toString(port), new Dimension(300,100));
		QueryWinYesNo qyn;
		NoticeWin nw;

		gwd.show();
		if (gwd.isCanceled()) System.exit(0);
		
		addr = gwd.getInput1Text();		
		port = Integer.valueOf(gwd.getInput2Text()).intValue();

		boolean tryAgain = false;
		boolean wasLoggedIn = false;
		//Falls Verbindungsabbrüche und derart stattfanden kann der Spieler es erneut versuchen
		while (!tryAgain) {
			try {
				server = new Socket(addr, port);
				tryAgain = true;
				PrintWriter send = new PrintWriter(server.getOutputStream());
				BufferedReader read = new BufferedReader(new InputStreamReader(server.getInputStream()));
				Console.write("Verbindung hergestellt");
				ClientMainWin guiCMainWin = new ClientMainWin(send);
				
				int command = 0;
				boolean resume = true;
				//Auf Befehle warten solange nichts gegenteiliges Empfangen wurde oder eintritt
				while (resume) {
					switch (command = read.read()) {
					case Commands.SND_LOGIN:
						guiCMainWin.setUsername(login(send));
						break;
					case 63://Jaja der falsche Login Code
					case Commands.LOGIN_SUCCESS:
						wasLoggedIn = true;
						System.out.println("Erfolgreich angemeldet");
						guiCMainWin.setVisible(true);
						break;
					case Commands.LOGIN_FAILED:
						qyn = new QueryWinYesNo ("Anmeldung fehlgeschlagen!", "Anmeldung fehlgeschlagen. Erneut versuchen?", new Dimension(300,100));
						qyn.show();
						if (qyn.getAnswer()) {
							guiCMainWin.setUsername(login(send));// Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case Commands.LOGIN_PASSWRONG:
						qyn = new QueryWinYesNo ("Passwort falsch!", "Falsches Passwort! Erneut versuchen?", new Dimension(300,100));
						qyn.show();
						if (qyn.getAnswer()) {
							guiCMainWin.setUsername(login(send));// Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case Commands.LOGIN_ALREADY_ON:
						qyn = new QueryWinYesNo ("Bereits online!", "Es ist Bereits ein Benutzer mit diesen Namen online. Als anderer Benutzer anmelden?", new Dimension(300,100));
						qyn.show();
						if (qyn.getAnswer()) {
							guiCMainWin.setUsername(login(send));//Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case Commands.LOGIN_BAN:
						nw = new NoticeWin("Gebannt!", "Benutzer ist vom Server gebannt!", new Dimension(300,100));
						nw.show();
						resume = false;
						break;
					case Commands.BC_NEWUSER:
						String user = read.readLine();
						guiCMainWin.addUser(user);
						guiCMainWin.addMessage(user + " hat sich angemeldet!");
						break;
					case Commands.BC_USEROFF:
						String usr = read.readLine();
						guiCMainWin.removeUser(usr);
						guiCMainWin.addMessage(usr + " hat sich abgemeldet!");
						break;
					case Commands.USER_WARN:
						if (read.read() == Commands.USER_SELF) {
							guiCMainWin.addMessage("Du wurdest verwarnt! Grund:" + read.readLine());
						} else {
							read.readLine();
						}
						break;
					case Commands.USER_KICK:
						if (read.read() == Commands.USER_SELF) {
							guiCMainWin.addMessage("Du wurdest vom Server gekickt! Grund:" + read.readLine());
						} else {
							read.readLine();
						}
						break;
					case Commands.USER_BAN:
						if (read.read() == Commands.USER_SELF) {
							guiCMainWin.addMessage("Du wurdest vom Server gebannt! Grund:" + read.readLine());
						} else {
							read.readLine();
						}
						break;
					case Commands.USERLIST:
						String[] users = read.readLine().split("\t");
						for (int i = 0; i < users.length; i++) {
							guiCMainWin.addUser(users[i]);
						}
						break;
					case Commands.ACTION_FORBIDDEN:
						guiCMainWin.addMessage(read.readLine());
						break;
					case Commands.BC_MESSAGE:
						int length = read.read();
						for (int i = 0; i < length; i++) {
							guiCMainWin.addMessage(read.readLine());
						}
						break;
					case -1:
						throw new SocketException();
					default:
						System.out.println("Falscher Code Erhalten" + command);
						guiCMainWin.addMessage("Unbekannten Befehl erhalten:" + command);
					}
				}
				server.close();
			} catch (UnknownHostException e) {
				//Tja falsche Adresse
				nw = new NoticeWin("Nicht gefunden!", "Die angegebene Adresse konnte nicht gefunden werden!", new Dimension(300,100));
				nw.show();
				tryAgain = false;
			} catch (IOException e) {
				if (wasLoggedIn) {
					//Wenn Benutzer bereits angemeldet war Fragen ob er versuchen möchte den Server erneut zu erreichen
					qyn = new QueryWinYesNo("Verbindung unterbrochen", "Verbindung zum Server wurde unterbrochen. Erneut versuchen?", new Dimension(300,100));
					qyn.show();
					if (qyn.getAnswer()) {
						tryAgain = false;
					}
				} else {
					//Benutzer war nicht angemeldet also ist entweder der Port falsch oder es läuft aktuell kein MSS Server auf dieser Adresse
					nw = new NoticeWin("Server nicht gefunden","Kein MSS Server auf angegebener Adresse gefunden oder Port falsch!", new Dimension(300,100));
					nw.show();
					tryAgain = false;
				}
			}
			//Entsprechend der Fälle ob Benutzer angemeldet war und ob erneut eine Verbindungsaufnahme versucht werden soll abfragen
			if (!tryAgain && !wasLoggedIn) {
				qyn = new QueryWinYesNo("Erneut probieren", "Erneut probieren?", new Dimension(300,100));
				qyn.show();
				tryAgain = !qyn.getAnswer();
			} else if (!tryAgain && wasLoggedIn) {
				wasLoggedIn = false;
			}
		}
	}

	public static String login(PrintWriter send) {
		QueryWinDouble gwd = new QueryWinDouble("Logindaten", "Benutzername", "Passwort", "Ok", "Phil", "0", new Dimension(300,100));
		gwd.show();
		if (gwd.isCanceled()) System.exit(0);
		
		send.write(Commands.SND_LOGIN);
		send.append(gwd.getInput1Text().replace("\\", "") + "\t" + gwd.getInput2Text().replace("\\", "") + "\n");
		send.flush();
		return gwd.getInput1Text().replace("\\", "");
	}

}
