package org.mss;

public abstract class Spielfeld {
	public int height;
	public int width;
	public String[][] feld = null;
	
	public abstract void displayFeld();
	
	public Spielfeld() {
		this(10, 10);
	}
	
	public Spielfeld(int width, int height) {
		this.width = width;
		this.height = height;
		feld = new String[width][height];
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
