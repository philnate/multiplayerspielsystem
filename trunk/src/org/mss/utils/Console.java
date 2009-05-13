package org.mss.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Console {
	private static BufferedReader cons = new BufferedReader(new InputStreamReader(System.in));
	private static boolean debug = true;
	
	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Console.debug = debug;
	}

	public static String read(String def) {
		return read("", def);
	}

	public static String read(String description, String def) {
		if (description != "") {
			System.out.print(description);
			if (def != "") {
				System.out.println(" (def: " + def + ")?");	
			} else {
				System.out.println(" (leer für default)?");
			}
		}
		String res = "";
		try {
			res = cons.readLine();
			if (res.trim().equalsIgnoreCase("")) {
				res = def;
			}
		} catch (IOException e) {
			res = def;
			e.printStackTrace();
		}
		
		return res;
	}
	
	public static int read(String description, int def) {
		if (description != "") {
			System.out.print(description + " (def: " + def + ")?");
		}
		int res;
		try {
			res =Integer.parseInt(cons.readLine());
		} catch (NumberFormatException e) {
			res = def;
		} catch (Exception e) {
			res = def;
			e.printStackTrace();
		}
		
		return res;
	}
	
	public static int read(int def) {
		return read("", def);
	}
	
	public static void write(String text) {
		System.out.println(text);
	}
	
	public static void debug(String text) {
		if (debug) {
			write("Debug: "+ text);
		}
	}
}