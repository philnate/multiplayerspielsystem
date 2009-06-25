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
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.html.HTMLEditorKit;

import org.mss.types.Commands;

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

	PrintWriter send;
	Vector<String> users = new Vector<String>();
	String username = "";
	String htmlStart = "<html><body>";
	String htmlEnd = "</html></body>";
	String chatLines = "";

	public final int COLOR_NOTE = 0x1;
	public final int COLOR_NORMAL = 0x2;
	public final int COLOR_IMPORTANT = 0x3;
	public final int COLOR_SELF = 0x4;

	public void addUser(String user) {
		synchronized (users) {
			Iterator<String> it = users.iterator();
			while (it.hasNext()) {
				if (it.next().contentEquals(user)) {
					return;// Benutzer steht weshalb auch immer bereits in
							// Liste;
				}
			}
			users.add(user);
			userlist.setListData(users);
		}
	}

	public void removeUser(String user) {
		synchronized (users) {
			Iterator<String> it = users.iterator();
			while (it.hasNext()) {
				String curUser = it.next();
				if (curUser.contentEquals(user)) {
					it = null;
					users.remove(curUser);
					break;
				}
			}
			userlist.setListData(users);
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ClientMainWin(PrintWriter send) {
		this.send = send;
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		Container c = this.getContentPane();
		GridBagLayout gbl = new GridBagLayout();
		c.setLayout(gbl);

		// User-Feld
		setComp(c, gbl, new JScrollPane(userlist), 0, 0, 4, 1);
		userlist.setCellRenderer(new HashColorCellRenderer());
		
		// Ausgabe-Feld
		messages.setEditable(false);
		messages.setEditorKit(new HTMLEditorKit());
		setComp(c, gbl, new JScrollPane(messages), 1, 0, 3, 1);

		// Eingabe-Feld
		setComp(c, gbl, new JScrollPane(txtSend), 1, 3, 1, 1);
		txtSend.addKeyListener(this);

		// Close-Button
		setComp(c, gbl, bClose, 2, 0, 1, 1);
		bClose.addActionListener(this);

		// Send-Button
		setComp(c, gbl, bSend, 2, 3, 1, 1);
		bSend.addActionListener(this);
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
		this.pack();
	}

	void setComp(Container c, GridBagLayout gbl, Component comp, int x, int y, int h, int w) {

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

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void send() {
		synchronized (send) {
			addMessage(username +":"+ txtSend.getText());
			this.send.write(Commands.BC_MESSAGE);
			String[] lines = txtSend.getText().split("\n");
			this.send.write(lines.length);
			for (int i = 0; i < lines.length; i++) {
				send.println(lines[i]);
			}
			send.flush();
		}
		txtSend.setText("");
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
