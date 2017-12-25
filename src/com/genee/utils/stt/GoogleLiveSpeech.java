package com.genee.utils.stt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.json.JSONObject;

import com.genee.exception.InvalidResponse;
import com.genee.utils.Constants;
import com.genee.utils.http.HttpUtility;
import com.genee.utils.io.Microphone;
import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;
import com.genee.utils.speech.Speech;
import com.genee.utils.speech.SpeechData;
import com.genee.utils.speech.SpeechListener;

import net.sourceforge.javaflacencoder.FLACFileWriter;

public class GoogleLiveSpeech implements Speech {

	private SpeechListener speechListener;
	private final String API_KEY;
	private AudioInputStream ais;
	final Microphone mic;
	private String language;
	private static final long MIN = 10000000;
	private static final long MAX = 900000009999999L;
	private static ThreadGroup eventPool = new ThreadGroup("GoogleLiveSpeech");

	public GoogleLiveSpeech(String API_KEY, String language) {
		this.language = language;
		this.API_KEY = API_KEY;
		mic = new Microphone(FLACFileWriter.FLAC);
	}

	@Override
	public void startListening() throws Exception {
		final long PAIR = MIN + (long) (Math.random() * ((MAX - MIN) + 1L));
		final String API_DOWN_URL = String.format(Constants.URL_GOOGLE_DOWN_SPEECH_API_V1, PAIR);
		final String API_UP_URL = String.format(Constants.URL_GOOGLE_UP_SPEECH_API_V1, language, PAIR, API_KEY);
		Thread downChannel = this.downChannel(API_DOWN_URL);
		Thread upChannel = this.upChannel(API_UP_URL, mic.getTargetDataLine(), mic.getAudioFormat());
		try {
			downChannel.join();
			upChannel.interrupt();
			upChannel.join();
		} catch (InterruptedException e) {
			downChannel.interrupt();
			downChannel.join();
			upChannel.interrupt();
			upChannel.join();
		}
	}

	@Override
	public void stopListening() {
		if (ais != null)
			try {
				ais.close();
			} catch (Exception ex) {
				Logger.log(TYPE.SERVERE, "GoogleLiveSpeech stopListening, " + ex.getMessage());
			}
	}

	@Override
	public void listen(SpeechListener speechListener) {
		this.speechListener = speechListener;
	}

	private Thread downChannel(final String url) {
		Thread downChannelThread = new Thread(eventPool, "Downstream Thread") {
			public void run() {
				Scanner inStream;
				try {
					inStream = HttpUtility.getHTTPSConnection(url);
					if (inStream == null) {
						Logger.log(TYPE.SERVERE, "NoInputStream while downChannel");
						//return;
					}
					String line;
					SpeechData speechData = new SpeechData();
					while (inStream.hasNext() && (line = inStream.nextLine()) != null) {
						if (line != null && line.length() > 17) {// Prevents blank responses from Firing
							parseResponse(line, speechData);
						}
					}
					inStream.close();
					if (speechListener != null) {
						speechListener.onResponse(speechData);
					}
				} catch (InvalidResponse e) {
					Logger.log(TYPE.SERVERE, "InvalidResponse while downChannel, " + e.getMessage());
				}
			}
		};
		downChannelThread.start();
		return downChannelThread;
	}

	private Thread upChannel(final String url, final TargetDataLine targetDataLine, final AudioFormat audioFormat)
			throws LineUnavailableException {
		if (!targetDataLine.isOpen()) {
			targetDataLine.open(audioFormat);
			targetDataLine.start();
		}
		Thread upChannelThread = new Thread(eventPool, "Upstream Thread") {
			public void run() {
				upChannelIntrl(url, targetDataLine, (int) audioFormat.getSampleRate());
			}
		};
		upChannelThread.start();
		return upChannelThread;

	}

	private void upChannelIntrl(String url, TargetDataLine mtl, int sampleRate) {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("Transfer-Encoding", "chunked");
			map.put("Content-Type", "audio/x-flac; rate=" + sampleRate);
			HttpsURLConnection httpConn = HttpUtility.postHTTPSConnection(url, map);
			final OutputStream out = httpConn.getOutputStream();
			// Note : if the audio is more than 15 seconds
			// don't write it to UrlConnInputStream all in one block as this sample does.
			// Rather, segment the byteArray and on intermittently, sleeping thread
			// supply bytes to the urlConn Stream at a rate that approaches
			// the bitrate ( =30K per sec. in this instance ).
			Logger.log(TYPE.DEBUG, "GoogleLiveSpeech upChannel: Starting to write data to output...");
			ais = new AudioInputStream(mtl);
			AudioSystem.write(ais, FLACFileWriter.FLAC, out);
			Logger.log(TYPE.DEBUG, "GoogleLiveSpeech upChannel: Upstream Closed...");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void parseResponse(String line, SpeechData speechData) {
		JSONObject response = new JSONObject(line);
		SpeechData.Chunk chunk = speechData.createChunk();
		if (response.has(Constants.LABEL_CONFIDENCE)) {
			chunk.setConfidence(String.valueOf(response.get(Constants.LABEL_CONFIDENCE)));
		}
		if (response.has(Constants.LABEL_TRANSCRIPT)) {
			chunk.setTranscript((String) response.get(Constants.LABEL_TRANSCRIPT));
		}
		if (response.has(Constants.LABEL_FINAL) && ((boolean) response.get(Constants.LABEL_FINAL))) {
			chunk.setFinalResponse(true);
		}
	}

	public static void main(String[] args) throws Exception {
		String API_KEY = "AIzaSyBfWZ1_TUK2-g0S3DyX_j1k6LDwwnnxdGw";
		String language = "en-IN";
		GoogleLiveSpeech googleLiveSpeech = new GoogleLiveSpeech(API_KEY, language);
		googleLiveSpeech.startListening();
		googleLiveSpeech.listen(new SpeechListener() {
			@Override
			public void onResponse(SpeechData speechData) {
				System.out.println(speechData);
			}
		});
	}

}
