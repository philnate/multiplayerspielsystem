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
import javax.swing.SwingConstants;

public class QueryWinYesNo {
	JButton yes = new JButton("Ja");
	JButton no = new JButton("Nein");
	JLabel label = new JLabel();
	JFrame window = new JFrame();
	Object nop = new Object();
	boolean answer;
	
	public QueryWinYesNo() {
		this("Frage","Wie wollen Sie verfahren:", new Dimension(300,100));
	}
	
	public QueryWinYesNo(String windowTitle, String labelText, Dimension dim) {
		window.setSize(dim);
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		window.setTitle(windowTitle);
		window.setLayout(new BorderLayout(5,5));
		window.add(label, BorderLayout.NORTH);
		window.add(yes, BorderLayout.WEST);
		window.add(no, BorderLayout.EAST);
		
		label.setText(labelText);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(dim.width,dim.height/2));
		yes.setPreferredSize(new Dimension(dim.width/2, dim.height/4));
		no.setPreferredSize(new Dimension(dim.width/2,dim.height/4));
	}
	
	public void show() {
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setAnswer(false);
			}
		});
		
		yes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setAnswer(true);
			}
		});
		
		no.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setAnswer(false);
			}
		});
		
		window.pack();
		window.setVisible(true);
		yes.requestFocus();

		synchronized (nop) {
			try {
				nop.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		window.dispose();
	}
	
	private void setAnswer(boolean bool) {
		synchronized(nop) {
			answer = bool;
			nop.notifyAll();
		}
	}

	public boolean getAnswer() {
		return answer;
	}
}
