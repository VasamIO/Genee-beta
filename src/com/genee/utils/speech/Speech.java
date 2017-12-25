package com.genee.utils.speech;

public interface Speech {
	
	public void startListening() throws Exception;

	public void stopListening();
	
	public void listen(SpeechListener listener);

}
