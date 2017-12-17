package com.genee.utils.speech;

import java.io.IOException;
import java.util.logging.Logger;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

public class SphnixLiveSpeech implements WakeUpSpeech {

	LiveSpeechRecognizer recognizer;
	Configuration configuration;

	public SphnixLiveSpeech() throws IOException {
		Logger cmRootLogger = Logger.getLogger("default.config");
		cmRootLogger.setLevel(java.util.logging.Level.OFF);
		String conFile = System.getProperty("java.util.logging.config.file");
		if (conFile == null) {
			System.setProperty("java.util.logging.config.file", "ignoreAllSphinx4LoggingOutput");
		}
		configuration = new Configuration();
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setGrammarPath("file:src");
		configuration.setGrammarName("hello");
		configuration.setUseGrammar(true);

	}

	@Override
	public void startListening() throws Exception {
		recognizer = new LiveSpeechRecognizer(configuration);
		recognizer.startRecognition(false);
	}

	@Override
	public String getResults() {
		return recognizer.getResult().getResult().toString();
	}

	@Override
	public void stopListening() {
		recognizer.stopRecognition();
		recognizer = null;
	}

}
