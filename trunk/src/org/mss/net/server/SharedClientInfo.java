package org.mss.net.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import org.mss.Spieler;
import org.mss.types.MSSDataObject;
import org.mss.utils.Console;

public class SharedClientInfo {
	private static ArrayList<ClientThread> siblings = new ArrayList<ClientThread>(5);
	private static ArrayList<String> games = new ArrayList<String>(10);

	private static SharedClientInfo sci = null;
	private static String userfile = "./userlist.txt";
	private static RandomAccessFile users = null;
	private static RandomAccessFile banned = null;
	private static String banfile = "./banlist.txt";
	private static Spieler admin = new Spieler("Admin");

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
	
	public void addGame(String game) {
		synchronized (games) {
			games.add(game);
		}
	}
	
	public void removeGame(String game) {
		synchronized(games) {
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).contains(game)) {
					games.remove(i);
					return;
				}
			}
		}
	}

	public String[] getGames() {
		return games.toArray(new String[0]);
	}
	/*
	 * Prüfen ob Benutzer bereits angemeldet ist
	 */
	public boolean findSibling(String username) {
		synchronized(siblings) {
			Iterator<ClientThread> it = siblings.iterator();
			while(it.hasNext()) {
				if (it.next().myself.getName().contentEquals(username)) {
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
		int logon = MSSDataObject.LOGIN_FAILED;
		synchronized (users) {
			try {
				users.seek(0);
				String user = "";
				//Alle Benutzer durch schleifen und schauen ob Name enthalten ist und ob das Passwort übereinstimmt
				while (true) {
					if ((user = users.readLine()) == null)
						break;
					if (user.contains(credentials.subSequence(0, credentials.indexOf("\t")))) {
						logon = user.contentEquals(credentials) ? MSSDataObject.LOGIN_SUCCESS : MSSDataObject.LOGIN_PASSWRONG;
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
		String name = (sender != null)? sender.myself.getName():admin.getName();
		if (type == MSSDataObject.BC_MESSAGE && sender != null) {
			sender.window.addMessage(name + ":" + message, name.hashCode());
		}
		synchronized (sci.getSiblings()) {
			//Alle Teilnehmer durchschleifen und entsprechenden Befehlscode setzen und Nachricht anhängen
			for (int i = 0; i < sci.getSiblings().size(); i++) {
				ClientThread sibling = sci.getSiblings().get(i);
				Object data = message;
				Spieler fromUser;
				if (sender != null) {
					fromUser = sender.myself;
				} else {
					fromUser = admin;
				}
				if (sibling != sender) {
					synchronized (sibling.send) {
						try {
							sibling.send.writeObject(new MSSDataObject(type,data, fromUser));
							sibling.send.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					//Besondere Nachrichten die an sich selbst geschickt werden müssen
					synchronized (sender.send) {
						switch (type) {
						case MSSDataObject.BC_NEWUSER:
							//Die Liste mit allen aktuellen Teilnehmern schicken
							type=MSSDataObject.USERLIST;
							Iterator<ClientThread> it = SharedClientInfo.siblings.iterator();
							
							Spieler[] users = new Spieler[SharedClientInfo.siblings.size()];
							int j = 0;
							while (it.hasNext()) {
								users[j] = it.next().myself;
								j++;
							}
							data = users;
							break;
						case MSSDataObject.BC_USEROFF:
						case MSSDataObject.BC_MESSAGE:
							continue;//Warum sich selber die Nachricht schicken?
						}
						try {
							sender.send.writeObject(new MSSDataObject(type, data, fromUser));
							sender.send.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void sendTo(MSSDataObject data) {
		synchronized (siblings) {
			Iterator<ClientThread> it = siblings.iterator();
			Spieler current;
			ClientThread curThread;
			while(it.hasNext()) {
				curThread = it.next();
				current = curThread.myself;
				for (int i=0; i < data.getToUser().length; i++) {
					if (current.toString().contentEquals(data.getToUser()[i].toString())) {
						try {
							curThread.send.writeObject(data);
							curThread.send.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
