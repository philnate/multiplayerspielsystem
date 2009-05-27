package org.mss.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import org.mss.utils.Console;

public class Connector extends Thread
{
  private ServerSocket listener = null;

  public void run()
  {
    Console.log("Warte auf neuen Teilnehmer!");
    try
    {
      while (true) {
      ClientThread client = new ClientThread(this.listener.accept());
      Thread clientThread = new Thread(client);
      clientThread.start();
      }
    } catch (IOException e) {
      Console.log("Fehler bei Verbindungsaufnahme mit Client!");
      e.printStackTrace();
    }
  }

  Connector(ServerSocket listener)
  {
    this.listener = listener;
  }
}