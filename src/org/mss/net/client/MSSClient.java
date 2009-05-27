package org.mss.net.client;

import org.mss.types.Commands;
import org.mss.utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class MSSClient {
	

	public static void main(String[] args) {
		
		Socket server = null;
		int port = 62742;
		Thread sendMessage = null;
		ThreadSM threadSM = null;
		String addr = "localhost";
		Console.write("Willkommen bei MSS. Dem Spielsystem der Zukunft!");
		addr = Console.read("Bitte geb die Adresse des MSS Servers an auf den du dich anmelden willst!", addr);
		port = Console.read("Bitte geb den Port für " + addr + " an, wo der MSS Server lauscht!", port);
		boolean tryAgain = false;
		boolean wasLoggedIn = false;
		
		while (!tryAgain) {
			try {
				server = new Socket(addr,port);
				tryAgain = true;
				PrintWriter send = new PrintWriter(server.getOutputStream());
				BufferedReader read = new BufferedReader(new InputStreamReader(server.getInputStream()));
				Console.write("Verbindung hergestellt");

				int command = 0;
				boolean resume = true;
				while (resume) {
					switch (command = read.read()) {
					case Commands.SND_LOGIN:
							login(send);
							break;
					case Commands.LOGIN_SUCCESS:
							Console.write("Erfolgreich angemeldet!");
							wasLoggedIn = true;
							break;
					case Commands.LOGIN_FAILED:
							if (Console.read("Anmeldung fehlgeschlagen. Erneut versuchen","ja").contentEquals("ja")) {
								login(send);//Erneute Anmeldung
							} else {
								resume = false;
							}
							break;
					case Commands.LOGIN_PASSWRONG:
						if (Console.read("Passwort falsch. Erneut probieren","ja").contentEquals("ja")) {
							login(send);//Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case Commands.LOGIN_ALREADY_ON:
						if (Console.read("Es ist bereits ein Benutzer mit diesem Namen angemeldet. Als anderer Benutzer anmelden", "ja").contentEquals("ja")) {
							login(send);
						} else {
							resume = false;
						}
						break;
					case Commands.BC_NEWUSER:
							Console.write(read.readLine() + " hat sich angemeldet!");
						break;
					case Commands.BC_USEROFF:
							Console.write(read.readLine() + " hat sich abgemeldet!");
						break;
					case Commands.USERLIST:
							String[] activeUser = read.readLine().split("\t");
							Console.write("Aktuell angemeldete Benutzer:");
							for (int i = 0; i < activeUser.length; i++) {
								Console.write(activeUser[i]);
							}
							threadSM = new ThreadSM(send);
							sendMessage = new Thread(threadSM);
							sendMessage.start();
						break;
					case Commands.BC_MESSAGE:
							Console.write(read.readLine());
						break;
					default: Console.write("Unbekannten Befehl erhalten:" + command);
					}
				}
				server.close();
			} catch (UnknownHostException e) {
				Console.write("Die angegebene Adresse existiert nicht.");
				tryAgain = false;
			} catch (IOException e) {
				try {
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
					if (Console.read("Verbindung zum Server wurde unterbrochen. Erneut versuchen", "ja").contentEquals("ja")) {
						tryAgain = false;
					}
				} else {
					Console.write("Kein MSS Server auf angegebener Adresse gefunden oder Port falsch!");
					tryAgain = false;
				}
			}
			System.out.println("hier bin ich");
			//TODO Beim anmelden eines Benutzers der bereits angemeldet ist und dann wechsel auf anderen: ¤òPhil	MeMe	 hat sich angemeldet!
			if (!tryAgain && !wasLoggedIn) {
				tryAgain = Console.read("Erneut probieren?","ja").contentEquals("ja")? false:true;
			} else if (!tryAgain && wasLoggedIn) {
				wasLoggedIn = false;
			}
		}
	}
	
	public static void login(PrintWriter send) {
		send.write(Commands.SND_LOGIN);
		send.append(Console.read("Geb deinen Benutzernamen ein", "Phil").replace("\\", "") + "\t" + Console.read("Gib dein Password ein", "0").replace("\\", "")+ "\n");
		send.flush();
	}

}
