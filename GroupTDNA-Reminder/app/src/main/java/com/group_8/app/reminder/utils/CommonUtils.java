package com.group_8.app.reminder.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * 
 * @author Sea
 *
 */
public class CommonUtils {
	public static String TAG = "COMPA";
	public static String APP_PATH = Environment.getExternalStorageDirectory() + "/RecognizeTextOCR/";

	/**
	 * Clear temp image in folder APP_PATH
	 */
	public static void cleanFolder() {
		info("Create or empty folder");
		String datapath = APP_PATH;
		File tenpPath = new File(datapath);
		if (!tenpPath.exists()) {
			if (!tenpPath.mkdir()) {
				// Can not create path
			}
		} else {
			for (File child : tenpPath.listFiles()) {
				// Keep only config files
				if (!child.getName().contains(".txt")) {
					child.delete();
				}
			}
		}
	}

	public static void info(Object msg) {
		Log.i(TAG, msg.toString());
	}

}
