package org.mss.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/*
 * Abfragefenster Terminiert wenn beendet
 */
public class QueryWinDouble {
	JTextField input1 = new JTextField();
	JTextField input2 = new JTextField();
	JButton submit = new JButton();
	JLabel label1 = new JLabel();
	JLabel label2 = new JLabel();
	JFrame window = new JFrame();
	boolean canceled = false;
	Object nop = new Object();

	public void setAlwaysOnTop(boolean val) {
		window.setAlwaysOnTop(val);
	}

	public boolean getAlwaysOnTop() {
		return window.isAlwaysOnTop();
	}

	public QueryWinDouble() {
		this("", "", "", "", "", "", new Dimension(400, 100));
	}

	public QueryWinDouble(String windowTitle, String label1Text, String label2Text, String buttonText, String input1Default, String input2Default, Dimension dim) {
		// Erzeugen des Abfragefensters
		window.setSize(dim);
		window.setResizable(false);
		window.setLayout(new BorderLayout(5, 5));
		GridBagLayout gbl = new GridBagLayout();
		window.setLayout(gbl);

		window.setTitle(windowTitle);
		// Eingabefeld erzeugen
		input1.setPreferredSize(new Dimension(Math.round(dim.width * .7F), dim.height / 4));
		setInput1Text(input1Default);

		input2.setPreferredSize(new Dimension(Math.round(dim.width * .7F), dim.height / 4));
		setInput2Text(input2Default);

		// Abschickbutton
		submit.setPreferredSize(new Dimension(Math.round(dim.width * .3F), dim.height));
		setButtonText(buttonText);

		// Nachrichtenanzeige
		label1.setPreferredSize(new Dimension(Math.round(dim.width * .7F), dim.height / 4));
		setLabel1Text(label1Text);

		label2.setPreferredSize(new Dimension(Math.round(dim.width * .7F), dim.height / 4));
		setLabel2Text(label2Text);

		// Elemente hinzufügen zum Fenster
		addComponent(window, gbl, label1, 0, 0, 1, 1, 0.7, 1/4);
		addComponent(window, gbl, input1, 0, 1, 1, 1, 0.7, 1/4);
		addComponent(window, gbl, label2, 0, 2, 1, 1, 0.7, 1/4);
		addComponent(window, gbl, input2, 0, 3, 1, 1, 0.7, 1/4);
		addComponent(window, gbl, submit, 2, 0, 1, 4, 0.3, 1);
		
		window.setLocationRelativeTo(null);
	}

	// Getter und Setter
	public void setLabel1Text(String text) {
		label1.setText(text);
	}

	public String getLabel1Text() {
		return label1.getText();
	}

	public void setLabel2Text(String text) {
		label2.setText(text);
	}

	public String getLabel2Text() {
		return label2.getText();
	}

	public void setButtonText(String text) {
		submit.setText(text);
	}

	public String getButtonText() {
		return submit.getText();
	}

	public void setInput1Text(String text) {
		input1.setText(text);
	}

	public String getInput1Text() {
		return input1.getText();
	}

	public void setInput2Text(String text) {
		input2.setText(text);
	}

	public String getInput2Text() {
		return input2.getText();
	}

	public void setWindowDim(Dimension dim) {
		window.setSize(dim);
	}

	public Dimension getWindowDim() {
		return window.getSize();
	}

	public boolean isCanceled() {
		return canceled;
	}

	// Anzeigen des Fensters mit anschließender Terminierung
	public void show() {

		// Registrieren des Close Buttons
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeWindow(true);
			}
		});
		// Registrieren des Abschickenbuttons
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow(false);
			}
		});

		// Registrieren des EnterEvents beim Eingabefeld
		input1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow(false);
			}
		});

		input2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow(false);
			}
		});


		window.pack();
		window.setVisible(true);
		input1.requestFocus();
		synchronized (nop) {
			try {
				nop.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		window.dispose();// Resourcen freigeben
	}

	private void closeWindow(boolean canceled) {
		synchronized (nop) {
			this.canceled = canceled;
			nop.notifyAll();
		}
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
}
