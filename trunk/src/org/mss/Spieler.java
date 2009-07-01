package org.mss;

import java.io.Serializable;

public class Spieler implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 217396186956531544L;
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