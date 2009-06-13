package org.mss.windows;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;

import org.mss.net.server.SharedClientInfo;
import org.mss.types.Commands;

public class MainWin {
	SharedClientInfo sci = SharedClientInfo.getInstance();
	JFrame window = new JFrame("MSS - Lobby");
	JButton submit = new JButton("Senden");
	JTextField input = new JTextField();
	JEditorPane messages = new JEditorPane();
	JPopupMenu popup = new JPopupMenu("Benutzeraktion");
	JMenuItem mIKick = new JMenuItem("User kicken");
	JMenuItem mIWarn = new JMenuItem("User verwarnen");
	JMenuItem mIBan = new JMenuItem("User bannen");
	JList userlist = new JList(sci.getSiblingsArray());	
	
	String htmlStart = "<html><body>";
	String htmlEnd = "</html></body>";
	String chatLines = "";
	
	public final int COLOR_NOTE = 0x1;
	public final int COLOR_NORMAL = 0x2;
	public final int COLOR_IMPORTANT = 0x3;
	public final int COLOR_SELF = 0x4;
	private int typeOfPunishment = 0;

	public MainWin() {
		//Allgemeine Einstellungen für das Fenster
		window.setResizable(true);
		window.setPreferredSize(new Dimension(800,700));
		window.setSize(window.getPreferredSize());
		window.setLocationRelativeTo(null);
		//Layout setzen
		GridBagLayout gbl = new GridBagLayout();
		window.setLayout(gbl);
		
		messages.setEditable(false);
		messages.setEditorKit(new HTMLEditorKit());
		
		addComponent(window, gbl, new JScrollPane(messages), 0,0, 10, 10, 9,8);
		addComponent(window, gbl, input, 0, 11, 1, 10, 9, 2);
		addComponent(window, gbl, new JScrollPane(userlist),11,0,1,10,.1,10);
		addComponent(window, gbl, submit, 11,11,1,1,0.5,0.5);

		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		
		userlist.addMouseListener(new MouseAdapter() {
			private void displayPopup(MouseEvent e) {
	        	 popup.add(mIWarn);
	        	 popup.add(mIKick);
	        	 popup.add(mIBan);
	        	 popup.pack();
	        	 popup.setLocation(e.getLocationOnScreen());
	        	 popup.setVisible(true);
			}
			
			public void mousePressed(MouseEvent e) {
		         if (e.isPopupTrigger()){
		        	 displayPopup(e);
		         }
		    }
			
			public void mouseReleased(MouseEvent e) {
		         if (e.isPopupTrigger()){
		        	 displayPopup(e);
		         }
		    }
		});
		
		mIWarn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendAllSelected(Commands.USER_WARN);
			}
		});
		
		mIKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendAllSelected(Commands.USER_KICK);
			}
		});
		
		mIBan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendAllSelected(Commands.USER_BAN);
			}
		});
	}

	private void sendMessage() {
		//Nachricht abschicken
		if (input.getText().contentEquals("")) return;
		sci.notifyOthers(Commands.BC_MESSAGE, input.getText(), null);
		addMessage("Admin:"+input.getText(), COLOR_SELF);
		input.setText("");		
	}

	private void sendAllSelected(int type) {
		typeOfPunishment = type;
		Thread getReason = new Thread(new Runnable() {
			public void run() {
				QueryWin query = new QueryWin("Grund","Bitte Grund angeben!", "Ok", "Du weist warum", new Dimension(400,100));
				query.show();	
				if (!query.canceled) {
					String reason = query.getQueryAnswer();
					synchronized(sci) {
						int[] users = userlist.getSelectedIndices();
						for (int i = 0; i < users.length; i++) {
							sci.notifyOthers(typeOfPunishment, reason, sci.getSiblings().get(users[i]));
							if (typeOfPunishment != Commands.USER_WARN) {
								if (typeOfPunishment == Commands.USER_BAN) {
									sci.addUser(sci.getSiblings().get(users[i]).username,true);
								}
								sci.getSiblings().remove(users[i]).close();
								
							}
						}
						refreshUserlist();
					}
				}
				window.setEnabled(true);
				window.toFront();
			}
		});
		getReason.start();
		window.setEnabled(false);
		popup.setVisible(false);
	}

	public void show() {
		window.setVisible(true);
	}
	
	static void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y,
			int width, int height, double weightx, double weighty ) { 
		
		GridBagConstraints gbc = new GridBagConstraints(); 
		gbc.fill = GridBagConstraints.BOTH; 
		gbc.gridx = x; 
		gbc.gridy = y; 
		gbc.gridwidth = width; 
		gbc.gridheight = height; 
		gbc.weightx = weightx; 
		gbc.weighty = weighty; 
		gbl.setConstraints( c, gbc ); 
		cont.add( c ); 
	}
	public void refreshUserlist() {
		synchronized(sci.getSiblings()) {
			userlist.setListData(sci.getSiblingsArray());
		}
	}
	
	public void addMessage(String message, int color) {
		String colorcode = "#";
		switch (color) {
		case COLOR_IMPORTANT: colorcode += "ff0000";
			break;
		case COLOR_NOTE: colorcode += "aaaaaa";
			break;
		case COLOR_SELF: colorcode += "0000ff";
			break;
		case COLOR_NORMAL:
		default:colorcode += "000000";
		}
		synchronized(messages) {
			chatLines += "<font color='"+ colorcode + "'>" + message + "</font><br />";
			messages.setText(htmlStart + chatLines + htmlEnd);
		}
	}
}
