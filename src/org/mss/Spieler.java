package org.mss;

public class Spieler {
	private String name = "Spieler"+Math.round(Math.random()*1000);
	private boolean comp = true;
	
	public Spieler() {
	}

	public Spieler(String name) {
		this.name = (name.contentEquals(""))? this.name: name;
		this.comp = false;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isComp() {
		return comp;
	}

	public void setComp(boolean comp) {
		this.comp = comp;
	}
	public String toString() {
		return name + ((comp)? "(C)":"");
	}
}