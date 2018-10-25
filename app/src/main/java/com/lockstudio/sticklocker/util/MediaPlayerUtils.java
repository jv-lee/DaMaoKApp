package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

public class MediaPlayerUtils {
	public static void play(Context mContext, String mediaPath) {
		AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		if (AudioManager.RINGER_MODE_SILENT == audioManager.getRingerMode() || AudioManager.RINGER_MODE_VIBRATE == audioManager.getRingerMode()) {
			return;
		}
		if (new File(mediaPath).exists()) {
			try {
				MediaPlayer mediaPlayer = MediaPlayer.create(mContext, Uri.parse(mediaPath));
				play(mContext, mediaPlayer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void play(Context mContext, int mediaRes) {
		AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		if (AudioManager.RINGER_MODE_SILENT == audioManager.getRingerMode() || AudioManager.RINGER_MODE_VIBRATE == audioManager.getRingerMode()) {
			return;
		}
		try {
			MediaPlayer mediaPlayer = MediaPlayer.create(mContext, mediaRes);
			play(mContext, mediaPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void play(Context mContext, MediaPlayer mediaPlayer) {
		if (mediaPlayer != null) {
			mediaPlayer.setVolume(0.5f, 0.5f);
			mediaPlayer.start();
		}

	}
}
