package org.mss.net.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mss.types.MSSDataObject;
import org.mss.utils.Console;
import org.mss.windows.MainWin;

/*
 * Serverseitige Repräsentation des Remotespielers
 */
public class ClientThread implements Runnable {
	private Socket client = null;
	protected ObjectOutputStream snd = null;
	private ObjectInputStream red = null;
	public String username = "";
	private static SharedClientInfo sci = SharedClientInfo.getInstance();
	protected MainWin window = null;
	private boolean kicked = false;

	public void run() {
		//Wenn zum ersten mal gestartet laden der Benutzerdatei
		try {
			//Zur Liste der angemeldeten Clients hinzufügen
			snd = new ObjectOutputStream(client.getOutputStream());
			snd.flush();
			red = new ObjectInputStream(client.getInputStream());

			synchronized (snd) {
				snd.writeObject(new MSSDataObject(MSSDataObject.SND_LOGIN));
				snd.flush();
			}
			boolean resume = true;
			int command = 0;
			//Gucken was der Benutzer sendet und dem entsprechend verfahren
			while (resume) {
				MSSDataObject inData = null;
				try {
					inData = (MSSDataObject) red.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				switch (command = read.read()) {
				switch (inData.getType()) {
				case MSSDataObject.SND_LOGIN:
					checkLogin(inData);
					break;
				case MSSDataObject.BC_MESSAGE:
					if (!username.contentEquals("")) {
						sci.notifyOthers(MSSDataObject.BC_MESSAGE, (String) inData.getData()/*message*/, this);
					} else {
						synchronized(snd) {
							snd.writeObject(new MSSDataObject(MSSDataObject.ACTION_FORBIDDEN,"Nur angemeldete Benutzer dürfen Nachrichten schicken!"));
							snd.flush();
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
		String credentials = (String) data.getData();//read.readLine();
		int logon = MSSDataObject.LOGIN_FAILED;//Per default wird Login nicht "akzeptiert"
		if (credentials.toLowerCase().contains("admin")) {
			synchronized (snd) {
				snd.writeObject(MSSDataObject.LOGIN_ALREADY_ON);
				snd.flush();
				return;
			}		
		}
		if (sci.isBanned(credentials.split("\t")[0])) {//Wenn Benutzer gebannt sperren
			synchronized (snd) {
				snd.writeObject(MSSDataObject.LOGIN_BAN);
				snd.flush();
				username = credentials.split("\t")[0] + "(gebannt)";
				throw new SocketException();
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
				username = credentials.split("\t")[0];
				sci.addSibling(this);
				window.refreshUserlist();
				window.addMessage("Neuer Teilnehmer:" + username, window.COLOR_NOTE);
			}
		}
		//Status der Prüfung mitteilen
		synchronized (snd) {
			snd.writeObject(new MSSDataObject(logon));
			snd.flush();
		}
		//Bei Erfolg den anderen Teilnehmern den neuen Benutzer bekannt machen
		if (logon == MSSDataObject.LOGIN_SUCCESS) {
			sci.notifyOthers(MSSDataObject.BC_NEWUSER, this);
		}
	}

	public String toString() {
		return username;
	}
}
