package com.genee.exception;

public class InvalidResponse extends Exception{
	
	private static final long serialVersionUID = 1L;

	public InvalidResponse(int responseCode, String msg) {
		super("Error code:" + String.valueOf(responseCode) + ":" + msg);
	}

}
