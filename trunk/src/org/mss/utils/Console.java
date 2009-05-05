package org.mss.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Console {
	private static BufferedReader cons = new BufferedReader(new InputStreamReader(System.in));
	
	public static String read(String def) {
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
	
	public static int read(int def) {
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
}