package com.genee.utils.tts;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class PollyDemo {

	private final AmazonPolly polly;
	private final Voice voice;
	private static final String SAMPLE = "Congratulations. You have successfully built this working demo  of Amazon Polly in Java. Have fun building voice enabled apps with Amazon";

	public PollyDemo(Region region) {
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAJ7LRLLSHJUAREC4A", "mDqzmy/ki2gjQTVZJktruPnBEWKafCu68k6kX5jM");
		polly =  AmazonPollyClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(region.getName()).build();
		
		DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

		// Synchronously ask Amazon Polly to describe available TTS voices.
		DescribeVoicesResult describeVoicesResult = polly.describeVoices(describeVoicesRequest);
		voice = describeVoicesResult.getVoices().get(0);
	}

	public InputStream synthesize(String text, OutputFormat format) throws IOException {
		SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId())
				.withOutputFormat(format);
		SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);

		return synthRes.getAudioStream();
	}

	public static void main(String args[]) throws Exception {
		// create the test class
		PollyDemo helloWorld = new PollyDemo(Region.getRegion(Regions.AP_SOUTH_1));
		// get the audio stream
		InputStream speechStream = helloWorld.synthesize(SAMPLE, OutputFormat.Mp3);

		// create an MP3 player
		AdvancedPlayer player = new AdvancedPlayer(speechStream,
				javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());

		player.setPlayBackListener(new PlaybackListener() {
			@Override
			public void playbackStarted(PlaybackEvent evt) {
				System.out.println("Playback started");
				System.out.println(SAMPLE);
			}

			@Override
			public void playbackFinished(PlaybackEvent evt) {
				System.out.println("Playback finished");
			}
		});

		// play it!
		player.play();

	}
}