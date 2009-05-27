package org.mss.net.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.mss.types.Commands;

public class ThreadSM implements Runnable {
	private PrintWriter send = null;
	private BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
	boolean streamOpen = true;

	public void run() {
		do {
			String message = "";

			try {
				if ((this.read.ready())) {
				message = this.read.readLine();
					synchronized (this.send) {
						this.send.write(Commands.BC_MESSAGE);
						this.send.println(message);
						this.send.flush();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				this.streamOpen = false;
			}
			try {
				Thread.sleep(500);
			} catch (java.lang.InterruptedException e1) {
			}
		} while (this.streamOpen);
	}

	ThreadSM(PrintWriter send) {
		this.send = send;
	}

	public void close() {
		this.streamOpen = false;
	}
}