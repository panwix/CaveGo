package com.panwix.cavego.framework.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.panwix.cavego.framework.FileIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AndroidFileIO implements FileIO {
	Context context;
	AssetManager assets;
	String externalStoragePath;
	
	public AndroidFileIO(Context context){
		this.context = context;
		this.assets = context.getAssets();
		this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}
	@Override
	public InputStream readAsset(String fileName) throws IOException {
		// TODO Auto-generated method stub
		return assets.open(fileName);
	}

	@Override
	public InputStream readFile(String fileName) throws IOException {
		// TODO Auto-generated method stub
		return new FileInputStream(externalStoragePath + fileName);
	}

	@Override
	public OutputStream writeFile(String fileName) throws IOException {
		// TODO Auto-generated method stub
		return new FileOutputStream(externalStoragePath + fileName);
	}
	public SharedPreferences getPreference(){
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

}

