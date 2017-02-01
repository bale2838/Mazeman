package com.bale.mazeman.sound;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
	public static final Sound chomp = new Sound("/chomp.wav");
	public static final Sound theme = new Sound("/theme.wav");
	public static final Sound playerhurt = new Sound("/playerhurt.wav");
	public static final Sound death = new Sound("/death.wav");

	private AudioClip clip;
	Thread thread;

	private Sound(String name) {
		try {
			clip = Applet.newAudioClip(Sound.class.getResource(name));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void play() {
		try {
			new Thread() {
				public void run() {
					clip.play();
				}
			}.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void loop() {
		try {
			new Thread() {
				public void run() {
					clip.loop();
				}
			}.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		clip.stop();
	}
}