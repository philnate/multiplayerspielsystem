package org.mss.types;

import java.io.Serializable;

import org.mss.Spieler;

public class MSSDataObject implements Serializable {
	
	public static final int SND_LOGIN = 0x01;
	public static final int LOGIN_FAILED = 0xa1;
	public static final int LOGIN_ALREADY_ON = 0xa2;
	public static final int LOGIN_SUCCESS = 0xa4;
	public static final int LOGIN_PASSWRONG = 0xa5;
	public static final int LOGIN_BAN = 0xa6;
	public static final int BC_NEWUSER = 0xf2;
	public static final int BC_USEROFF = 0xf3;
	public static final int BC_MESSAGE = 0xf1;
	public static final int USERLIST = 0xb1;
	public static final int ERROR_UNKOWN = 0xb2;
	public static final int ACTION_FORBIDDEN = 0x29a;
	public static final int USER_WARN = 0xc1;
	public static final int USER_KICK = 0xc3;
	public static final int USER_BAN = 0xc5;
	public static final int GAME_REQUEST = 0xd1;
	public static final int GAME_ANSWER = 0xd2;
	public static final int GAME_TURN = 0xd3;
	public static final int GAME_CLOSED = 0xd4;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6528868916918817836L;
	private int type;
	private String additionalData;
	private Spieler[] toUser;
	private Object data;
	private Spieler fromUser;

	public MSSDataObject(int type, Object data, Spieler[] toUser, Spieler fromUser, String additionalData) {
		this.type = type;
		this.toUser = toUser;
		this.data = data;
		this.fromUser = fromUser;
		this.additionalData = additionalData;
	}
	
	public MSSDataObject(int type, Object data, Spieler[] toUser, Spieler fromUser) {
		this(type, data, toUser, fromUser, "");
	}

	public MSSDataObject(int type, Object data) {
		this(type, data, null, null);
	}
	
	public MSSDataObject(int type) {
		this(type, null, null, null);
	}

	public MSSDataObject(int type, Object data, Spieler fromUser) {
		this(type, data, null, fromUser);
	}

	public int getType() {
		return type;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public Spieler[] getToUser() {
		return toUser;
	}

	public Object getData() {
		return data;
	}
	
	public Spieler getFromUser() {
		return fromUser;
	}
}
