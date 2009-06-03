package org.mss.net.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.mss.types.Commands;
import org.mss.utils.Console;

/*
 * Serverseitige Repräsentation des Remotespielers
 */
public class ClientThread implements Runnable {
	private static String userfile = "./userlist.txt";
	private static RandomAccessFile users = null;
	private Socket client = null;
	private PrintWriter send = null;
	private BufferedReader read = null;
	public static ArrayList<String> activeUser = new ArrayList<String>(10);
	private String username = "nicht authentifizierter Teilnehmer";
	private static ArrayList<ClientThread> mySiblings = new ArrayList<ClientThread>(5);

	public void run() {
		//Wenn zum ersten mal gestartet laden der Benutzerdatei
		if (users == null) {
			try {
				users = new RandomAccessFile(userfile, "rw");
			} catch (FileNotFoundException e) {
				Console.write("Benutzerdatei nicht gefunden");
				e.printStackTrace();
			}
		}
		try {
			//Zur Liste der angemeldeten Clients hinzufügen
			synchronized (mySiblings) {
				mySiblings.add(this);
			}
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
						notifyOthers(Commands.BC_MESSAGE, read.readLine());
					} else {
						read.readLine();//Gesendete Nachricht verwerfen
						synchronized(send) {
							send.write(Commands.ACTION_FORBIDDEN);
							send.println("Nur angemeldete Benutzer dürfen Nachrichten schicken");
							send.flush();
						}
					}
					break;
				default:
					Console.write("Unbekannten Clientcode " + command + " erhalten");
				}
			}
		} catch (IOException e) {
			if (e.getClass() == SocketException.class) {
				//Verbindung wurde getrennt also im Server anzeigen
				Console.log("Verbindung von Benutzer " + username + " wurde getrennt.");
			} else {
				e.printStackTrace();
			}
			//Egal welcher Fehler aufgetreten ist, der Benutzer wird vom Server abgemeldet
			synchronized (activeUser) {
				//Entfernen aus der Benutzerliste
				activeUser.remove(username);
			}
			//An die anderen Benutzer schicken das einer offline gegangen ist
			notifyOthers(Commands.BC_USEROFF);
			try {
				client.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//Aus der Prozessliste entfernen
			synchronized (mySiblings) {
				mySiblings.remove(this);
			}
			return;
		}
	}

	public ClientThread(Socket client) {
		this.client = client;
	}

	/*
	 * Sucht nach dem Benutzer in der Benutzerdatei
	 */
	public static int findUser(String credentials) {
		int logon = Commands.LOGIN_FAILED;
		synchronized (users) {
			try {
				users.seek(0);
				String user = "";
				//Alle Benutzer durch schleifen und schauen ob Name enthalten ist und ob das Passwort übereinstimmt
				while (true) {
					if ((user = users.readLine()) == null)
						break;
					if (user.contains(credentials.subSequence(0, credentials.indexOf("\t")))) {
						logon = user.contentEquals(credentials) ? Commands.LOGIN_SUCCESS : Commands.LOGIN_PASSWRONG;
						// Benutzerkennung korrekt oder falsches Passwort
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logon;
	}

	/*
	 * Fügt den Benutzer der Benutzerliste hinzu
	 */
	public static boolean addUser(String credentials) {
		synchronized (users) {
			try {
				users.seek(users.length());
				users.writeBytes(credentials + "\n");
				return true;
			} catch (IOException e) {
				// Fehler beim Schreiben also false zurückgeben
				e.printStackTrace();
				return false;
			}
		}
	}

	/*
	 * Überprüfen der gelieferten Login Informationen
	 */
	public void checkLogin() throws IOException {
		String credentials = read.readLine();
		int logon = Commands.LOGIN_FAILED;//Per default wird Login nicht "akzeptiert"
		for (int i = 0; i < activeUser.size(); i++) {
			//Prüfen ob bereits ein Benutzer mit diesem Namen angemeldet ist
			if (activeUser.get(i).contentEquals(credentials.split("\t")[0])) {
				logon = Commands.LOGIN_ALREADY_ON;
				break;
			}
		}
		if (logon != Commands.LOGIN_ALREADY_ON) {
			logon = findUser(credentials);
			//Existiert Benutzer in Datei?
			if (logon == Commands.LOGIN_FAILED) {
				//Nein hinzufügen
				logon = addUser(credentials) ? Commands.LOGIN_SUCCESS : Commands.LOGIN_FAILED;
			}
			//Anmeldung hat funktioniert. Also hinzufügen und Namen setzen
			if (logon == Commands.LOGIN_SUCCESS) {
				username = credentials.split("\t")[0];
				synchronized (activeUser) {
					activeUser.add(username);
				}
				Console.log("Neuer Teilnehmer:" + username);
			}
		}
		//Status der Prüfung mitteilen
		synchronized (send) {
			send.write(logon);
			send.flush();
		}
		//Bei Erfolg den anderen Teilnehmern den neuen Benutzer bekannt machen
		if (logon == Commands.LOGIN_SUCCESS) {
			notifyOthers(Commands.BC_NEWUSER);
		}
	}

	public void notifyOthers(int type) {
		notifyOthers(type, "");
	}

	/*
	 * Übermitteln von Nachrichten an alle anderen Teilnehmer und evtl sich selbst
	 */
	public void notifyOthers(int type, String message) {
		synchronized (mySiblings) {
			//Alle Teilnehmer durchschleifen und entsprechenden Befehlscode setzen und Nachricht anhängen
			for (int i = 0; i < mySiblings.size(); i++) {
				ClientThread sibling = mySiblings.get(i);
				if (sibling != this) {
					synchronized (sibling.send) {
						sibling.send.write(type);
						switch (type) {
						case Commands.BC_NEWUSER: // ATT:Nicht einfach
													// vertauschen Code von
													// USEROFF nötig
						case Commands.BC_USEROFF:
							sibling.send.println(username);
							break;
						case Commands.BC_MESSAGE:
							sibling.send.println(username + ":" + message);
							break;
						default:
							break;
						}
						sibling.send.flush();
					}
				} else {
					//Besondere Nachrichten die an sich selbst geschickt werden müssen
					synchronized (send) {
						if (type == Commands.BC_NEWUSER) {
							//Die Liste mit allen aktuellen Teilnehmern schicken
							send.write(Commands.USERLIST);
							for (int j = 0; j < activeUser.size(); j++) {
								send.print(activeUser.get(j) + "\t");
							}
							send.println();
							send.flush();
						}
					}
				}
			}
		}
	}
}
