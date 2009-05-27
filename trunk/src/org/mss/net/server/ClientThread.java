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

		if (users == null) {
			try {
				users = new RandomAccessFile(userfile, "rw");
			} catch (FileNotFoundException e) {
				Console.write("Benutzerdatei nicht gefunden");
				e.printStackTrace();
			}
		}
		try {
			mySiblings.add(this);
			send = new PrintWriter(client.getOutputStream());
			read = new BufferedReader(new InputStreamReader(client.getInputStream()));
			synchronized(send) {
				send.write(Commands.SND_LOGIN);//Login Daten anfordern.
				send.flush();
			}
			boolean resume = true;
			int command = 0;
			while (resume) {
				switch (command = read.read()) {
				case Commands.SND_LOGIN:
					checkLogin();
					break;
				case Commands.BC_MESSAGE:
					notifyOthers(Commands.BC_MESSAGE, read.readLine());
					break;
				default: Console.write("Unbekannten Clientcode " + command + " erhalten");
				}
			}
		} catch (IOException e) {
			if (e.getClass() == SocketException.class) {
				Console.log("Verbindung von Benutzer " + username + " wurde getrennt.");
			} else {
				e.printStackTrace();
			}
			try {
				client.close();//Verbindung beenden
				activeUser.remove(username);
				notifyOthers(Commands.BC_USEROFF);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
//			try {
//				this.join();//Thread beenden
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}
			mySiblings.remove(this);
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
		synchronized(users) {
			try {
				if (users.getFilePointer() != 0) {
					users.seek(0);
				}
				String user = "";
				while (true) {
					if ((user = users.readLine()) == null) break; 
					if (user.contains(credentials.subSequence(0, credentials.indexOf("\t")))) {
						logon = user.contentEquals(credentials)? Commands.LOGIN_SUCCESS:Commands.LOGIN_PASSWRONG;//Benutzerkennung korrekt 1, oder falsches Passwort 2						
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
	 * Fügt den Benutzer der Benutzer liste hinzu
	 */
	public static boolean addUser(String credentials) {
		synchronized(users) {
			try {
				if (users.getFilePointer() != users.length()) {
					users.seek(users.length());
				}
				users.writeBytes(credentials+"\n");
				return true;
			} catch (IOException e) {
				// Fehler beim Schreiben also false zurückgeben
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public void checkLogin() throws IOException {
		String credentials = read.readLine();
		int logon = Commands.LOGIN_FAILED;
		for (int i = 0; i < activeUser.size(); i++) {
			if (activeUser.get(i).contentEquals(credentials.split("\t")[0])) {
				logon = Commands.LOGIN_ALREADY_ON;
				break;
			}
		}
		if (logon != Commands.LOGIN_ALREADY_ON) {
			logon = findUser(credentials);
			if (logon == Commands.LOGIN_FAILED) {
				logon = addUser(credentials)? Commands.LOGIN_SUCCESS:Commands.LOGIN_FAILED;
			}
			if (logon == Commands.LOGIN_SUCCESS) {
				username = credentials.split("\t")[0];
				activeUser.add(username);
				Console.log("Neuer Teilnehmer:" + username);
			}
		}
		synchronized(send) {
			send.write(logon);
			send.flush();
		}
		if (logon == Commands.LOGIN_SUCCESS) {
			notifyOthers(Commands.BC_NEWUSER);
		}
	}
	
	public void notifyOthers(int type) {
		notifyOthers(type, "");
	}
	
	public void notifyOthers(int type, String message) {
//		Thread[] mySiblings = new Thread[ClientThread.activeCount()];
//		ClientThread.enumerate(mySiblings);
//		for (int i = 0; i < mySiblings.length; i++) {
//			if (mySiblings[i].getClass() == this.getClass()) {
//				ClientThread  sibling = (ClientThread) mySiblings[i];
//				if (sibling != this) {
		for (int i = 0; i < mySiblings.size(); i++) {
				ClientThread sibling = mySiblings.get(i);
				if (sibling != this) {
					synchronized (sibling.send) {
						sibling.send.write(type);
						switch (type) {
						case Commands.BC_NEWUSER: //ATT:Nicht einfach vertauschen Code von USEROFF nötig
						case Commands.BC_USEROFF:
							sibling.send.println(username);
							break;
						case Commands.BC_MESSAGE:
							sibling.send.println(username + ":" + message);
							break;
						default: break;
						}
						sibling.send.flush();
					}
				} else {
					synchronized(send) {
						if (type == Commands.BC_NEWUSER) {
							send.write(Commands.USERLIST);
							for (int j = 0; j < activeUser.size(); j++) {
								send.print(activeUser.get(j)+"\t");	
							}
							send.println();
							send.flush();
						}
					}
				}
			}
		}
//	}
}
