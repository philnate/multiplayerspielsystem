package org.mss.utils.listener;

import java.util.EventObject;
import org.mss.types.Zug;

public class FieldClickedListener extends EventObject {
	private Zug zug = null;
	
	public FieldClickedListener(Zug zug) {
		super(null);
		this.zug = zug;
	}
	
	public Zug getZug() {
		return zug;
	}
}
