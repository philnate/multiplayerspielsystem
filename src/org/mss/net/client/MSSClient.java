package org.mss.net.client;

import org.mss.Spiel;
import org.mss.Spieler;
import org.mss.games.Chomp;
import org.mss.games.Viergewinnt;
import org.mss.types.MSSDataObject;
import org.mss.utils.listener.CurGameClosedListener;
import org.mss.windows.ClientMainWin;
import org.mss.windows.NoticeWin;
import org.mss.windows.QueryWinDouble;
import org.mss.windows.QueryWinYesNo;

import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

/*
 * Spielerhauptklasse
 */
public class MSSClient {

	public static void main(String[] args) {

		Socket server = null;
		String addr = "localhost";
		int port = 62742;	
		Spieler myself = null;
		ArrayList<Spieler> users = new ArrayList<Spieler>(10);
		QueryWinDouble gwd = new QueryWinDouble("Verbindungsangaben","Host", "Port", "Ok", addr, Integer.toString(port), new Dimension(300,100));
		QueryWinYesNo qyn;
		NoticeWin nw;
		Thread run;
		gwd.show();
		if (gwd.isCanceled()) System.exit(0);
		
		addr = gwd.getInput1Text();		
		port = Integer.valueOf(gwd.getInput2Text()).intValue();

		boolean tryAgain = false;
		boolean wasLoggedIn = false;
		ClientMainWin guiCMainWin = null;
		Spiel curGame = null;

		//Falls Verbindungsabbrüche und derart stattfanden kann der Spieler es erneut versuchen
		while (!tryAgain) {
			try {
				server = new Socket(addr, port);
				tryAgain = true;
				final ObjectOutputStream send = new ObjectOutputStream(server.getOutputStream());
				send.flush();
				//Damit Gegenstelle InputStream öffnen kann
				ObjectInputStream red = new ObjectInputStream(server.getInputStream());
				if (guiCMainWin == null) {
					guiCMainWin = new ClientMainWin(send, curGame);
				}

				boolean resume = true;
				//Auf Befehle warten solange nichts gegenteiliges Empfangen wurde oder eintritt
				while (resume) {
					MSSDataObject inData = null;
					try {
						inData = (MSSDataObject) red.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassCastException e) {
						//Verbindung wurde beendet, gebannt
						inData = new MSSDataObject(MSSDataObject.LOGIN_BAN);
					}
					switch (inData.getType()) {
					case MSSDataObject.SND_LOGIN:
						login(send);
						break;
					case MSSDataObject.LOGIN_SUCCESS:
						wasLoggedIn = true;
						myself =(Spieler) inData.getData();
						guiCMainWin.setSpieler(myself);
						guiCMainWin.setVisible(true);
						break;
					case MSSDataObject.LOGIN_FAILED:
						qyn = new QueryWinYesNo ("Anmeldung fehlgeschlagen!", "Anmeldung fehlgeschlagen. Erneut versuchen?", new Dimension(300,100));
						qyn.show();
						if (qyn.getAnswer()) {
							login(send);// Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case MSSDataObject.LOGIN_PASSWRONG:
						qyn = new QueryWinYesNo ("Passwort falsch!", "Falsches Passwort! Erneut versuchen?", new Dimension(300,100));
						qyn.show();
						if (qyn.getAnswer()) {
							login(send);// Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case MSSDataObject.LOGIN_ALREADY_ON:
						qyn = new QueryWinYesNo ("Bereits online!", "Es ist Bereits ein Benutzer mit diesen Namen online. Als anderer Benutzer anmelden?", new Dimension(300,100));
						qyn.show();
						if (qyn.getAnswer()) {
							login(send);//Erneute Anmeldung
						} else {
							resume = false;
						}
						break;
					case MSSDataObject.LOGIN_BAN:
						nw = new NoticeWin("Gebannt!", "Benutzer ist vom Server gebannt!", new Dimension(300,100));
						nw.show();
						resume = false;
						break;
					case MSSDataObject.BC_NEWUSER:
						Spieler user = inData.getFromUser();
						users.add(user);
						guiCMainWin.addMessage(user.getName() + " hat sich angemeldet!");
						guiCMainWin.refreshUsers(users);
						break;
					case MSSDataObject.BC_USEROFF:
						Spieler usr = inData.getFromUser();
						users.remove(usr);
						guiCMainWin.addMessage(usr.getName() + " hat sich abgemeldet!");
						guiCMainWin.refreshUsers(users);
						if (curGame != null && !curGame.isClosed()) {
							Spieler[] aktuell = curGame.listeSpieler();
							for (int i= 0; i < aktuell.length; i++) {
								if (aktuell[i].getName().equals(usr.getName())) {
									nw = new NoticeWin("Spielbeendet", "Dein Gegenspieler hat das Spiel beendet!", new Dimension(300,100));
									nw.show();
									curGame.close();
									guiCMainWin.addMessage("Das Spiel wurde von deinem Gegenspieler verlassen!");
									break;
								}
							}
						}
						break;
					case MSSDataObject.USER_WARN://ATTN nicht einfach so verschieben Code in USER_BAN wichtig
					case MSSDataObject.USER_KICK:// -''-
					case MSSDataObject.USER_BAN:
						String nameIt = "";
						switch (inData.getType()) {
						case MSSDataObject.USER_WARN:
							nameIt = "verwarnt"; 
							break;
						case MSSDataObject.USER_KICK:
							nameIt = "gekickt";
							break;
						case MSSDataObject.USER_BAN:
							nameIt = "gebannt";
							break;
						}
						
						if (inData.getFromUser() == myself) {
							guiCMainWin.addMessage("Du wurdest " + nameIt + "! Grund:" + inData.getData()); //+ read.readLine());
						} else {
							guiCMainWin.addMessage("Benutzer " + inData.getFromUser().getName()+ " wurde " + nameIt + "! Grund:" + inData.getData());
						}
						break;
					case MSSDataObject.USERLIST:
						Spieler[] curUsers = (Spieler[]) inData.getData();
						for (int i = 0; i < curUsers.length; i++) {
							users.add(curUsers[i]);
						}
						guiCMainWin.refreshUsers(users);
						break;
					case MSSDataObject.ACTION_FORBIDDEN:
						guiCMainWin.addMessage((String) inData.getData());
						break;
					case MSSDataObject.BC_MESSAGE:
						guiCMainWin.addMessage(inData.getFromUser().getName() + ":" + (String)inData.getData());
						break;
					case MSSDataObject.GAME_REQUEST:
						Spieler answer = null;
						if (curGame == null || curGame.isClosed()) {
							qyn = new QueryWinYesNo("Spieleinladung", "Der Spieler "+ inData.getFromUser().getName() + " möchte Sie auf eine Party " + inData.getData().toString() + " einladen", new Dimension(400,100));
							//TODO nicht weitere Nachrichten blockieren..
							qyn.show();
							if (qyn.getAnswer()) {
								answer = myself;
								if (inData.getData().toString().contentEquals("Chomp")) {
									curGame = new Chomp();
								} else {
									curGame = new Viergewinnt();
								}
								try {
									curGame.plusSpieler(inData.getFromUser());
									curGame.plusSpieler(myself);
									curGame.addListener(new CurGameClosedListener(send, inData.getFromUser(),guiCMainWin, curGame));
									curGame.show();
									guiCMainWin.addMessage("Du befindest dich jetzt im Spiel mit " + inData.getFromUser().getName());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						send.writeObject(new MSSDataObject(MSSDataObject.GAME_ANSWER, answer,new Spieler[]{inData.getFromUser()},myself,(String)inData.getData()));
						send.flush();
						break;
					case MSSDataObject.GAME_ANSWER:
						if (inData.getData() == null) { 
							nw = new NoticeWin("Spielanfrage","Ihre Anfrage an " + inData.getFromUser().getName() + " bezüglich einer Runde " + inData.getAdditionalData() +" wurde abgelehnt!", new Dimension(300, 100));
							nw.show();
						} else {
							if (inData.getAdditionalData().contentEquals("Chomp")) {
								curGame = new Chomp();
							} else {
								curGame = new Viergewinnt();
							}
							try {
								curGame.plusSpieler(myself);
								curGame.plusSpieler(inData.getFromUser());
								curGame.addListener(new CurGameClosedListener(send, inData.getFromUser(),guiCMainWin, curGame));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							guiCMainWin.addMessage("Du befindest dich jetzt im Spiel mit " + inData.getFromUser().getName());
							curGame.show();
							run = new Thread(new RunnableTurn(true, send, inData, myself, curGame, guiCMainWin));
							run.start();
						}
						break;
					case MSSDataObject.GAME_TURN:
						for (int i = 0; i < curGame.listeSpieler().length; i++) {
							if (inData.getFromUser() == curGame.listeSpieler()[i]) {
								run = new Thread(new RunnableTurn(false, send, inData, myself, curGame, guiCMainWin));
								run.start();
								break;
							}
						}
						break;
					case MSSDataObject.GAME_CLOSED:
						nw = new NoticeWin("Spielbeendet", "Dein Gegenspieler hat das Spiel beendet!", new Dimension(300,100));
						nw.show();
						curGame.close();
//						curGame.dispose();
//						curGame = null;
						guiCMainWin.addMessage("Das Spiel wurde von deinem Gegenspieler verlassen!");
						break;
					default:
						guiCMainWin.addMessage("Unbekannten Befehl erhalten:" + inData.getType());
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
		if (guiCMainWin != null) {
			guiCMainWin.dispose();
		}
		guiCMainWin = null;
	}

	public static void login(ObjectOutputStream snd) throws IOException {
		QueryWinDouble gwd = new QueryWinDouble("Logindaten", "Benutzername", "Passwort", "Ok", "Phil", "0", new Dimension(300,100));
		gwd.show();
		if (gwd.isCanceled()) System.exit(0);
		
		snd.writeObject(new MSSDataObject(MSSDataObject.SND_LOGIN, gwd.getInput1Text().replace("\\", "") + "\t" + gwd.getInput2Text().replace("\\", "")));
		snd.flush();
	}
}
