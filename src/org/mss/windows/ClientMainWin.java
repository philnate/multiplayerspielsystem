package org.mss.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.text.html.HTMLEditorKit;

import org.mss.Spiel;
import org.mss.Spieler;
import org.mss.types.MSSDataObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClientMainWin extends JFrame implements KeyListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5466899701556007438L;
	JTextArea txtSend = new JTextArea(5, 30);
	JList userlist = new JList();
	JButton bSend = new JButton("Senden");
	JButton bClose = new JButton("Schlieﬂen");
	JEditorPane messages = new JEditorPane();
	JPopupMenu menu = new JPopupMenu();
	JMenu fourwins = new JMenu("Viergewinnt");
	JMenu chomp = new JMenu("Chomp");
	JMenuItem f_offline = new JMenuItem("Gegen PC");
	JMenuItem f_online = new JMenuItem("Gegen Mensch");
	JMenuItem c_offline = new JMenuItem("Gegen PC");
	JMenuItem c_online = new JMenuItem("Gegen Mensch");	
	Spiel curGame = null;

	ObjectOutputStream send;
	Vector<String> users = new Vector<String>();
	String htmlStart = "<html><body>";
	String htmlEnd = "</html></body>";
	String chatLines = "";
	
	public final int COLOR_NOTE = 0x1;
	public final int COLOR_NORMAL = 0x2;
	public final int COLOR_IMPORTANT = 0x3;
	public final int COLOR_SELF = 0x4;
	private Spieler spieler;

	public ClientMainWin(ObjectOutputStream send, Spiel curGame) {
		this.setTitle("MSS Client - Lobby");
		this.send = send;
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.curGame = curGame;
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		// User-Feld
		setComp(this, gbl, new JScrollPane(userlist), 0, 0, 4, 1);
		userlist.setCellRenderer(new HashColorCellRenderer());
		userlist.setComponentPopupMenu(menu);
		userlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		menu.add(fourwins);
		menu.add(chomp);
		fourwins.add(f_offline);
		fourwins.add(f_online);
		chomp.add(c_offline);
		chomp.add(c_online);

		// Ausgabe-Feld
		messages.setEditable(false);
		messages.setEditorKit(new HTMLEditorKit());
		setComp(this, gbl, new JScrollPane(messages), 1, 0, 3, 1);

		// Eingabe-Feld
		setComp(this, gbl, new JScrollPane(txtSend), 1, 3, 1, 1);
		txtSend.addKeyListener(this);

		// Close-Button
		setComp(this, gbl, bClose, 2, 0, 1, 1);
		bClose.addActionListener(this);

		// Send-Button
		setComp(this, gbl, bSend, 2, 3, 1, 1);
		bSend.addActionListener(this);
		
		//Fenster schlieﬂen
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		txtSend.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					txtSend.setText(txtSend.getText() + "\n");
					return;
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
					send();
				}
			}
		});
		//Viergewinnt gegen PC
		f_offline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getSource().toString());
			}
		});
		//Viergewinnt gegen Mensch
		f_online.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendGameRequest("Viergewinnt");
			}
		});
		//Chomp gegen PC
		c_offline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getSource().toString());
			}
		});
		//Chomp gegen Mensch
		c_online.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendGameRequest("Chomp");
			}
		});
		this.pack();
	}

	private void sendGameRequest(String game) {
		synchronized (send) {
			try {
				if (userlist.getSelectedValue() == null || curGame != null || userlist.getSelectedValue() == spieler) return;
				Spieler[] enemy = new Spieler[1];
				enemy[0] = (Spieler) userlist.getSelectedValue();
				send.writeObject(new MSSDataObject(MSSDataObject.GAME_REQUEST, game, enemy, spieler));
				send.flush();
				//TODO Anzeige das Anfrage aussteht...auch abbrechbar...
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setComp(Container c, GridBagLayout gbl, Component comp, int x, int y, int h, int w) {

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridheight = h;
		gbc.gridwidth = w;
		gbc.weightx = (double) w;
		gbc.weighty = (double) h;
		gbl.setConstraints(comp, gbc);
		c.add(comp, gbc);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bClose) {
			dispose();
			System.exit(0);
		}
		if (e.getSource() == bSend) {
			send();
		}
	}
	public void refreshUsers(ArrayList<Spieler> users) {
		Collections.sort(users, new Comparator<Spieler>(){
		public int compare(Spieler s1, Spieler s2) {
			return s1.getName().compareTo(s2.getName());
		}
	});
		userlist.setListData(users.toArray());
	}
	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void send() {
		synchronized (send) {
			addMessage(spieler.getName() + ":" + txtSend.getText());
			try {
				send.writeObject(new MSSDataObject(MSSDataObject.BC_MESSAGE, txtSend.getText()));
				send.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		txtSend.setText("");
	}

	public void setSpieler(Spieler spieler) {
		this.spieler = spieler;
	}

	public synchronized void addMessage(String text) {
		Color col;
		if (text.indexOf(":") == -1) {
			col = Color.gray;
		} else {
			col = new Color(text.substring(0, text.indexOf(":")).hashCode());
		}
		chatLines += "<font color='#" + Integer.toHexString(col.getRGB()).substring(2) + "'>"
					+text.replace("\n", "<br />") +"<br /></font>";
		messages.setText(htmlStart+chatLines+htmlEnd );
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

}
