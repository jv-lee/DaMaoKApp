package com.tommy.ad;

import android.os.Environment;

import java.io.File;

public class Env {

	public static  String ROOT_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator + "Android" + File.separator + "data";

}
