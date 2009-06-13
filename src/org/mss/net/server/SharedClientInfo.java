package org.mss.net.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import org.mss.types.Commands;
import org.mss.utils.Console;

public class SharedClientInfo {
	private static ArrayList<ClientThread> siblings = new ArrayList<ClientThread>(5);
	private static SharedClientInfo sci = null;
	private static String userfile = "./userlist.txt";
	private static RandomAccessFile users = null;
	private static RandomAccessFile banned = null;
	private static String banfile = "./banlist.txt";

	private SharedClientInfo(){}
	
	public synchronized static SharedClientInfo getInstance() {
		if (sci == null) {
			sci = new SharedClientInfo();
			try {
				users = new RandomAccessFile(userfile, "rw");
				banned = new RandomAccessFile(banfile, "rw");
			} catch (FileNotFoundException e) {
				Console.write("Benutzerdatei nicht gefunden");
				e.printStackTrace();
			}
		}
		return sci;
	}
	
	public void addSibling(ClientThread sibling) {
		synchronized(siblings) {
			siblings.add(sibling);
		}
	}
	
	public void removeSibling(ClientThread sibling) {
		synchronized(siblings) {
			siblings.remove(sibling);
		}
	}
	
	public ArrayList<ClientThread> getSiblings() {
		return siblings;
	}

	public ClientThread[] getSiblingsArray() {
		return siblings.toArray(new ClientThread[0]);
	}
	
	/*
	 * Prüfen ob Benutzer bereits angemeldet ist
	 */
	public boolean findSibling(String username) {
		synchronized(siblings) {
			Iterator<ClientThread> it = siblings.iterator();
			while(it.hasNext()) {
				if (it.next().username.contentEquals(username)) {
					return true;
				}
			}
			return false;
		}
	}
	

	/*
	 * Sucht nach dem Benutzer in der Benutzerdatei
	 */
	public int findUser(String credentials) {
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
	
	public boolean isBanned(String username) {
		synchronized (banned) {
			try {
				banned.seek(0);
				String user = "";
				while (true) {
					user = banned.readLine();
					if (user == null)
						return false;
					if (user.contentEquals(username)) {
						return true;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}
	/*
	 * Fügt den Benutzer der Benutzerliste hinzu
	 */
	public boolean addUser(String credentials, boolean ban) {
		RandomAccessFile file = ban? banned:users;
		synchronized (file) {
			try {
				file.seek(file.length());
				file.writeBytes(credentials + "\n");
				return true;
			} catch (IOException e) {
				// Fehler beim Schreiben also false zurückgeben
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public void notifyOthers(int type, ClientThread sender) {
		notifyOthers(type, "", sender);
	}

	/*
	 * Übermitteln von Nachrichten an alle anderen Teilnehmer und evtl sich selbst
	 */
	public void notifyOthers(int type, String message, ClientThread sender) {
		String name = (sender != null)? sender.username:"Admin";
		if (type == Commands.BC_MESSAGE && sender != null) {
			sender.window.addMessage(name + ":" + message, sender.window.COLOR_NORMAL);
		}
		synchronized (sci.getSiblings()) {
			//Alle Teilnehmer durchschleifen und entsprechenden Befehlscode setzen und Nachricht anhängen
			for (int i = 0; i < sci.getSiblings().size(); i++) {
				ClientThread sibling = sci.getSiblings().get(i);
				if (sibling != sender) {
					synchronized (sibling.send) {
						sibling.send.write(type);
						switch (type) {
						case Commands.USER_WARN:
							sibling.send.write(Commands.USER_OTHER);
							sibling.send.println(name + " wurde verwarnt. (Grund:" + message + ")");
							break;
						case Commands.USER_KICK:
							sibling.send.write(Commands.USER_OTHER);
							sibling.send.println(name + " wurde gekickt. (Grund:" + message + ")");
							break;
						case Commands.USER_BAN:
							sibling.send.write(Commands.USER_OTHER);
							sibling.send.println(name + " wurde gebannt. (Grund:" + message + ")");
							break;
						case Commands.BC_NEWUSER: // ATT:Nicht einfach
													// vertauschen Code von
													// USEROFF nötig
						case Commands.BC_USEROFF:
							sibling.send.println(name);
							break;
						case Commands.BC_MESSAGE:
							sibling.send.println(name + ":" + message);
							break;
						default:
							break;
						}
						sibling.send.flush();
					}
				} else {
					//Besondere Nachrichten die an sich selbst geschickt werden müssen
					synchronized (sender.send) {
						switch (type) {
						case Commands.BC_NEWUSER:
							sender.send.write(Commands.USERLIST);
							//Die Liste mit allen aktuellen Teilnehmern schicken
							Iterator<ClientThread> it = SharedClientInfo.siblings.iterator();
							while (it.hasNext()) {
								sender.send.print(it.next().username + "\t");
							}
							sender.send.println();
							break;
						case Commands.USER_WARN://ATT nicht einfach verschieben
						case Commands.USER_KICK:
						case Commands.USER_BAN:
							sender.send.write(type);
							sender.send.write(Commands.USER_SELF);
							sender.send.println(message);
							break;
						}
						sender.send.flush();
					}
				}
			}
		}
	}
}
