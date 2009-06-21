package org.mss.net.client;

import org.mss.types.Commands;
import org.mss.utils.*;
import org.mss.windows.ClientConnect;
import org.mss.windows.ClientMainWin;
import org.mss.windows.ClientRegist;

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
		
		ClientConnect guiConnect = new ClientConnect("Connection");
		ClientRegist guiRegist = new ClientRegist("Registration");
		ClientMainWin guiCMainWin = new ClientMainWin();
		guiConnect.show();
		guiRegist.show();

		Socket server = null;
		Thread sendMessage = null;
		ThreadSM threadSM = null;
		String addr = "localhost";
		int port = 62742;
		//Console.write("Willkommen bei MSS. Dem Spielsystem der Zukunft!");
		if(!guiConnect.getAddrText().contentEquals("")){
			addr = guiConnect.getAddrText(); //Console.read("Bitte geb die Adresse des MSS Servers an auf den du dich anmelden willst!", addr);
		}
		System.out.println(guiConnect.getPortText());
		if(!guiConnect.getAddrText().contentEquals("")) {
			port = Integer.valueOf(guiConnect.getPortText()).intValue();//Console.read("Bitte geb den Port f�r " + addr + " an, wo der MSS Server lauscht!", port);
		}
		
		String name = guiRegist.getUserText();
		String password = guiRegist.getPwText();
		
		System.out.println(name + " " + password);
			
		boolean tryAgain = false;
		boolean wasLoggedIn = false;
		//Falls Verbindungsabbr�che und derart stattfanden kann der Spieler es erneut versuchen
		while (!tryAgain) {
			try {
				guiCMainWin.setVisible(true);
				
				server = new Socket(addr, port);
				tryAgain = true;
				PrintWriter send = new PrintWriter(server.getOutputStream());
				BufferedReader read = new BufferedReader(new InputStreamReader(server.getInputStream()));
				Console.write("Verbindung hergestellt");

				int command = 0;
				boolean resume = true;
				//Auf Befehle warten solange nichts gegenteiliges Empfangen wurde oder eintritt
				while (resume) {
					switch (command = read.read()) {
					case Commands.SND_LOGIN:
						login(send, name, password);
						break;
					case Commands.LOGIN_SUCCESS:
						Console.write("Erfolgreich angemeldet!");
						wasLoggedIn = true;
						break;
					case Commands.LOGIN_FAILED:
						if (Console.read("Anmeldung fehlgeschlagen. Erneut versuchen", "ja").contentEquals("ja")) {
							login(send, name, password);// Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case Commands.LOGIN_PASSWRONG:
						if (Console.read("Passwort falsch. Erneut probieren", "ja").contentEquals("ja")) {
							login(send, name, password);// Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case Commands.LOGIN_ALREADY_ON:
						if (Console.read("Es ist bereits ein Benutzer mit diesem Namen angemeldet. Als anderer Benutzer anmelden", "ja").contentEquals("ja")) {
							login(send, name, password);//Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case Commands.LOGIN_BAN:
						Console.write("Benutzer ist vom Server gebannt!");
						resume = false;
						break;
					case Commands.BC_NEWUSER:
						Console.write(read.readLine() + " hat sich angemeldet!");
						break;
					case Commands.BC_USEROFF:
						Console.write(read.readLine() + " hat sich abgemeldet!");
						break;
					case Commands.USER_WARN:
						if (read.read() == Commands.USER_SELF) {
							Console.write("Du wurdest verwarnt! Grund:" + read.readLine());
						} else {
							Console.write(read.readLine());
						}
						break;
					case Commands.USER_KICK:
						if (read.read() == Commands.USER_SELF) {
							Console.write("Du wurdest vom Server gekickt! Grund:" + read.readLine());
						} else {
							Console.write(read.readLine());
						}
						break;
					case Commands.USER_BAN:
						if (read.read() == Commands.USER_SELF) {
							Console.write("Du wurdest vom Server gebannt! Grund:" + read.readLine());
						} else {
							Console.write(read.readLine());
						}
						break;
					case Commands.USERLIST:
						//Client wurde angemeldet alle Benutzernamen ausgeben
						String[] activeUser = read.readLine().split("\t");
						Console.write("Aktuell angemeldete Benutzer:");
						for (int i = 0; i < activeUser.length; i++) {
							Console.write(activeUser[i]);
						}
						//Thread zum Lesen von der Console starten
						threadSM = new ThreadSM(send);
						sendMessage = new Thread(threadSM);
						sendMessage.start();
						break;
					case Commands.ACTION_FORBIDDEN:
						Console.write(read.readLine());
						break;
					case Commands.BC_MESSAGE:
						int length = read.read();
						for (int i = 0; i < length; i++) {
							Console.write(read.readLine());
						}
						break;
					case -1:
						throw new SocketException();
					default:
						Console.write("Unbekannten Befehl erhalten:" + command);
					}
				}
				server.close();
			} catch (UnknownHostException e) {
				//Tja falsche Adresse
				Console.write("Die angegebene Adresse existiert nicht.");
				tryAgain = false;
			} catch (IOException e) {
				try {
					//Verbindung wurde beendet (evtl. Server abgeschossen)
					//�berpr�fen ob der Benutzer bereits angemeldet war und entsprechend ThreadSM z.B. schlie�en
					if (wasLoggedIn) {
						threadSM.close();
						sendMessage.join();
						sendMessage = null;
						threadSM = null;
					}
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}

				if (wasLoggedIn) {
					//Wenn Benutzer bereits angemeldet war Fragen ob er versuchen m�chte den Server erneut zu erreichen
					if (Console.read("Verbindung zum Server wurde unterbrochen. Erneut versuchen", "ja").contentEquals("ja")) {
						tryAgain = false;
					}
				} else {
					//Benutzer war nicht angemeldet also ist entweder der Port falsch oder es l�uft aktuell kein MSS Server auf dieser Adresse
					Console.write("Kein MSS Server auf angegebener Adresse gefunden oder Port falsch!");
					tryAgain = false;
				}
			}
			//Entsprechend der F�lle ob Benutzer angemeldet war und ob erneut eine Verbindungsaufnahme versucht werden soll abfragen
			if (!tryAgain && !wasLoggedIn) {
				tryAgain = Console.read("Erneut probieren?", "ja").contentEquals("ja") ? false : true;
			} else if (!tryAgain && wasLoggedIn) {
				wasLoggedIn = false;
			}
		}
	}

	public static void login(PrintWriter send, String name, String password) {
		send.write(Commands.SND_LOGIN);
		send.append(name.replace("\\", "") + "\t" + password.replace("\\", "") + "\n");
//		send.append(Console.read("Geb deinen Benutzernamen ein", "Phil").replace("\\", "") + "\t"
//				+ Console.read("Gib dein Password ein", "0").replace("\\", "") + "\n");
		send.flush();
	}

}