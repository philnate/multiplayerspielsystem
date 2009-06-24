package org.mss.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
public class QueryWin {
	JTextField input = new JTextField();
	JButton submit = new JButton();
	JLabel label = new JLabel();
	JFrame window = new JFrame();
	String queryAnswer = "";
	boolean canceled = false;
	Object nop = new Object();

	public void setAlwaysOnTop(boolean val) {
		window.setAlwaysOnTop(val);
	}
	
	public boolean getAlwaysOnTop() {
		return window.isAlwaysOnTop();
	}

	public QueryWin() {
		this("", "", "", "", new Dimension(400, 100));
	}

	public QueryWin(String windowTitle, String labelText, String buttonText, String inputDefault, Dimension dim) {
		// Erzeugen des Abfragefensters
		window.setSize(dim);
		window.setResizable(false);
		window.setLayout(new BorderLayout(5, 5));
		window.setTitle(windowTitle);
		// Eingabefeld erzeugen
		input.setPreferredSize(new Dimension(250, 30));
		setInputText(inputDefault);
		
		// Abschickbutton
		submit.setPreferredSize(new Dimension(150, 30));
		setButtonText(buttonText);
		
		// Nachrichtenanzeige
		label.setPreferredSize(new Dimension(400, 20));
		setLabelText(labelText);
		
		//Elemente hinzufügen zum Fenster
		window.add(input, BorderLayout.WEST);
		window.add(submit, BorderLayout.CENTER);
		window.add(label, BorderLayout.NORTH);
		window.setLocationRelativeTo(null);
	}

	//Getter und Setter
	public void setLabelText(String text) {
		label.setText(text);
	}

	public String getLabelText() {
		return label.getText();
	}

	public void setButtonText(String text) {
		submit.setText(text);
	}

	public String getButtonText() {
		return submit.getText();
	}

	public void setInputText(String text) {
		input.setText(text);
	}

	public String getInputText() {
		return input.getText();
	}

	public void setWindowDim(Dimension dim) {
		window.setSize(dim);
	}

	public Dimension getWindowDim() {
		return window.getSize();
	}

	public String getQueryAnswer() {
		return input.getText();
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	//Anzeigen des Fensters mit anschließender Terminierung 
	public void show() {
		
		//Registrieren des Close Buttons
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

		//Registrieren des EnterEvents beim Eingabefeld
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow(false);
			}
		});
		window.pack();
		window.setVisible(true);
		synchronized(nop) {
			try {
				nop.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		window.dispose();//Resourcen freigeben
	}

	private void closeWindow(boolean canceled) {
		synchronized(nop) {
			this.canceled = canceled;
			nop.notifyAll();
		}
	}
}
