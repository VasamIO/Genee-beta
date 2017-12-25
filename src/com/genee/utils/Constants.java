package com.genee.utils;

public class Constants {

	public static final String URL_GOOGLE_DUPLEX_SPEECH_API_V1 = "https://www.google.com/speech-api/full-duplex/v1/";
	public static final String URL_GOOGLE_DOWN_SPEECH_API_V1 = URL_GOOGLE_DUPLEX_SPEECH_API_V1 + "down?maxresults=1&pair=%s";
	public static final String URL_GOOGLE_UP_SPEECH_API_V1 = URL_GOOGLE_DUPLEX_SPEECH_API_V1 + "up?lang=%s&lm=dictation&client=chromium&pair=%s&key=%s&continuous=true&interim=true";

	public static final String LABEL_RESULT = "result";
	public static final String LABEL_CONFIDENCE = "confidence";
	public static final String LABEL_TRANSCRIPT = "transcript";
	public static final String LABEL_FINAL = "final";
	public static final String LABEL_GET = "GET";
	public static final String LABEL_POST = "POST";
	public static final String TYPE_UTF = "UTF-8";
	
	public static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 15; //15 sec 

}
