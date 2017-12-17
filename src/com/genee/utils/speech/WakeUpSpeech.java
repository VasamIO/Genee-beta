package com.genee.utils.speech;

public interface WakeUpSpeech {

	public void startListening() throws Exception;

	public String getResults();

	public void stopListening();

}
