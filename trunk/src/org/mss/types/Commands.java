package org.mss.types;

/* 
 * Klasse enthält die Befehlscode für die Client-Server Kommunikation
 */
public final class Commands {
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
	public static final int USER_KICK = 0xc2;
	public static final int USER_BAN = 0xc3;
	public static final int USER_SELF = 0xc4;
	public static final int USER_OTHER = 0xc5;
}
