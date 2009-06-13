package org.mss.net.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.mss.types.Commands;

/*
 * Thread ThreadSM kümmert sich um die Versendung der Nachrichten an die anderen Teilnehmer
 */
public class ThreadSM implements Runnable {
	private PrintWriter send = null;
	private BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
	boolean streamOpen = true;

	public void run() {
		//Solange der Thread sich nicht beenden soll
		do {
			String message = "";
			try {
				//Wenn eine Nachricht bereit liegt diese Abgreifen und an den Server zum Verteilen schicken
				if ((this.read.ready())) {
					message = this.read.readLine();
					synchronized (this.send) {
						this.send.write(Commands.BC_MESSAGE);
						this.send.write(message.split("\n").length);
						this.send.println(message);
						this.send.flush();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				this.streamOpen = false;
			}
			try {
				//Schlafen legen und später erneut prüfen ob Eingabe vorliegt 
				Thread.sleep(500);
			} catch (java.lang.InterruptedException e1) {
			}
		} while (this.streamOpen);
	}

	ThreadSM(PrintWriter send) {
		this.send = send;
	}

	/*
	 * Thread soll sich beenden also das "Close" Flag setzen
	 */
	public void close() {
		this.streamOpen = false;
	}
}