package org.mss.net.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mss.Spieler;
import org.mss.types.MSSDataObject;
import org.mss.utils.Console;
import org.mss.windows.MainWin;

/*
 * Serverseitige Repräsentation des Remotespielers
 */
public class ClientThread implements Runnable {
	private Socket client = null;
	protected ObjectOutputStream send = null;
	private ObjectInputStream read = null;
	private static SharedClientInfo sci = SharedClientInfo.getInstance();
	protected MainWin window = null;
	private boolean kicked = false;
	public Spieler myself;

	public void run() {
		//Wenn zum ersten mal gestartet laden der Benutzerdatei
		try {
			//Zur Liste der angemeldeten Clients hinzufügen
			send = new ObjectOutputStream(client.getOutputStream());
			send.flush();
			read = new ObjectInputStream(client.getInputStream());

			synchronized (send) {
				send.writeObject(new MSSDataObject(MSSDataObject.SND_LOGIN));
				send.flush();
			}
			boolean resume = true;
			//Gucken was der Benutzer sendet und dem entsprechend verfahren
			while (resume) {
				MSSDataObject inData = null;
				try {
					inData = (MSSDataObject) read.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (EOFException e) {
					//Verbindung wurde beendet
					e.printStackTrace();
					throw new SocketException();
				}
				switch (inData.getType()) {
				case MSSDataObject.SND_LOGIN:
					checkLogin(inData);
					break;
				case MSSDataObject.BC_MESSAGE:
					if (myself != null) {
						sci.notifyOthers(MSSDataObject.BC_MESSAGE, (String) inData.getData(), this);
					} else {
						synchronized(send) {
							send.writeObject(new MSSDataObject(MSSDataObject.ACTION_FORBIDDEN,"Nur angemeldete Benutzer dürfen Nachrichten schicken!"));
							send.flush();
						}
					}
					break;
				case MSSDataObject.GAME_ANSWER:
					sci.addGame(this.myself.getName() + "-" + inData.getToUser()[0].getName());
					window.refreshGames();
					sci.sendTo(inData);
					break;
				case MSSDataObject.GAME_REQUEST:
				case MSSDataObject.GAME_TURN:
					sci.sendTo(inData);
					break;
				case MSSDataObject.GAME_CLOSED:
					sci.removeGame(this.myself.getName());
					window.refreshGames();
					if (inData.getToUser() != null) sci.sendTo(inData);//Einer hat gewonnen
					break;
				case -1:
					throw new SocketException();
				default:
					Console.write("Unbekannten Clientcode " + inData.getType() + " erhalten");
				}
			}
		} catch (IOException e) {
			if (e.getClass() == SocketException.class) {
				//Verbindung wurde getrennt also im Server anzeigen
				if (myself != null) {
					window.addMessage("Verbindung von Benutzer " + myself.getName() + " wurde getrennt.",window.COLOR_NOTE);
					sci.removeGame(myself.getName());
					window.refreshGames();
				}
			} else {
				e.printStackTrace();
			}
			//Egal welcher Fehler aufgetreten ist, der Benutzer wird vom Server abgemeldet
			//An die anderen Benutzer schicken das einer offline gegangen ist, außer bei Kick
			if (!kicked) {
				sci.notifyOthers(MSSDataObject.BC_USEROFF, this);	
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
	public void checkLogin(MSSDataObject data) throws IOException {
		String credentials = (String) data.getData();
		int logon = MSSDataObject.LOGIN_FAILED;//Per default wird Login nicht "akzeptiert"
		if (credentials.toLowerCase().contains("admin")) {
			synchronized (send) {
				send.writeObject(MSSDataObject.LOGIN_ALREADY_ON);
				send.flush();
				return;
			}		
		}
		if (sci.isBanned(credentials.split("\t")[0])) {//Wenn Benutzer gebannt sperren
			synchronized (send) {
				send.writeObject(MSSDataObject.LOGIN_BAN);
				send.flush();
				myself = new Spieler(credentials.split("\t")[0] + "(gebannt)");
				close();
			}
		}
		if (sci.findSibling(credentials.split("\t")[0])) {
			logon = MSSDataObject.LOGIN_ALREADY_ON;
		}
		if (logon != MSSDataObject.LOGIN_ALREADY_ON) {
			logon = sci.findUser(credentials);
			//Existiert Benutzer in Datei?
			if (logon == MSSDataObject.LOGIN_FAILED) {
				//Nein hinzufügen
				logon = sci.addUser(credentials, false) ? MSSDataObject.LOGIN_SUCCESS : MSSDataObject.LOGIN_FAILED;
			}
			//Anmeldung hat funktioniert. Also hinzufügen und Namen setzen
			if (logon == MSSDataObject.LOGIN_SUCCESS) {
				myself = new Spieler(credentials.split("\t")[0]);
				sci.addSibling(this);
				window.refreshUserlist();
				window.addMessage("Neuer Teilnehmer:" + myself.getName(), window.COLOR_NOTE);
			}
		}
		//Status der Prüfung mitteilen
		synchronized (send) {
			send.writeObject(new MSSDataObject(logon,myself));
			send.flush();
		}
		//Bei Erfolg den anderen Teilnehmern den neuen Benutzer bekannt machen
		if (logon == MSSDataObject.LOGIN_SUCCESS) {
			sci.notifyOthers(MSSDataObject.BC_NEWUSER, this);
		}
	}

	public String toString() {
		return myself.getName();
	}
}
