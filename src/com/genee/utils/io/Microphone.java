package com.genee.utils.io;

import java.io.Closeable;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import com.genee.event.EventPool;

import net.sourceforge.javaflacencoder.FLACFileWriter;

public class Microphone implements Closeable {

	private TargetDataLine targetDataLine;

	public enum CaptureState {
		PROCESSING_AUDIO, STARTING_CAPTURE, CLOSED;
	}

	CaptureState state;

	private AudioFileFormat.Type fileType;

	private File audioFile;

	public Microphone(AudioFileFormat.Type fileType) {
		setState(CaptureState.CLOSED);
		setFileType(fileType);
		initTargetDataLine();
	}

	public CaptureState getState() {
		return state;
	}

	private void setState(CaptureState state) {
		this.state = state;
	}

	public File getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(File audioFile) {
		this.audioFile = audioFile;
	}

	public AudioFileFormat.Type getFileType() {
		return fileType;
	}

	public void setFileType(AudioFileFormat.Type fileType) {
		this.fileType = fileType;
	}

	public TargetDataLine getTargetDataLine() {
		return targetDataLine;
	}

	public void setTargetDataLine(TargetDataLine targetDataLine) {
		this.targetDataLine = targetDataLine;
	}

	private void initTargetDataLine() {
		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, getAudioFormat());
		try {
			setTargetDataLine((TargetDataLine) AudioSystem.getLine(dataLineInfo));
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		}

	}

	public void captureAudioToFile(File audioFile) {
		setState(CaptureState.STARTING_CAPTURE);
		setAudioFile(audioFile);

		if (getTargetDataLine() == null) {
			initTargetDataLine();
		}
		EventPool.newAsyncProcess("Microphone", "captureAudioToFile", () -> {
			try {
				AudioFileFormat.Type fileTypeX = getFileType();
				File audioFileX = getAudioFile();
				open();
				AudioSystem.write(new AudioInputStream(getTargetDataLine()), fileTypeX, audioFileX);
				// Will write to File until it's closed.
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public void captureAudioToFile(String audioFile) {
		File file = new File(audioFile);
		captureAudioToFile(file);
	}

	public AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		// 8000,11025,16000,22050,44100
		System.out.println(sampleRate);
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 1;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	public void open() {
		if (getTargetDataLine() == null) {
			initTargetDataLine();
		}
		if (!getTargetDataLine().isOpen() && !getTargetDataLine().isRunning() && !getTargetDataLine().isActive()) {
			try {
				setState(CaptureState.PROCESSING_AUDIO);
				getTargetDataLine().open(getAudioFormat());
				getTargetDataLine().start();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return;
			}
		}

	}

	public void close() {
		if (getState() == CaptureState.CLOSED) {
		} else {
			getTargetDataLine().stop();
			getTargetDataLine().close();
			setState(CaptureState.CLOSED);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
//		Microphone microphone = new Microphone(AudioFileFormat.Type.WAVE);
//		microphone.captureAudioToFile("/Users/vasam/Downloads/test123.wav");
		ambientListeningLoop(null);
	}
	
	public static void ambientListeningLoop(String[] args) throws InterruptedException {
		MicrophoneAnalyzer mic = new MicrophoneAnalyzer(FLACFileWriter.FLAC);
		mic.setAudioFile(new File("/Users/vasam/Downloads/test1234.flac"));
		while(true){
			Thread.sleep(1000);
			mic.open();
			final int THRESHOLD = 50;
			int volume = mic.getAudioVolume();
			System.out.println(volume);
			boolean isSpeaking = (volume > THRESHOLD);
			if(isSpeaking){
				try {
					System.out.println("Lestening...");
					mic.captureAudioToFile(mic.getAudioFile());//Saves audio to file.
					do{
						System.out.println("Here" +mic.getAudioVolume());
						Thread.sleep(2000);//Updates every second
						//break;
					}while(mic.getAudioVolume() > THRESHOLD);
					System.out.println("Lestening Complete!");
					break;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error Occured");
				}
				finally{
					System.out.println("mic Closed");
					mic.close();//Makes sure microphone closes on exit.
				}
			}
		}
	}

}
