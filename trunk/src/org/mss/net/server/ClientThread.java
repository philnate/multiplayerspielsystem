package org.mss.net.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.mss.types.Commands;
import org.mss.utils.Console;
import org.mss.windows.MainWin;

/*
 * Serverseitige Repräsentation des Remotespielers
 */
public class ClientThread implements Runnable {
	private Socket client = null;
	protected PrintWriter send = null;
	private BufferedReader read = null;
	public String username = "";
	private static SharedClientInfo sci = SharedClientInfo.getInstance();
	protected MainWin window = null;
	private boolean kicked = false;

	public void run() {
		//Wenn zum ersten mal gestartet laden der Benutzerdatei
		try {
			//Zur Liste der angemeldeten Clients hinzufügen
			send = new PrintWriter(client.getOutputStream());
			read = new BufferedReader(new InputStreamReader(client.getInputStream()));

			synchronized (send) {
				send.write(Commands.SND_LOGIN);// Login Daten anfordern.
				send.flush();
			}
			boolean resume = true;
			int command = 0;
			//Gucken was der Benutzer sendet und dem entsprechend verfahren
			while (resume) {
				switch (command = read.read()) {
				case Commands.SND_LOGIN:
					checkLogin();
					break;
				case Commands.BC_MESSAGE:
					if (!username.contentEquals("")) {
						int length = read.read();
						String message = "";
						for (int i = 0; i < length; i++) {
							message += ((i!=0)? "\n":"") + read.readLine();
						}
						sci.notifyOthers(Commands.BC_MESSAGE, message/*read.readLine()*/, this);
					} else {
						read.readLine();//Gesendete Nachricht verwerfen
						synchronized(send) {
							send.write(Commands.ACTION_FORBIDDEN);
							send.println("Nur angemeldete Benutzer dürfen Nachrichten schicken!");
							send.flush();
						}
					}
					break;
				case -1:
					throw new SocketException();
				default:
					Console.write("Unbekannten Clientcode " + command + " erhalten");
				}
			}
		} catch (IOException e) {
			if (e.getClass() == SocketException.class) {
				//Verbindung wurde getrennt also im Server anzeigen
				window.addMessage("Verbindung von Benutzer " + username + " wurde getrennt.",window.COLOR_NOTE);
			} else {
				e.printStackTrace();
			}
			//Egal welcher Fehler aufgetreten ist, der Benutzer wird vom Server abgemeldet
			//An die anderen Benutzer schicken das einer offline gegangen ist, außer bei Kick
			if (!kicked) {
				sci.notifyOthers(Commands.BC_USEROFF, this);	
			}

			try {
				client.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//Aus der Prozessliste entfernen
			sci.removeSibling(this);
			window.refreshUserlist();
			return;
		}
	}

	public ClientThread(Socket client, MainWin window) {
		this.client = client;
		this.window = window;
	}
	
	public void close() {
		try {
			kicked = true;
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Überprüfen der gelieferten Login Informationen
	 */
	public void checkLogin() throws IOException {
		String credentials = read.readLine();
		int logon = Commands.LOGIN_FAILED;//Per default wird Login nicht "akzeptiert"
		if (credentials.toLowerCase().contains("admin")) {
			synchronized (send) {
				send.write(Commands.LOGIN_ALREADY_ON);
				send.flush();
				return;
			}		
		}
		if (sci.isBanned(credentials.split("\t")[0])) {//Wenn Benutzer gebannt sperren
			synchronized (send) {
				send.write(Commands.LOGIN_BAN);
				send.flush();
				username = credentials.split("\t")[0] + "(gebannt)";
				throw new SocketException();
			}
		}
		if (sci.findSibling(credentials.split("\t")[0])) {
			logon = Commands.LOGIN_ALREADY_ON;
		}
		if (logon != Commands.LOGIN_ALREADY_ON) {
			logon = sci.findUser(credentials);
			//Existiert Benutzer in Datei?
			if (logon == Commands.LOGIN_FAILED) {
				//Nein hinzufügen
				logon = sci.addUser(credentials, false) ? Commands.LOGIN_SUCCESS : Commands.LOGIN_FAILED;
			}
			//Anmeldung hat funktioniert. Also hinzufügen und Namen setzen
			if (logon == Commands.LOGIN_SUCCESS) {
				username = credentials.split("\t")[0];
				sci.addSibling(this);
				window.refreshUserlist();
				window.addMessage("Neuer Teilnehmer:" + username, window.COLOR_NOTE);
			}
		}
		//Status der Prüfung mitteilen
		synchronized (send) {
			send.write(logon);
			send.flush();
		}
		//Bei Erfolg den anderen Teilnehmern den neuen Benutzer bekannt machen
		if (logon == Commands.LOGIN_SUCCESS) {
			sci.notifyOthers(Commands.BC_NEWUSER, this);
		}
	}

	public String toString() {
		return username;
	}
}
