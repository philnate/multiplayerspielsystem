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

public class NoticeWin {
	JLabel label = new JLabel();
	JFrame window = new JFrame();
	JButton accept = new JButton("Ok");
	Object nop = new Object();

	public NoticeWin() {
		this("","", new Dimension(300,100));
	}
	
	public NoticeWin(String title, String message, Dimension dim) {
		label.setText(message);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		window.setTitle(title);
		window.setResizable(false);
		window.setLayout(new BorderLayout(1,1));
		window.add(label,BorderLayout.NORTH);
		window.add(accept, BorderLayout.SOUTH);
		window.setPreferredSize(dim);
		window.setLocationRelativeTo(null);
		label.setPreferredSize(new Dimension(dim.width,Math.round(dim.height*.8F)));
		accept.setPreferredSize(new Dimension(dim.width, Math.round(dim.height*0.2F)));
	}
	
	public void show() {
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				synchronized (nop) {
					nop.notifyAll();
				}
			}
		});
		
		accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				synchronized (nop) {
					nop.notifyAll();
				}
			}
		});
		
		window.pack();
		window.setVisible(true);
		accept.requestFocus();

		synchronized(nop) {
			try {
				nop.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		window.dispose();
	}
	
}
