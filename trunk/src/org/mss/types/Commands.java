package org.mss.types;

/* Holds all constants which represent the communication set of Server and client
 * @since MS2
 */
public final class Commands {
	public static final int SND_LOGIN = 0x01; 
	public static final int LOGIN_FAILED = 0xa1;
	public static final int LOGIN_ALREADY_ON = 0xa2;
	public static final int LOGIN_SUCCESS = 0xa4;
	public static final int LOGIN_PASSWRONG = 0xa5; 
	public static final int BC_NEWUSER = 0x02;
	public static final int BC_USEROFF = 0x03;
	public static final int BC_MESSAGE = 0xf1;
	public static final int USERLIST = 0xf2;
	public static final int ERROR_UNKOWN = 0xff;
}
